"use client"

import { useState, useEffect } from "react"
import { motion } from "framer-motion"
import { useAuth } from "../context/AuthContext"
import { useCart } from "../context/CartContext"
import SweetCard from "../components/SweetCard"
import SearchFilter from "../components/SearchFilter"
import { sweetService } from "../services/sweetService"
import "./Catalog.css"

const Catalog = () => {
  const { user } = useAuth()
  const { addToCart } = useCart()
  const [sweets, setSweets] = useState([])
  const [filteredSweets, setFilteredSweets] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedCategory, setSelectedCategory] = useState("")
  const [priceRange, setPriceRange] = useState({ min: "", max: "" })

  // Fetch sweets from API on component mount
  useEffect(() => {
    fetchSweets()
  }, [])

  const fetchSweets = async () => {
    try {
      setLoading(true)
      setError("")
      const sweetsData = await sweetService.getAllSweets()
      setSweets(sweetsData)
      setFilteredSweets(sweetsData)
    } catch (err) {
      console.error("Error fetching sweets:", err)
      setError("Failed to load sweets. Please try again.")
      setSweets([])
      setFilteredSweets([])
    } finally {
      setLoading(false)
    }
  }

  const applyFilters = () => {
    let filtered = sweets

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(
        (sweet) =>
          sweet.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
          sweet.description.toLowerCase().includes(searchTerm.toLowerCase()),
      )
    }

    // Filter by category
    if (selectedCategory) {
      filtered = filtered.filter((sweet) => sweet.category === selectedCategory)
    }

    // Filter by price range
    if (priceRange.min || priceRange.max) {
      filtered = filtered.filter((sweet) => {
        const min = priceRange.min ? parseFloat(priceRange.min) : 0
        const max = priceRange.max ? parseFloat(priceRange.max) : Infinity
        return sweet.price >= min && sweet.price <= max
      })
    }

    setFilteredSweets(filtered)
  }

  const handleSearch = (term) => {
    setSearchTerm(term)
    // Apply filters immediately when search changes
    setTimeout(() => {
      let filtered = sweets
      if (term) {
        filtered = filtered.filter(
          (sweet) =>
            sweet.name.toLowerCase().includes(term.toLowerCase()) ||
            sweet.description.toLowerCase().includes(term.toLowerCase()),
        )
      }
      if (selectedCategory) {
        filtered = filtered.filter((sweet) => sweet.category === selectedCategory)
      }
      if (priceRange.min || priceRange.max) {
        filtered = filtered.filter((sweet) => {
          const min = priceRange.min ? parseFloat(priceRange.min) : 0
          const max = priceRange.max ? parseFloat(priceRange.max) : Infinity
          return sweet.price >= min && sweet.price <= max
        })
      }
      setFilteredSweets(filtered)
    }, 0)
  }

  const handleFilter = (filters) => {
    setSelectedCategory(filters.category || "")
    setPriceRange(filters.priceRange || { min: "", max: "" })
    // Apply filters immediately
    setTimeout(() => {
      let filtered = sweets
      if (searchTerm) {
        filtered = filtered.filter(
          (sweet) =>
            sweet.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
            sweet.description.toLowerCase().includes(searchTerm.toLowerCase()),
        )
      }
      if (filters.category) {
        filtered = filtered.filter((sweet) => sweet.category === filters.category)
      }
      if (filters.priceRange && (filters.priceRange.min || filters.priceRange.max)) {
        filtered = filtered.filter((sweet) => {
          const min = filters.priceRange.min ? parseFloat(filters.priceRange.min) : 0
          const max = filters.priceRange.max ? parseFloat(filters.priceRange.max) : Infinity
          return sweet.price >= min && sweet.price <= max
        })
      }
      setFilteredSweets(filtered)
    }, 0)
  }

  const handleSort = (sortBy) => {
    let sorted = [...filteredSweets]
    
    switch (sortBy) {
      case "name":
        sorted.sort((a, b) => a.name.localeCompare(b.name))
        break
      case "name-desc":
        sorted.sort((a, b) => b.name.localeCompare(a.name))
        break
      case "price-asc":
        sorted.sort((a, b) => a.price - b.price)
        break
      case "price-desc":
        sorted.sort((a, b) => b.price - a.price)
        break
      case "category":
        sorted.sort((a, b) => a.category.localeCompare(b.category))
        break
      default:
        break
    }
    
    setFilteredSweets(sorted)
  }


  // Show loading state
  if (loading) {
    return (
      <motion.div
        className="catalog-container"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <div className="catalog-header">
          <motion.div
            className="welcome-section"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <h1>Welcome back, {user?.name}!</h1>
            <p>Loading our delicious collection of sweets...</p>
          </motion.div>
        </div>
        <div className="loading-state">
          <div className="loading-spinner"></div>
          <h2>Loading Sweets...</h2>
          <p>Fetching fresh inventory from our kitchen</p>
        </div>
      </motion.div>
    )
  }

  // Show error state
  if (error) {
    return (
      <motion.div
        className="catalog-container"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <div className="catalog-header">
          <motion.div
            className="welcome-section"
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
          >
            <h1>Welcome back, {user?.name}!</h1>
            <p>Discover our delicious collection of sweets</p>
          </motion.div>
        </div>
        <div className="error-state">
          <h2>Oops! Something went wrong</h2>
          <p>{error}</p>
          <button onClick={fetchSweets} className="retry-btn">
            Try Again
          </button>
        </div>
      </motion.div>
    )
  }

  return (
    <motion.div
      className="catalog-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      <div className="catalog-header">
        <motion.div
          className="welcome-section"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <h1>Welcome back, {user?.name}!</h1>
          <p>Discover our delicious collection of sweets</p>
        </motion.div>
      </div>

      <motion.div
        className="catalog-content"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <SearchFilter onSearch={handleSearch} onFilter={handleFilter} onSort={handleSort} />

        <div className="sweets-grid">
          {filteredSweets.length > 0 ? (
            filteredSweets.map((sweet, index) => (
              <motion.div
                key={sweet.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <SweetCard sweet={sweet} />
              </motion.div>
            ))
          ) : (
            <motion.div className="no-results" initial={{ opacity: 0 }} animate={{ opacity: 1 }}>
              <h3>No sweets found</h3>
              <p>Try adjusting your search criteria</p>
            </motion.div>
          )}
        </div>
      </motion.div>
    </motion.div>
  )
}

export default Catalog
