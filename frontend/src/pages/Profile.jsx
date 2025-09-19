"use client"

import { useState, useContext, useEffect } from "react"
import { motion } from "framer-motion"
import { AuthContext } from "../context/AuthContext"
import axiosInstance from "../services/axiosInstance"
import "./Profile.css"

const Profile = () => {
  const { user, updateUser } = useContext(AuthContext)
  const [isEditing, setIsEditing] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [profileData, setProfileData] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
    role: ""
  })
  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    address: "",
  })

  // Fetch profile data on component mount
  useEffect(() => {
    fetchProfileData()
  }, [])

  // Update form data when profile data changes
  useEffect(() => {
    setFormData({
      name: profileData.name || "",
      phone: profileData.phone || "",
      address: profileData.address || "",
    })
  }, [profileData])

  const fetchProfileData = async () => {
    try {
      setLoading(true)
      setError("")
      
      const response = await axiosInstance.get("/api/auth/profile")
      
      if (response.data) {
        setProfileData({
          name: response.data.name || "",
          email: response.data.email || "",
          phone: response.data.phone || "",
          address: response.data.address || "",
          role: response.data.role || ""
        })
      }
    } catch (err) {
      console.error("Error fetching profile:", err)
      if (err.response?.status === 401) {
        setError("Please log in again to view your profile")
      } else {
        setError("Failed to load profile data. Please try again.")
      }
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError("")
    setSuccess("")

    try {
      const response = await axiosInstance.patch("/api/auth/profile", {
        name: formData.name,
        phone: formData.phone,
        address: formData.address
      })

      if (response.data) {
        // Update local profile data
        setProfileData(prev => ({
          ...prev,
          name: response.data.name || formData.name,
          phone: response.data.phone || formData.phone,
          address: response.data.address || formData.address
        }))

        // Update auth context
        updateUser({
          name: response.data.name || formData.name,
          phone: response.data.phone || formData.phone,
          address: response.data.address || formData.address
        })

        setSuccess("Profile updated successfully!")
        setIsEditing(false)
        
        // Clear success message after 3 seconds
        setTimeout(() => setSuccess(""), 3000)
      }
    } catch (err) {
      console.error("Error updating profile:", err)
      
      if (err.response?.status === 401) {
        setError("Please log in again to update your profile")
      } else if (err.response?.status === 400) {
        setError(err.response.data?.message || "Invalid data. Please check your information.")
      } else if (err.response?.status === 415) {
        setError("Please ensure all fields are properly filled.")
      } else {
        setError("Failed to update profile. Please try again.")
      }
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = () => {
    setFormData({
      name: profileData.name || "",
      phone: profileData.phone || "",
      address: profileData.address || "",
    })
    setIsEditing(false)
    setError("")
    setSuccess("")
  }

  if (!user) {
    return (
      <div className="profile-page">
        <div className="not-logged-in">
          <h2>Please log in to view your profile</h2>
        </div>
      </div>
    )
  }

  if (loading && !isEditing) {
    return (
      <div className="profile-page">
        <div className="loading-container">
          <h2>Loading profile...</h2>
        </div>
      </div>
    )
  }

  return (
    <motion.div
      className="profile-page"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.6 }}
    >
      <div className="profile-container">
        <motion.div
          className="profile-header"
          initial={{ y: -20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ delay: 0.2, duration: 0.6 }}
        >
          <div className="profile-avatar">
            <div className="avatar-circle">{user.name.charAt(0).toUpperCase()}</div>
          </div>
          <h1>My Profile</h1>
          <p className="user-role">Role: {user.role}</p>
        </motion.div>

        <motion.div
          className="profile-content"
          initial={{ y: 20, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ delay: 0.4, duration: 0.6 }}
        >
          {/* Error and Success Messages */}
          {error && (
            <motion.div
              className="error-message"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              {error}
            </motion.div>
          )}
          
          {success && (
            <motion.div
              className="success-message"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
            >
              {success}
            </motion.div>
          )}

          {!isEditing ? (
            <div className="profile-info">
              <div className="info-section">
                <h3>Personal Information</h3>
                <div className="info-grid">
                  <div className="info-item">
                    <label>Full Name</label>
                    <p>{profileData.name || "Not provided"}</p>
                  </div>
                  <div className="info-item">
                    <label>Email</label>
                    <p>{profileData.email || "Not provided"}</p>
                  </div>
                  <div className="info-item">
                    <label>Phone</label>
                    <p>{profileData.phone || "Not provided"}</p>
                  </div>
                  <div className="info-item">
                    <label>Address</label>
                    <p>{profileData.address || "Not provided"}</p>
                  </div>
                </div>
              </div>

              <motion.button
                className="edit-profile-btn"
                onClick={() => setIsEditing(true)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                disabled={loading}
              >
                {loading ? "Loading..." : "Edit Profile"}
              </motion.button>
            </div>
          ) : (
            <div className="edit-profile-form">
              <h3>Edit Profile</h3>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Full Name *</label>
                  <input 
                    type="text" 
                    name="name" 
                    value={formData.name} 
                    onChange={handleInputChange} 
                    required 
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label>Email</label>
                  <input 
                    type="email" 
                    value={profileData.email} 
                    disabled 
                    className="disabled-field"
                    title="Email cannot be changed"
                  />
                  <small className="field-note">Email cannot be changed</small>
                </div>

                <div className="form-group">
                  <label>Phone</label>
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    placeholder="Enter your phone number"
                    disabled={loading}
                  />
                </div>

                <div className="form-group">
                  <label>Address</label>
                  <textarea
                    name="address"
                    value={formData.address}
                    onChange={handleInputChange}
                    placeholder="Enter your address"
                    rows="3"
                    disabled={loading}
                  />
                </div>

                <div className="form-actions">
                  <motion.button
                    type="submit"
                    className="save-btn"
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    disabled={loading}
                  >
                    {loading ? "Saving..." : "Save Changes"}
                  </motion.button>
                  <motion.button
                    type="button"
                    className="cancel-btn"
                    onClick={handleCancel}
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                    disabled={loading}
                  >
                    Cancel
                  </motion.button>
                </div>
              </form>
            </div>
          )}
        </motion.div>
      </div>
    </motion.div>
  )
}

export default Profile
