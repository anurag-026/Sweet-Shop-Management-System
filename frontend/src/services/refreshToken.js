// refreshToken.js
import axios from "axios";

const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8081";

// Function to refresh the token
export const getRefreshToken = async () => {
  try {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return false;
    }

    // Use a plain axios client to avoid interceptors and retry loops
    const response = await axios.post(`${API_URL}/api/auth/refresh`, {}, {
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      },
      withCredentials: false // Disable credentials for refresh token call
    });

    if (response.data && response.data.token) {
      localStorage.setItem("access_token", response.data.token);
      return true;
    }
    return false;
  } catch (error) {
    console.error("Error refreshing token:", error);
    return false;
  }
};