// axiosInstance.js
import axios from "axios";
import { getRefreshToken } from "./refreshToken";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8081";

// Create an Axios instance
const axiosInstance = axios.create({
  baseURL: API_URL,
  withCredentials: true, // Ensures cookies are sent with requests
});

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

    // Check if the error response is 401 and the request hasn't been retried yet
    if (
      error.response &&
      error.response.status === 401 &&
      !originalRequest._retry
    ) {
      originalRequest._retry = true;

      const refreshSuccess = await getRefreshToken();
      if (refreshSuccess) {
        const newToken = localStorage.getItem("access_token");
        originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
        return axiosInstance(originalRequest); // Retry the original request
      } else {
        // If token refresh fails, clear storage and redirect to login
        localStorage.clear();
        window.location.href = "/login"; // Adjust the login path as needed
        return Promise.reject(error);
      }
    }

    // If the error is not 401 or the request has already been retried, reject the promise
    return Promise.reject(error);
  }
);

export default axiosInstance;
