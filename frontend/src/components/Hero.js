"use client";
import { motion } from "framer-motion";
import "./Hero.css";

const Hero = () => {
  return (
    <section className="hero">
      <div className="container">
        <div className="hero-content">
          <motion.div
            className="hero-text"
            initial={{ opacity: 0, y: 50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2 }}
          >
            <h1 className="hero-title text-balance">
              Indulge in the finest
              <span className="hero-accent"> handcrafted sweets</span>
            </h1>
            <p className="hero-description text-pretty">
              Discover our exquisite collection of premium confectionery,
              crafted with the finest ingredients and traditional techniques to
              bring you moments of pure sweetness.
            </p>
            <div className="hero-actions">
              <motion.a
                href="#catalog"
                className="btn btn-primary"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Explore Collection
              </motion.a>
              <motion.a
                href="#about"
                className="btn btn-secondary"
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                Our Story
              </motion.a>
            </div>
          </motion.div>

          <motion.div
            className="hero-image"
            initial={{ opacity: 0, scale: 0.8 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ duration: 0.8, delay: 0.4 }}
          >
            <div className="hero-image-container">
              <img
                src="/elegant-chocolate-truffles-and-colorful-macarons-d.jpg"
                alt="Premium sweets collection"
                className="hero-img"
              />
              <div className="hero-image-overlay"></div>
            </div>
          </motion.div>
        </div>
      </div>

      <div className="hero-decorations">
        <motion.div
          className="decoration decoration-1"
          animate={{
            y: [0, -20, 0],
            rotate: [0, 5, 0],
          }}
          transition={{
            duration: 4,
            repeat: Number.POSITIVE_INFINITY,
            ease: "easeInOut",
          }}
        >
          🍭
        </motion.div>
        <motion.div
          className="decoration decoration-2"
          animate={{
            y: [0, 15, 0],
            rotate: [0, -5, 0],
          }}
          transition={{
            duration: 3,
            repeat: Number.POSITIVE_INFINITY,
            ease: "easeInOut",
            delay: 1,
          }}
        >
          🧁
        </motion.div>
        <motion.div
          className="decoration decoration-3"
          animate={{
            y: [0, -10, 0],
            rotate: [0, 3, 0],
          }}
          transition={{
            duration: 5,
            repeat: Number.POSITIVE_INFINITY,
            ease: "easeInOut",
            delay: 2,
          }}
        >
          🍫
        </motion.div>
      </div>
    </section>
  );
};

export default Hero;
