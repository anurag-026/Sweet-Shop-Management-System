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

  // Fetch sweets on component mount
  useEffect(() => {
    fetchSweets()
  }, [])

  const fetchSweets = async () => {
    try {
      setLoading(true)
      setError("")
      const data = await sweetService.getAllSweets()
      setSweets(data)
      setFilteredSweets(data)
    } catch (err) {
      console.error('Error fetching sweets:', err)
      setError("Failed to load sweets. Please try again.")
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

  const handleSearch = async (term) => {
    setSearchTerm(term)
    try {
      setLoading(true)
      const filters = {
        name: term,
        category: selectedCategory,
        min: priceRange.min,
        max: priceRange.max
      }
      const data = await sweetService.getAllSweets(filters)
      setFilteredSweets(data)
    } catch (err) {
      console.error('Error searching sweets:', err)
      setError("Failed to search sweets. Please try again.")
    } finally {
      setLoading(false)
    }
  }

  const handleFilter = async (filters) => {
    setSelectedCategory(filters.category || "")
    setPriceRange(filters.priceRange || { min: "", max: "" })
    try {
      setLoading(true)
      const searchFilters = {
        name: searchTerm,
        category: filters.category,
        min: filters.priceRange?.min,
        max: filters.priceRange?.max
      }
      const data = await sweetService.getAllSweets(searchFilters)
      setFilteredSweets(data)
    } catch (err) {
      console.error('Error filtering sweets:', err)
      setError("Failed to filter sweets. Please try again.")
    } finally {
      setLoading(false)
    }
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


  if (loading && sweets.length === 0) {
    return (
      <motion.div
        className="catalog-container"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 0.5 }}
      >
        <div className="loading-container">
          <h2>Loading sweets...</h2>
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
        {error && (
          <motion.div
            className="error-message"
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
          >
            {error}
          </motion.div>
        )}

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
