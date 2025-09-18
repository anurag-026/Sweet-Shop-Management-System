"use client"

import { createContext, useContext, useState, useEffect } from "react"
import { mockUsers } from "../data/mockData"

const AuthContext = createContext()

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [loading, setLoading] = useState(true)

  // Using mockUsers imported from mockData.js

  useEffect(() => {
    // Check if user is logged in on app start
    const savedUser = localStorage.getItem("user")
    if (savedUser) {
      const userData = JSON.parse(savedUser)
      setUser(userData)
      setIsAuthenticated(true)
    }
    setLoading(false)
  }, [])

  const login = async (email, password) => {
    try {
      // Simulate API call
      const foundUser = mockUsers.find((u) => u.email === email && u.password === password)

      if (foundUser) {
        const { password: _, ...userWithoutPassword } = foundUser
        setUser(userWithoutPassword)
        setIsAuthenticated(true)
        localStorage.setItem("user", JSON.stringify(userWithoutPassword))
        return true
      }
      return false
    } catch (error) {
      console.error("Login error:", error)
      return false
    }
  }

  const register = async (name, email, password) => {
    try {
      // Check if user already exists
      const existingUser = mockUsers.find((u) => u.email === email)
      if (existingUser) {
        return false
      }

      // Create new user
      const newUser = {
        id: mockUsers.length + 1,
        name,
        email,
        role: "user",
        phone: "",
        address: "",
      }

      // Add to mock users (in real app, this would be API call)
      mockUsers.push({ ...newUser, password })

      setUser(newUser)
      setIsAuthenticated(true)
      localStorage.setItem("user", JSON.stringify(newUser))
      return true
    } catch (error) {
      console.error("Registration error:", error)
      return false
    }
  }

  const logout = () => {
    setUser(null)
    setIsAuthenticated(false)
    localStorage.removeItem("user")
  }

  const updateUser = (updatedData) => {
    const updatedUser = { ...user, ...updatedData }
    setUser(updatedUser)
    localStorage.setItem("user", JSON.stringify(updatedUser))
  }

  const value = {
    user,
    isAuthenticated,
    loading,
    login,
    register,
    logout,
    updateUser,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export { AuthContext }
