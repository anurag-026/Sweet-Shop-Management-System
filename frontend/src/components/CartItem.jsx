"use client"

import { useContext } from "react"
import { motion } from "framer-motion"
import { CartContext } from "../context/CartContext"
import "./CartItem.css"

const CartItem = ({ item }) => {
  const { updateQuantity, removeFromCart } = useContext(CartContext)

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(item.id)
    } else {
      updateQuantity(item.id, newQuantity)
    }
  }

  return (
    <motion.div
      className="cart-item"
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 20 }}
      transition={{ duration: 0.3 }}
    >
      <div className="item-image">
        <img src={item.image || "/placeholder.svg"} alt={item.name} />
      </div>

      <div className="item-details">
        <h3>{item.name}</h3>
        <p className="item-category">{item.category}</p>
        <p className="item-price">${item.price}</p>
      </div>

      <div className="quantity-controls">
        <motion.button
          className="quantity-btn"
          onClick={() => handleQuantityChange(item.quantity - 1)}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          -
        </motion.button>
        <span className="quantity">{item.quantity}</span>
        <motion.button
          className="quantity-btn"
          onClick={() => handleQuantityChange(item.quantity + 1)}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          +
        </motion.button>
      </div>

      <div className="item-total">
        <p className="total-price">${(item.price * item.quantity).toFixed(2)}</p>
        <motion.button
          className="remove-btn"
          onClick={() => removeFromCart(item.id)}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          Remove
        </motion.button>
      </div>
    </motion.div>
  )
}

export default CartItem
