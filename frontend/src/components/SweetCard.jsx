"use client"

import { useContext } from "react"
import { motion } from "framer-motion"
import { CartContext } from "../context/CartContext"
import "./SweetCard.css"

const SweetCard = ({ sweet }) => {
  const { addToCart, removeFromCart, updateQuantity, cartItems } = useContext(CartContext)

  // Find the cart item for this sweet
  const cartItem = cartItems.find(item => item.id === sweet.id)
  const cartQuantity = cartItem ? cartItem.quantity : 0

  const handleAddToCart = () => {
    if (sweet.quantity > 0) {
      addToCart(sweet)
    }
  }

  const handleRemoveFromCart = () => {
    removeFromCart(sweet.id)
  }

  const handleUpdateQuantity = (newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(sweet.id)
    } else if (newQuantity <= sweet.quantity) {
      updateQuantity(sweet.id, newQuantity)
    }
  }

  // Check if we can add more items (considering current cart quantity + available stock)
  const canAddMore = cartQuantity < sweet.quantity
  const isOutOfStock = sweet.quantity === 0

  return (
    <motion.div
      className="sweet-card"
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      whileHover={{ y: -5 }}
    >
      <div className="sweet-image">
        <img src={sweet.image || "/placeholder.svg"} alt={sweet.name} />
        <div className="sweet-overlay">
          <motion.button
            className="quick-add-btn"
            onClick={handleAddToCart}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
          >
            Quick Add
          </motion.button>
        </div>
      </div>

      <div className="sweet-info">
        <div className="sweet-category">{sweet.category}</div>
        <h3 className="sweet-name">{sweet.name}</h3>
        <p className="sweet-description">{sweet.description}</p>

        <div className="sweet-footer">
          <div className="price-section">
            <span className="sweet-price">${sweet.price}</span>
            <span className="stock-info">Stock: {sweet.quantity}</span>
          </div>

          {cartQuantity > 0 ? (
            <div className="quantity-controls">
              <motion.button
                className="quantity-btn minus-btn"
                onClick={() => handleUpdateQuantity(cartQuantity - 1)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                -
              </motion.button>
              <div className="quantity-info">
                <span className="quantity-display">{cartQuantity}</span>
                {!canAddMore && (
                  <span className="stock-limit-message">Max reached</span>
                )}
              </div>
              <motion.button
                className={`quantity-btn plus-btn ${!canAddMore ? 'disabled' : ''}`}
                onClick={() => handleUpdateQuantity(cartQuantity + 1)}
                disabled={!canAddMore || isOutOfStock}
                whileHover={{ scale: canAddMore ? 1.05 : 1 }}
                whileTap={{ scale: canAddMore ? 0.95 : 1 }}
              >
                +
              </motion.button>
            </div>
          ) : (
            <motion.button
              className="add-to-cart-btn"
              onClick={handleAddToCart}
              disabled={isOutOfStock}
              whileHover={{ scale: !isOutOfStock ? 1.05 : 1 }}
              whileTap={{ scale: !isOutOfStock ? 0.95 : 1 }}
            >
              {isOutOfStock ? "Out of Stock" : "Add to Cart"}
            </motion.button>
          )}
        </div>
      </div>

      {sweet.quantity <= 5 && sweet.quantity > 0 && <div className="low-stock-badge">Only {sweet.quantity} left!</div>}
      {cartQuantity > 0 && <div className="in-cart-badge">In Cart</div>}
    </motion.div>
  )
}

export default SweetCard
