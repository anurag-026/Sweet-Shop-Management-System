"use client"

import { useState, useContext } from "react"
import { motion } from "framer-motion"
import { AuthContext } from "../context/AuthContext"
import { CartContext } from "../context/CartContext"
import "./AdminPanel.css"

const AdminPanel = () => {
  const { user } = useContext(AuthContext)
  const { sweets, setSweets } = useContext(CartContext)
  const [showAddForm, setShowAddForm] = useState(false)
  const [editingSweet, setEditingSweet] = useState(null)
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    category: "",
    description: "",
    image: "",
    stock: "",
  })

  // Redirect if not admin
  if (!user || user.role !== "admin") {
    return (
      <div className="admin-panel">
        <div className="access-denied">
          <h2>Access Denied</h2>
          <p>You need admin privileges to access this page.</p>
        </div>
      </div>
    )
  }

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = (e) => {
    e.preventDefault()
    const newSweet = {
      ...formData,
      id: editingSweet ? editingSweet.id : Date.now(),
      price: Number.parseFloat(formData.price),
      stock: Number.parseInt(formData.stock),
    }

    if (editingSweet) {
      setSweets(sweets.map((sweet) => (sweet.id === editingSweet.id ? newSweet : sweet)))
      setEditingSweet(null)
    } else {
      setSweets([...sweets, newSweet])
    }

    setFormData({
      name: "",
      price: "",
      category: "",
      description: "",
      image: "",
      stock: "",
    })
    setShowAddForm(false)
  }

  const handleEdit = (sweet) => {
    setEditingSweet(sweet)
    setFormData({
      name: sweet.name,
      price: sweet.price.toString(),
      category: sweet.category,
      description: sweet.description,
      image: sweet.image,
      stock: sweet.stock.toString(),
    })
    setShowAddForm(true)
  }

  const handleDelete = (id) => {
    if (window.confirm("Are you sure you want to delete this sweet?")) {
      setSweets(sweets.filter((sweet) => sweet.id !== id))
    }
  }

  return (
    <motion.div
      className="admin-panel"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.6 }}
    >
      <div className="admin-header">
        <h1>Admin Panel</h1>
        <motion.button
          className="add-sweet-btn"
          onClick={() => setShowAddForm(!showAddForm)}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          {showAddForm ? "Cancel" : "Add New Sweet"}
        </motion.button>
      </div>

      {showAddForm && (
        <motion.div
          className="add-sweet-form"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.3 }}
        >
          <h2>{editingSweet ? "Edit Sweet" : "Add New Sweet"}</h2>
          <form onSubmit={handleSubmit}>
            <div className="form-row">
              <input
                type="text"
                name="name"
                placeholder="Sweet Name"
                value={formData.name}
                onChange={handleInputChange}
                required
              />
              <input
                type="number"
                name="price"
                placeholder="Price"
                value={formData.price}
                onChange={handleInputChange}
                step="0.01"
                required
              />
            </div>
            <div className="form-row">
              <select name="category" value={formData.category} onChange={handleInputChange} required>
                <option value="">Select Category</option>
                <option value="chocolate">Chocolate</option>
                <option value="candy">Candy</option>
                <option value="gummy">Gummy</option>
                <option value="cookies">Cookies</option>
                <option value="cakes">Cakes</option>
              </select>
              <input
                type="number"
                name="stock"
                placeholder="Stock Quantity"
                value={formData.stock}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="form-row">
              <textarea
                name="description"
                placeholder="Description"
                value={formData.description}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="form-row">
              <input
                type="url"
                name="image"
                placeholder="Image URL"
                value={formData.image}
                onChange={handleInputChange}
                required
              />
            </div>
            <motion.button type="submit" className="submit-btn" whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              {editingSweet ? "Update Sweet" : "Add Sweet"}
            </motion.button>
          </form>
        </motion.div>
      )}

      <div className="sweets-management">
        <h2>Manage Sweets</h2>
        <div className="sweets-grid">
          {sweets.map((sweet) => (
            <motion.div
              key={sweet.id}
              className="admin-sweet-card"
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 0.3 }}
            >
              <img src={sweet.image || "/placeholder.svg"} alt={sweet.name} />
              <div className="sweet-info">
                <h3>{sweet.name}</h3>
                <p className="price">${sweet.price}</p>
                <p className="stock">Stock: {sweet.stock}</p>
                <p className="category">{sweet.category}</p>
              </div>
              <div className="admin-actions">
                <button className="edit-btn" onClick={() => handleEdit(sweet)}>
                  Edit
                </button>
                <button className="delete-btn" onClick={() => handleDelete(sweet.id)}>
                  Delete
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </motion.div>
  )
}

export default AdminPanel
