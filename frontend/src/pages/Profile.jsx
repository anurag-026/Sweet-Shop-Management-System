"use client"

import { useState, useContext } from "react"
import { motion } from "framer-motion"
import { AuthContext } from "../context/AuthContext"
import "./Profile.css"

const Profile = () => {
  const { user, updateUser } = useContext(AuthContext)
  const [isEditing, setIsEditing] = useState(false)
  const [formData, setFormData] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phone: user?.phone || "",
    address: user?.address || "",
  })

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    updateUser(formData)
    setIsEditing(false)
  }

  const handleCancel = () => {
    setFormData({
      name: user?.name || "",
      email: user?.email || "",
      phone: user?.phone || "",
      address: user?.address || "",
    })
    setIsEditing(false)
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
          {!isEditing ? (
            <div className="profile-info">
              <div className="info-section">
                <h3>Personal Information</h3>
                <div className="info-grid">
                  <div className="info-item">
                    <label>Full Name</label>
                    <p>{user.name}</p>
                  </div>
                  <div className="info-item">
                    <label>Email</label>
                    <p>{user.email}</p>
                  </div>
                  <div className="info-item">
                    <label>Phone</label>
                    <p>{user.phone || "Not provided"}</p>
                  </div>
                  <div className="info-item">
                    <label>Address</label>
                    <p>{user.address || "Not provided"}</p>
                  </div>
                </div>
              </div>

              <motion.button
                className="edit-profile-btn"
                onClick={() => setIsEditing(true)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Edit Profile
              </motion.button>
            </div>
          ) : (
            <div className="edit-profile-form">
              <h3>Edit Profile</h3>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Full Name</label>
                  <input type="text" name="name" value={formData.name} onChange={handleInputChange} required />
                </div>

                <div className="form-group">
                  <label>Email</label>
                  <input type="email" name="email" value={formData.email} onChange={handleInputChange} required />
                </div>

                <div className="form-group">
                  <label>Phone</label>
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleInputChange}
                    placeholder="Enter your phone number"
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
                  />
                </div>

                <div className="form-actions">
                  <motion.button
                    type="submit"
                    className="save-btn"
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
                  >
                    Save Changes
                  </motion.button>
                  <motion.button
                    type="button"
                    className="cancel-btn"
                    onClick={handleCancel}
                    whileHover={{ scale: 1.05 }}
                    whileTap={{ scale: 0.95 }}
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
