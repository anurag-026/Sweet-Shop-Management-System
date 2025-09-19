"use client";

import { useState, useContext, useEffect } from "react";
import { motion } from "framer-motion";
import { AuthContext } from "../context/AuthContext";
import { CartContext } from "../context/CartContext";
import { sweetService } from "../services/sweetService";
import "./AdminPanel.css";

const AdminPanel = () => {
  const { user } = useContext(AuthContext);
  const { sweets, setSweets } = useContext(CartContext);
  const [showAddForm, setShowAddForm] = useState(false);
  const [editingSweet, setEditingSweet] = useState(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [showEditModal, setShowEditModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [formData, setFormData] = useState({
    name: "",
    price: "",
    category: "",
    description: "",
    image: "",
    stock: "",
  });

  // Fetch sweets on component mount
  useEffect(() => {
    fetchSweets();
  }, []);

  const fetchSweets = async () => {
    try {
      setLoading(true);
      setError("");
      const data = await sweetService.getAllSweets();
      setSweets(data);
    } catch (err) {
      console.error("Error fetching sweets:", err);
      setError("Failed to load sweets. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // Redirect if not admin
  if (!user || user.role !== "ROLE_ADMIN") {
    return (
      <div className="admin-panel">
        <div className="access-denied">
          <h2>Access Denied</h2>
          <p>You need admin privileges to access this page.</p>
        </div>
      </div>
    );
  }

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError("");
      setSuccess("");

      const sweetData = {
        name: formData.name,
        category: formData.category,
        price: Number.parseFloat(formData.price),
        quantity: Number.parseInt(formData.stock),
        description: formData.description,
        image: formData.image,
      };

      if (editingSweet) {
        const updatedSweet = await sweetService.updateSweet(
          editingSweet.id,
          sweetData
        );
        setSweets(
          sweets.map((sweet) =>
            sweet.id === editingSweet.id ? updatedSweet : sweet
          )
        );
        setEditingSweet(null);
        setSuccess("Sweet updated successfully!");
      } else {
        const newSweet = await sweetService.createSweet(sweetData);
        setSweets([...sweets, newSweet]);
        setSuccess("Sweet created successfully!");
      }

      setFormData({
        name: "",
        price: "",
        category: "",
        description: "",
        image: "",
        stock: "",
      });
      setShowAddForm(false);

      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error saving sweet:", err);
      setError("Failed to save sweet. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (sweet) => {
    setEditingSweet(sweet);
    setFormData({
      name: sweet.name,
      price: sweet.price.toString(),
      category: sweet.category,
      description: sweet.description,
      image: sweet.image,
      stock: sweet.quantity.toString(), // Changed from stock to quantity
    });
    setShowEditModal(true);
  };

  const confirmEdit = async () => {
    if (!editingSweet) return;
    try {
      setLoading(true);
      setError("");
      setSuccess("");

      const sweetData = {
        name: formData.name,
        category: formData.category,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.stock),
        description: formData.description,
        image: formData.image,
      };

      const updatedSweet = await sweetService.updateSweet(editingSweet.id, sweetData);

      setSweets(
        sweets.map((sweet) => (sweet.id === editingSweet.id ? updatedSweet : sweet))
      );

      setShowEditModal(false);
      setEditingSweet(null);
      resetForm();
      setSuccess("Sweet updated successfully!");
      setTimeout(() => setSuccess(""), 3000);
    } catch (err) {
      console.error("Error updating sweet:", err);
      setError("Failed to update sweet. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      name: "",
      price: "",
      category: "",
      description: "",
      image: "",
      stock: "",
    });
  };

  const cancelEdit = () => {
    setShowEditModal(false);
    setEditingSweet(null);
    resetForm();
  };

  const handleDelete = (sweet) => {
    setDeleteTarget(sweet);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (deleteTarget) {
      try {
        setLoading(true);
        setError("");
        await sweetService.deleteSweet(deleteTarget.id);
        setSweets(sweets.filter((sweet) => sweet.id !== deleteTarget.id));
        setShowDeleteModal(false);
        setDeleteTarget(null);
        setSuccess("Sweet deleted successfully!");

        // Clear success message after 3 seconds
        setTimeout(() => setSuccess(""), 3000);
      } catch (err) {
        console.error("Error deleting sweet:", err);
        setError("Failed to delete sweet. Please try again.");
      } finally {
        setLoading(false);
      }
    }
  };

  const cancelDelete = () => {
    setShowDeleteModal(false);
    setDeleteTarget(null);
  };

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
              <select
                name="category"
                value={formData.category}
                onChange={handleInputChange}
                required
              >
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
            <motion.button
              type="submit"
              className="submit-btn"
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
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
                <p className="stock">Stock: {sweet.quantity}</p>
                <p className="category">{sweet.category}</p>
              </div>
              <div className="admin-actions">
                <button className="edit-btn" onClick={() => handleEdit(sweet)}>
                  Edit
                </button>
                <button
                  className="delete-btn"
                  onClick={() => handleDelete(sweet)}
                >
                  Delete
                </button>
              </div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* Custom Delete Confirmation Modal */}
      {showDeleteModal && (
        <motion.div
          className="modal-overlay"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <motion.div
            className="delete-modal"
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            transition={{ type: "spring", stiffness: 500, damping: 30 }}
          >
            <div className="modal-header">
              <h3>Confirm Delete</h3>
            </div>
            <div className="modal-content">
              {deleteTarget && (
                <>
                  <div className="product-preview">
                    <img
                      src={deleteTarget.image}
                      alt={deleteTarget.name}
                      className="product-image"
                    />
                    <div className="product-info">
                      <h4>{deleteTarget.name}</h4>
                      <p className="product-price">${deleteTarget.price}</p>
                      <p className="product-category">
                        {deleteTarget.category}
                      </p>
                    </div>
                  </div>
                  <p className="confirmation-message">
                    Are you sure you want to delete this sweet? This action
                    cannot be undone.
                  </p>
                </>
              )}
            </div>
            <div className="modal-actions">
              <motion.button
                className="cancel-btn"
                onClick={cancelDelete}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Cancel
              </motion.button>
              <motion.button
                className="confirm-delete-btn"
                onClick={confirmDelete}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Delete
              </motion.button>
            </div>
          </motion.div>
        </motion.div>
      )}

      {/* Custom Edit Modal */}
      {showEditModal && (
        <motion.div
          className="modal-overlay"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
        >
          <motion.div
            className="edit-modal"
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            transition={{ type: "spring", stiffness: 500, damping: 30 }}
          >
            <div className="modal-header edit-header">
              <h3>Edit Sweet</h3>
            </div>
            <div className="modal-content">
              {editingSweet && (
                <div className="edit-form">
                  <div className="form-row">
                    <label htmlFor="name">Name</label>
                    <input
                      type="text"
                      id="name"
                      name="name"
                      value={formData.name}
                      onChange={handleInputChange}
                      required
                    />
                  </div>

                  <div className="form-row">
                    <label htmlFor="price">Price ($)</label>
                    <input
                      type="number"
                      id="price"
                      name="price"
                      step="0.01"
                      min="0"
                      value={formData.price}
                      onChange={handleInputChange}
                      required
                    />
                  </div>

                  <div className="form-row">
                    <label htmlFor="category">Category</label>
                    <select
                      id="category"
                      name="category"
                      value={formData.category}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Select a category</option>
                      <option value="chocolate">Chocolate</option>
                      <option value="gummies">Gummies</option>
                      <option value="hard candy">Hard Candy</option>
                      <option value="fudge">Fudge</option>
                      <option value="caramel">Caramel</option>
                      <option value="truffles">Truffles</option>
                      <option value="macarons">Macarons</option>
                      <option value="brittle">Brittle</option>
                      <option value="lollipops">Lollipops</option>
                    </select>
                  </div>

                  <div className="form-row">
                    <label htmlFor="stock">Stock</label>
                    <input
                      type="number"
                      id="stock"
                      name="stock"
                      min="0"
                      value={formData.stock}
                      onChange={handleInputChange}
                      required
                    />
                  </div>

                  <div className="form-row">
                    <label htmlFor="description">Description</label>
                    <textarea
                      id="description"
                      name="description"
                      value={formData.description}
                      onChange={handleInputChange}
                      required
                    ></textarea>
                  </div>

                  <div className="form-row">
                    <label htmlFor="image">Image URL</label>
                    <input
                      type="text"
                      id="image"
                      name="image"
                      value={formData.image}
                      onChange={handleInputChange}
                      required
                    />
                  </div>

                  <div className="image-preview">
                    {formData.image && (
                      <img src={formData.image} alt="Sweet preview" />
                    )}
                  </div>
                </div>
              )}
            </div>
            <div className="modal-actions">
              <motion.button
                className="cancel-btn"
                onClick={cancelEdit}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Cancel
              </motion.button>
              <motion.button
                className="confirm-edit-btn"
                onClick={confirmEdit}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Save Changes
              </motion.button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </motion.div>
  );
};

export default AdminPanel;
