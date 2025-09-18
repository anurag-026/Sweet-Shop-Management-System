"use client"

import { useState } from "react"
import { motion } from "framer-motion"
import { useAuth } from "../context/AuthContext"
import { useCart } from "../context/CartContext"
import SweetCard from "../components/SweetCard"
import SearchFilter from "../components/SearchFilter"
import { mockSweets } from "../data/mockData"
import "./Dashboard.css"

const Dashboard = () => {
  const { user } = useAuth()
  const { addToCart } = useCart()
  const [sweets, setSweets] = useState(mockSweets)
  const [filteredSweets, setFilteredSweets] = useState(mockSweets)
  const [loading, setLoading] = useState(false)
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedCategory, setSelectedCategory] = useState("")
  const [priceRange, setPriceRange] = useState({ min: "", max: "" })

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


  return (
    <motion.div
      className="dashboard-container"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      <div className="dashboard-header">
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
        className="dashboard-content"
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

export default Dashboard
