// refreshToken.js
import axiosInstance from "./axiosInstance";

// Function to refresh the token
export const getRefreshToken = async () => {
  try {
    const token = localStorage.getItem("access_token");
    if (!token) {
      return false;
    }

    const response = await axiosInstance.post("/api/auth/refresh", {}, {
      headers: {
        "Authorization": `Bearer ${token}`
      }
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