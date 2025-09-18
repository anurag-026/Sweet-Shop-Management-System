"use client"
import { motion } from "framer-motion"
import { Link, useNavigate, useLocation } from "react-router-dom"
import { useAuth } from "../context/AuthContext"
import { useCart } from "../context/CartContext"
import "./Header.css"

const Header = () => {
  const { user, logout } = useAuth()
  const { getTotalItems } = useCart()
  const navigate = useNavigate()
  const location = useLocation()
  
  const cartCount = getTotalItems()
  return (
    <motion.header
      className="header"
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      transition={{ duration: 0.6, ease: "easeOut" }}
    >
      <div className="container">
        <div className="header-content">
          <motion.div 
            className="logo" 
            whileHover={{ scale: 1.05 }} 
            transition={{ type: "spring", stiffness: 300 }}
            onClick={() => navigate("/dashboard")}
            style={{ cursor: "pointer" }}
          >
            <h1 className="logo-text">Sweet Delights</h1>
            <span className="logo-tagline">Premium Confectionery</span>
          </motion.div>

          <nav className="nav">
            <div className="nav-links">
              <Link 
                to="/dashboard" 
                className={`nav-link ${location.pathname === "/dashboard" ? "active" : ""}`}
              >
                Catalog
              </Link>
              <Link 
                to="/profile" 
                className={`nav-link ${location.pathname === "/profile" ? "active" : ""}`}
              >
                Profile
              </Link>
              <Link 
                to="/cart" 
                className={`nav-link ${location.pathname === "/cart" ? "active" : ""}`}
              >
                Cart
              </Link>
            </div>

            <div className="nav-actions">
              <motion.button
                className="cart-btn"
                onClick={() => navigate("/cart")}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                <span className="cart-icon">🛒</span>
                {cartCount > 0 && (
                  <motion.span
                    className="cart-count"
                    initial={{ scale: 0 }}
                    animate={{ scale: 1 }}
                    transition={{ type: "spring", stiffness: 500 }}
                  >
                    {cartCount}
                  </motion.span>
                )}
              </motion.button>

              {user ? (
                <div className="user-menu">
                  <span className="user-name">Hello, {user.name}!</span>
                  {user.role === "admin" && (
                    <button className="btn btn-secondary" onClick={() => navigate("/admin")}>
                      Admin Panel
                    </button>
                  )}
                  <button className="btn btn-primary" onClick={logout}>
                    Logout
                  </button>
                </div>
              ) : (
                <button className="btn btn-primary" onClick={() => navigate("/login")}>
                  Sign In
                </button>
              )}
            </div>
          </nav>
        </div>
      </div>
    </motion.header>
  )
}

export default Header
