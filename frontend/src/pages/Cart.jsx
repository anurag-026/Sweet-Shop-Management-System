"use client"
import { useState, useEffect } from "react"
import { motion } from "framer-motion"
import { useCart } from "../context/CartContext"
import CartItem from "../components/CartItem"
import PaymentModal from "../components/PaymentModal"
import { cartService } from "../services/cartService"
import { orderService } from "../services/orderService"
import "./Cart.css"

const Cart = () => {
  const { cartItems, getTotalPrice, clearCart } = useCart()
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [showPaymentModal, setShowPaymentModal] = useState(false)

  const handleCheckout = () => {
    if (cartItems.length === 0) return
    setShowPaymentModal(true)
  }

  const handlePaymentConfirm = async (paymentData) => {
    try {
      setLoading(true)
      setError("")
      setSuccess("")
      
      const order = await orderService.checkoutWithPayment(paymentData)
      setSuccess("Checkout successful! Thank you for your purchase.")
      clearCart()
      setShowPaymentModal(false)
      
      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(""), 3000)
    } catch (err) {
      console.error('Error during checkout:', err)
      setError("Checkout failed. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  const handlePaymentCancel = () => {
    setShowPaymentModal(false)
  }

  return (
    <motion.div
      className="cart-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      <div className="cart-content">
        <motion.div
          className="cart-header"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <h1>Shopping Cart</h1>
          <p>
            {cartItems.length} {cartItems.length === 1 ? "item" : "items"} in your cart
          </p>
        </motion.div>

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

        {cartItems.length === 0 ? (
          <motion.div
            className="empty-cart"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <div className="empty-cart-icon">🛒</div>
            <h2>Your cart is empty</h2>
            <p>Add some delicious sweets to get started!</p>
          </motion.div>
        ) : (
          <motion.div
            className="cart-items-container"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 }}
          >
            <div className="cart-items">
              {cartItems.map((item, index) => (
                <motion.div
                  key={`${item.id}-${index}`}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <CartItem item={item} />
                </motion.div>
              ))}
            </div>

            <motion.div
              className="cart-summary"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 }}
            >
              <div className="summary-card">
                <h3>Order Summary</h3>
                <div className="summary-row">
                  <span>Subtotal:</span>
                  <span>${getTotalPrice().toFixed(2)}</span>
                </div>
                <div className="summary-row">
                  <span>Shipping:</span>
                  <span>Free</span>
                </div>
                <div className="summary-row total">
                  <span>Total:</span>
                  <span>${getTotalPrice().toFixed(2)}</span>
                </div>

                <div className="cart-actions">
                  <motion.button
                    className="clear-cart-btn"
                    onClick={clearCart}
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                  >
                    Clear Cart
                  </motion.button>
                  <motion.button
                    className="checkout-btn"
                    onClick={handleCheckout}
                    whileHover={{ scale: 1.02 }}
                    whileTap={{ scale: 0.98 }}
                    disabled={loading}
                  >
                    {loading ? "Processing..." : "Checkout"}
                  </motion.button>
                </div>
              </div>
            </motion.div>
          </motion.div>
        )}

        {/* Payment Modal */}
        <PaymentModal
          isOpen={showPaymentModal}
          onClose={handlePaymentCancel}
          onConfirm={handlePaymentConfirm}
          totalAmount={getTotalPrice()}
          loading={loading}
        />
      </div>
    </motion.div>
  )
}

export default Cart
