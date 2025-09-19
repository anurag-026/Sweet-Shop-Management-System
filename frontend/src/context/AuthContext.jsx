"use client";

import { createContext, useContext, useState, useEffect } from "react";
import axiosInstance from "../services/axiosInstance";

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if user is logged in on app start
    const savedUser = localStorage.getItem("user");
    if (savedUser) {
      const userData = JSON.parse(savedUser);
      setUser(userData);
      setIsAuthenticated(true);
    }
    setLoading(false);
  }, []);

  const login = async (email, password, userData = null) => {
    try {
      // If userData is provided (from API), use it directly
      if (userData) {
        setUser(userData);
        setIsAuthenticated(true);
        localStorage.setItem("user", JSON.stringify(userData));
        return true;
      }

      // If no userData provided, return false (should use API login)
      console.warn("No userData provided to login function. Use API login instead.");
      return false;
    } catch (error) {
      console.error("Login error:", error);
      return false;
    }
  };

  const register = async (name, email, password, userData = null) => {
    try {
      // If userData is provided (from API), use it directly
      if (userData) {
        setUser(userData);
        setIsAuthenticated(true);
        localStorage.setItem("user", JSON.stringify(userData));
        return true;
      }

      // If no userData provided, return false (should use API registration)
      console.warn("No userData provided to register function. Use API registration instead.");
      return false;
    } catch (error) {
      console.error("Registration error:", error);
      return false;
    }
  };

  const logout = async () => {
    try {
      // Call API logout endpoint if token exists
      const token = localStorage.getItem("access_token");
      if (token) {
        try {
          await axiosInstance.post(
            "/api/auth/logout",
            {},
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
        } catch (error) {
          console.error("Logout API call failed:", error);
          // Continue with local logout even if API call fails
        }
      }
    } catch (error) {
      console.error("Logout error:", error);
    } finally {
      // Always clear local state and storage
      setUser(null);
      setIsAuthenticated(false);
      localStorage.removeItem("user");
      localStorage.removeItem("access_token");
    }
  };

  const updateUser = (updatedData) => {
    const updatedUser = { ...user, ...updatedData };
    setUser(updatedUser);
    localStorage.setItem("user", JSON.stringify(updatedUser));
  };

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    register,
    logout,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export { AuthContext };
