import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import "./PaymentModal.css"

const PaymentModal = ({ isOpen, onClose, onConfirm, totalAmount, loading }) => {
  const [selectedPaymentMode, setSelectedPaymentMode] = useState("")
  const [paymentDetails, setPaymentDetails] = useState({
    cardNumber: "",
    expiryDate: "",
    cvv: "",
    cardholderName: "",
    paypalEmail: ""
  })

  const paymentModes = [
    {
      id: "CREDIT_CARD",
      name: "Credit Card",
      icon: "💳",
      description: "Pay securely with your credit card"
    },
    {
      id: "PAYPAL",
      name: "PayPal",
      icon: "🅿️",
      description: "Pay with your PayPal account"
    }
  ]

  const handlePaymentModeSelect = (mode) => {
    setSelectedPaymentMode(mode)
    // Reset payment details when switching modes
    setPaymentDetails({
      cardNumber: "",
      expiryDate: "",
      cvv: "",
      cardholderName: "",
      paypalEmail: ""
    })
  }

  const handleInputChange = (field, value) => {
    setPaymentDetails(prev => ({
      ...prev,
      [field]: value
    }))
  }

  const handleConfirm = () => {
    if (!selectedPaymentMode) {
      alert("Please select a payment mode")
      return
    }

    // Basic validation
    if (selectedPaymentMode === "CREDIT_CARD") {
      if (!paymentDetails.cardNumber || !paymentDetails.expiryDate || 
          !paymentDetails.cvv || !paymentDetails.cardholderName) {
        alert("Please fill in all credit card details")
        return
      }
    } else if (selectedPaymentMode === "PAYPAL") {
      if (!paymentDetails.paypalEmail) {
        alert("Please enter your PayPal email")
        return
      }
    }

    onConfirm({
      paymentMode: selectedPaymentMode,
      paymentDetails: paymentDetails
    })
  }

  const formatCardNumber = (value) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '')
    const matches = v.match(/\d{4,16}/g)
    const match = matches && matches[0] || ''
    const parts = []
    for (let i = 0, len = match.length; i < len; i += 4) {
      parts.push(match.substring(i, i + 4))
    }
    if (parts.length) {
      return parts.join(' ')
    } else {
      return v
    }
  }

  const formatExpiryDate = (value) => {
    const v = value.replace(/\s+/g, '').replace(/[^0-9]/gi, '')
    if (v.length >= 2) {
      return v.substring(0, 2) + '/' + v.substring(2, 4)
    }
    return v
  }

  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          className="payment-modal-overlay"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onClose}
        >
          <motion.div
            className="payment-modal"
            initial={{ opacity: 0, scale: 0.9, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.9, y: 20 }}
            onClick={(e) => e.stopPropagation()}
          >
            <div className="payment-modal-header">
              <h2>Choose Payment Method</h2>
              <button className="close-btn" onClick={onClose}>×</button>
            </div>

            <div className="payment-modal-content">
              <div className="order-summary">
                <h3>Order Total: ${totalAmount.toFixed(2)}</h3>
              </div>

              <div className="payment-modes">
                <h4>Select Payment Method</h4>
                <div className="payment-modes-grid">
                  {paymentModes.map((mode) => (
                    <motion.div
                      key={mode.id}
                      className={`payment-mode-card ${selectedPaymentMode === mode.id ? 'selected' : ''}`}
                      onClick={() => handlePaymentModeSelect(mode.id)}
                      whileHover={{ scale: 1.02 }}
                      whileTap={{ scale: 0.98 }}
                    >
                      <div className="payment-mode-icon">{mode.icon}</div>
                      <div className="payment-mode-info">
                        <h5>{mode.name}</h5>
                        <p>{mode.description}</p>
                      </div>
                    </motion.div>
                  ))}
                </div>
              </div>

              {selectedPaymentMode && (
                <motion.div
                  className="payment-details"
                  initial={{ opacity: 0, height: 0 }}
                  animate={{ opacity: 1, height: "auto" }}
                  transition={{ duration: 0.3 }}
                >
                  {selectedPaymentMode === "CREDIT_CARD" && (
                    <div className="credit-card-form">
                      <h4>Credit Card Details</h4>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Cardholder Name</label>
                          <input
                            type="text"
                            placeholder="John Doe"
                            value={paymentDetails.cardholderName}
                            onChange={(e) => handleInputChange("cardholderName", e.target.value)}
                          />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Card Number</label>
                          <input
                            type="text"
                            placeholder="1234 5678 9012 3456"
                            value={paymentDetails.cardNumber}
                            onChange={(e) => handleInputChange("cardNumber", formatCardNumber(e.target.value))}
                            maxLength={19}
                          />
                        </div>
                      </div>
                      <div className="form-row">
                        <div className="form-group">
                          <label>Expiry Date</label>
                          <input
                            type="text"
                            placeholder="MM/YY"
                            value={paymentDetails.expiryDate}
                            onChange={(e) => handleInputChange("expiryDate", formatExpiryDate(e.target.value))}
                            maxLength={5}
                          />
                        </div>
                        <div className="form-group">
                          <label>CVV</label>
                          <input
                            type="text"
                            placeholder="123"
                            value={paymentDetails.cvv}
                            onChange={(e) => handleInputChange("cvv", e.target.value.replace(/\D/g, ''))}
                            maxLength={4}
                          />
                        </div>
                      </div>
                    </div>
                  )}

                  {selectedPaymentMode === "PAYPAL" && (
                    <div className="paypal-form">
                      <h4>PayPal Details</h4>
                      <div className="form-group">
                        <label>PayPal Email</label>
                        <input
                          type="email"
                          placeholder="your.email@example.com"
                          value={paymentDetails.paypalEmail}
                          onChange={(e) => handleInputChange("paypalEmail", e.target.value)}
                        />
                      </div>
                    </div>
                  )}
                </motion.div>
              )}
            </div>

            <div className="payment-modal-actions">
              <button className="cancel-btn" onClick={onClose} disabled={loading}>
                Cancel
              </button>
              <button 
                className="confirm-btn" 
                onClick={handleConfirm}
                disabled={loading || !selectedPaymentMode}
              >
                {loading ? "Processing..." : `Pay $${totalAmount.toFixed(2)}`}
              </button>
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

export default PaymentModal
