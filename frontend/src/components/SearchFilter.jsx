"use client"

import { useState } from "react"
import { motion } from "framer-motion"
import "./SearchFilter.css"

const SearchFilter = ({ onSearch, onFilter, onSort }) => {
  const [searchTerm, setSearchTerm] = useState("")
  const [selectedCategory, setSelectedCategory] = useState("")
  const [priceRange, setPriceRange] = useState({ min: "", max: "" })
  const [sortBy, setSortBy] = useState("name")

  const categories = ["chocolate", "candy", "gummy", "cookies", "cakes"]

  const handleSearchChange = (e) => {
    const value = e.target.value
    setSearchTerm(value)
    onSearch(value)
  }

  const handleCategoryChange = (category) => {
    setSelectedCategory(category)
    onFilter({ category, priceRange })
  }

  const handlePriceRangeChange = (field, value) => {
    const newPriceRange = { ...priceRange, [field]: value }
    setPriceRange(newPriceRange)
    onFilter({ category: selectedCategory, priceRange: newPriceRange })
  }

  const handleSortChange = (e) => {
    const value = e.target.value
    setSortBy(value)
    onSort(value)
  }

  const clearFilters = () => {
    setSearchTerm("")
    setSelectedCategory("")
    setPriceRange({ min: "", max: "" })
    setSortBy("name")
    onSearch("")
    onFilter({ category: "", priceRange: { min: "", max: "" } })
    onSort("name")
  }

  return (
    <motion.div
      className="search-filter"
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
    >
      <div className="search-section">
        <div className="search-input-container">
          <input
            type="text"
            placeholder="Search for sweets..."
            value={searchTerm}
            onChange={handleSearchChange}
            className="search-input"
          />
          <div className="search-icon">🔍</div>
        </div>
      </div>

      <div className="filter-section">
        <div className="filter-group">
          <label>Categories</label>
          <div className="category-buttons">
            <motion.button
              className={`category-btn ${selectedCategory === "" ? "active" : ""}`}
              onClick={() => handleCategoryChange("")}
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
            >
              All
            </motion.button>
            {categories.map((category) => (
              <motion.button
                key={category}
                className={`category-btn ${selectedCategory === category ? "active" : ""}`}
                onClick={() => handleCategoryChange(category)}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                {category.charAt(0).toUpperCase() + category.slice(1)}
              </motion.button>
            ))}
          </div>
        </div>

        <div className="filter-group">
          <label>Price Range</label>
          <div className="price-inputs">
            <input
              type="number"
              placeholder="Min"
              value={priceRange.min}
              onChange={(e) => handlePriceRangeChange("min", e.target.value)}
              className="price-input"
            />
            <span className="price-separator">-</span>
            <input
              type="number"
              placeholder="Max"
              value={priceRange.max}
              onChange={(e) => handlePriceRangeChange("max", e.target.value)}
              className="price-input"
            />
          </div>
        </div>

        <div className="filter-group">
          <label>Sort By</label>
          <select value={sortBy} onChange={handleSortChange} className="sort-select">
            <option value="name">Name (A-Z)</option>
            <option value="name-desc">Name (Z-A)</option>
            <option value="price-asc">Price (Low to High)</option>
            <option value="price-desc">Price (High to Low)</option>
            <option value="category">Category</option>
          </select>
        </div>

        <motion.button
          className="clear-filters-btn"
          onClick={clearFilters}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          Clear Filters
        </motion.button>
      </div>
    </motion.div>
  )
}

export default SearchFilter
