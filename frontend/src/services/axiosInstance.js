// axiosInstance.js
import axios from "axios";
import { getRefreshToken } from "./refreshToken";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8081";

// Create an Axios instance
const axiosInstance = axios.create({
  baseURL: API_URL,
  withCredentials: true, // Ensures cookies are sent with requests
});

// Maximum number of retries for any failed API (initial try + 2 retries = 3 total attempts)
const MAX_RETRIES = 2;

// Simple exponential backoff delay
function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// Request interceptor to add the Authorization header to each request
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle 401 Unauthorized errors
axiosInstance.interceptors.response.use(
  (response) => {
    // If the response is successful, simply return it
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const status = error?.response?.status;
    const requestUrl = originalRequest?.url || "";

    // Never intercept the refresh token call itself
    if (requestUrl && requestUrl.includes("/api/auth/refresh")) {
      return Promise.reject(error);
    }

    // Handle 401: try refresh once per original request
    if (status === 401 && !originalRequest._refreshTried) {
      originalRequest._refreshTried = true;

      const refreshSuccess = await getRefreshToken();
      if (refreshSuccess) {
        const newToken = localStorage.getItem("access_token");
        if (newToken) {
          originalRequest.headers = originalRequest.headers || {};
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        }
        return axiosInstance(originalRequest);
      }

      // Refresh failed: clear and redirect, do not retry further
      localStorage.clear();
      window.location.href = "/login";
      return Promise.reject(error);
    }

    // Generic capped retry for transient failures (network errors, 5xx, timeouts)
    const shouldRetry = !status || (status >= 500 && status < 600);
    originalRequest._retryCount = originalRequest._retryCount || 0;

    if (shouldRetry && originalRequest._retryCount < MAX_RETRIES) {
      originalRequest._retryCount += 1;
      const backoffMs = 300 * Math.pow(2, originalRequest._retryCount - 1); // 300ms, 600ms
      await delay(backoffMs);
      return axiosInstance(originalRequest);
    }

    // Give up
    return Promise.reject(error);
  }
);

export default axiosInstance;
