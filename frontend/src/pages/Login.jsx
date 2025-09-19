"use client";

import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { useAuth } from "../context/AuthContext";
import "./Login.css";
import axiosInstance from "../services/axiosInstance";

const Login = () => {
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    try {
      // Make API call to login endpoint
      const response = await axiosInstance.post("/api/auth/login", {
        email: formData.email,
        password: formData.password
      });

      if (response.data && response.data.token) {
        // Store token and user data
        localStorage.setItem("access_token", response.data.token);
        
        // Update auth context with user data
        const userData = {
          email: response.data.email,
          role: response.data.role,
          name: response.data.email.split('@')[0] // Extract name from email for now
        };
        
        // Call the login function from context to update state
        await login(formData.email, formData.password, userData);
        
        navigate("/dashboard");
      } else {
        setError("Invalid response from server");
      }
    } catch (err) {
      console.error("Login error:", err);
      
      // Handle different error types
      if (err.response) {
        // Server responded with error status
        if (err.response.status === 401) {
          setError("Invalid email or password");
        } else if (err.response.status === 400) {
          setError(err.response.data.message || "Invalid request");
        } else {
          setError(err.response.data.message || "Login failed. Please try again.");
        }
      } else if (err.request) {
        // Network error
        setError("Network error. Please check your connection.");
      } else {
        // Other error
        setError("Login failed. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="sd-login-container"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.5 }}
    >
      <div className="sd-login-card">
        <motion.div
          className="sd-login-header"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
        >
          <h1>Welcome Back</h1>
          <p>Sign in to your sweet account</p>
        </motion.div>

        <motion.form
          onSubmit={handleSubmit}
          className="sd-login-form"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
        >
          {error && (
            <motion.div
              className="sd-login-error"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              {error}
            </motion.div>
          )}

          <div className="sd-login-form-group">
            <label htmlFor="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              placeholder="Enter your email"
            />
          </div>

          <div className="sd-login-form-group">
            <label htmlFor="password">Password</label>
            <div className="sd-login-password-container">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
                required
                placeholder="Enter your password"
              />
              <button
                type="button"
                className="sd-login-password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <motion.button
            type="submit"
            className="sd-login-submit-btn"
            disabled={loading}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            {loading ? "Signing In..." : "Sign In"}
          </motion.button>
        </motion.form>

        <motion.div
          className="sd-login-footer"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4 }}
        >
          <p>
            Don't have an account? <Link to="/register">Sign up</Link>
          </p>
        </motion.div>
      </div>
    </motion.div>
  );
};

export default Login;
