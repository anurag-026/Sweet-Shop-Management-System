"use client"

import { useState } from "react"
import { Link, useNavigate } from "react-router-dom"
import { motion } from "framer-motion"
import { useAuth } from "../context/AuthContext"
import axiosInstance from "../services/axiosInstance"
import "./Register.css"

const Register = () => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  })
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const [showConfirmPassword, setShowConfirmPassword] = useState(false)
  const { register } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError("")

    // Client-side validation
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match")
      setLoading(false)
      return
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters long")
      setLoading(false)
      return
    }

    try {
      // Make API call to register endpoint
      const response = await axiosInstance.post("/api/auth/register", {
        fullName: formData.name,
        email: formData.email,
        password: formData.password
      })

      if (response.data && response.data.includes("successfully")) {
        // Registration successful, now login the user
        try {
          const loginResponse = await axiosInstance.post("/api/auth/login", {
            email: formData.email,
            password: formData.password
          })

          if (loginResponse.data && loginResponse.data.token) {
            // Store token and user data
            localStorage.setItem("access_token", loginResponse.data.token)
            
            // Update auth context with user data
            const userData = {
              email: loginResponse.data.email,
              role: loginResponse.data.role,
              name: formData.name
            }
            
            // Call the register function from context to update state
            await register(formData.name, formData.email, formData.password, userData)
            
            navigate("/dashboard")
          } else {
            setError("Registration successful but login failed. Please try logging in manually.")
          }
        } catch (loginError) {
          console.error("Auto-login after registration failed:", loginError)
          setError("Registration successful! Please login with your credentials.")
          navigate("/login")
        }
      } else {
        setError("Registration failed. Please try again.")
      }
    } catch (err) {
      console.error("Registration error:", err)
      
      // Handle different error types
      if (err.response) {
        // Server responded with error status
        if (err.response.status === 400) {
          if (err.response.data && err.response.data.includes("already registered")) {
            setError("Email is already registered. Please use a different email or try logging in.")
          } else {
            setError(err.response.data || "Invalid registration data. Please check your information.")
          }
        } else if (err.response.status === 422) {
          setError("Please check your email format and ensure password meets requirements.")
        } else {
          setError(err.response.data || "Registration failed. Please try again.")
        }
      } else if (err.request) {
        // Network error
        setError("Network error. Please check your connection and try again.")
      } else {
        // Other error
        setError("Registration failed. Please try again.")
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <motion.div
      className="sd-register-container"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      transition={{ duration: 0.5 }}
    >
      <div className="sd-register-card">
        <motion.div
          className="sd-register-header"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.2 }}
        >
          <h1>Join Sweet Store</h1>
          <p>Create your account to start shopping</p>
        </motion.div>

        <motion.form
          onSubmit={handleSubmit}
          className="sd-register-form"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.3 }}
        >
          {error && (
            <motion.div
              className="sd-register-error"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              {error}
            </motion.div>
          )}

          <div className="sd-register-form-group">
            <label htmlFor="name">Full Name</label>
            <input
              type="text"
              id="name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              placeholder="Enter your full name"
            />
          </div>

          <div className="sd-register-form-group">
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

          <div className="sd-register-form-group">
            <label htmlFor="password">Password</label>
            <div className="sd-register-password-container">
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
                className="sd-register-password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <div className="sd-register-form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <div className="sd-register-password-container">
              <input
                type={showConfirmPassword ? "text" : "password"}
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleChange}
                required
                placeholder="Confirm your password"
              />
              <button 
                type="button" 
                className="sd-register-password-toggle"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
              >
                {showConfirmPassword ? "👁️" : "👁️‍🗨️"}
              </button>
            </div>
          </div>

          <motion.button
            type="submit"
            className="sd-register-submit-btn"
            disabled={loading}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
          >
            {loading ? "Creating Account..." : "Create Account"}
          </motion.button>
        </motion.form>

        <motion.div
          className="sd-register-footer"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.4 }}
        >
          <p>
            Already have an account? <Link to="/login">Sign in</Link>
          </p>
        </motion.div>
      </div>
    </motion.div>
  )
}

export default Register
