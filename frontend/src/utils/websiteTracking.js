class WebsiteTracker {
  constructor() {
    this.sessionId = this.generateSessionId();
    this.startTime = Date.now();
    this.pageViews = 0;
    this.currentPage = window.location.pathname;
    this.userId = this.getUserId();
    this.isTracking = true;

    this.init();
  }

  init() {
    if (!this.isTracking) return;

    // Track session start
    this.trackSessionStart();

    // Track initial page view
    this.trackPageView();

    // Set up event listeners
    this.setupEventListeners();

    // Track page visibility changes
    this.setupVisibilityTracking();

    // Track beforeunload for session end
    this.setupSessionEndTracking();

    console.log("Website tracking initialized");
  }

  /**
   * Generate unique session ID
   */
  generateSessionId() {
    const existingSessionId = sessionStorage.getItem("tracking_session_id");
    if (existingSessionId) {
      return existingSessionId;
    }

    const sessionId =
      "session_" + Date.now() + "_" + Math.random().toString(36).substr(2, 9);
    sessionStorage.setItem("tracking_session_id", sessionId);
    return sessionId;
  }

  /**
   * Get user ID from localStorage or JWT token
   */
  getUserId() {
    // Try to get from localStorage first
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      return storedUserId;
    }

    // Try to get from JWT token if available
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        return payload.userId || payload.sub || null;
      } catch (e) {
        console.warn("Could not parse JWT token for user ID");
      }
    }

    return null;
  }

  /**
   * Set up event listeners for tracking
   */
  setupEventListeners() {
    // Track page changes (for SPA)
    window.addEventListener("popstate", () => {
      this.trackPageView();
    });

    // Track clicks on product links
    document.addEventListener("click", (event) => {
      this.trackClick(event);
    });

    // Track form submissions
    document.addEventListener("submit", (event) => {
      this.trackFormSubmission(event);
    });

    // Track scroll depth
    this.trackScrollDepth();
  }

  /**
   * Set up page visibility tracking
   */
  setupVisibilityTracking() {
    document.addEventListener("visibilitychange", () => {
      if (document.hidden) {
        this.trackSessionEnd();
      } else {
        this.trackSessionResume();
      }
    });
  }

  /**
   * Set up session end tracking
   */
  setupSessionEndTracking() {
    window.addEventListener("beforeunload", () => {
      this.trackSessionEnd();
    });
  }

  /**
   * Track session start
   */
  async trackSessionStart() {
    const data = {
      sessionId: this.sessionId,
      landingPage: this.currentPage,
      referrer: document.referrer,
      userAgent: navigator.userAgent,
      ipAddress: null, // Will be determined by backend
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/session-start", data);
  }

  /**
   * Track page view
   */
  async trackPageView() {
    this.currentPage = window.location.pathname;
    this.pageViews++;

    const data = {
      sessionId: this.sessionId,
      page: this.currentPage,
      referrer: document.referrer,
      userAgent: navigator.userAgent,
      ipAddress: null,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/page-view", data);
  }

  /**
   * Track session end
   */
  async trackSessionEnd() {
    const sessionDuration = Math.floor((Date.now() - this.startTime) / 1000);

    const data = {
      sessionId: this.sessionId,
      exitPage: this.currentPage,
      sessionDuration: sessionDuration,
      pageViews: this.pageViews,
    };

    // Use sendBeacon for reliable delivery on page unload
    if (navigator.sendBeacon) {
      navigator.sendBeacon("/api/tracking/session-end", JSON.stringify(data));
    } else {
      await this.sendTrackingData("/api/tracking/session-end", data);
    }
  }

  /**
   * Track session resume
   */
  async trackSessionResume() {
    // Track page view when user returns
    this.trackPageView();
  }

  /**
   * Track click events
   */
  trackClick(event) {
    const target = event.target;
    const href = target.getAttribute("href");

    // Track product clicks
    if (href && href.includes("/products/")) {
      const productId = this.extractProductId(href);
      if (productId) {
        this.trackProductView(productId);
      }
    }

    // Track add to cart clicks
    if (
      target.classList.contains("add-to-cart") ||
      target.closest(".add-to-cart")
    ) {
      const productId = this.extractProductIdFromElement(target);
      if (productId) {
        this.trackAddToCart(productId);
      }
    }

    // Track checkout clicks
    if (href && href.includes("/checkout")) {
      this.trackCheckoutStart();
    }
  }

  /**
   * Track form submissions
   */
  trackFormSubmission(event) {
    const form = event.target;

    // Track checkout form submission
    if (
      form.id === "checkout-form" ||
      form.classList.contains("checkout-form")
    ) {
      this.trackCheckoutStart();
    }
  }

  /**
   * Track scroll depth
   */
  trackScrollDepth() {
    let maxScroll = 0;
    let scrollCheckpoints = [25, 50, 75, 90, 100];
    let reachedCheckpoints = new Set();

    window.addEventListener("scroll", () => {
      const scrollPercent = Math.round(
        (window.scrollY / (document.body.scrollHeight - window.innerHeight)) *
          100
      );

      if (scrollPercent > maxScroll) {
        maxScroll = scrollPercent;

        // Track scroll depth milestones
        scrollCheckpoints.forEach((checkpoint) => {
          if (
            scrollPercent >= checkpoint &&
            !reachedCheckpoints.has(checkpoint)
          ) {
            reachedCheckpoints.add(checkpoint);
            this.trackScrollDepth(checkpoint);
          }
        });
      }
    });
  }

  /**
   * Track product view
   */
  async trackProductView(productId) {
    const data = {
      sessionId: this.sessionId,
      productId: productId,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/product-view", data);
  }

  /**
   * Track add to cart
   */
  async trackAddToCart(productId) {
    const data = {
      sessionId: this.sessionId,
      productId: productId,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/add-to-cart", data);
  }

  /**
   * Track checkout start
   */
  async trackCheckoutStart() {
    const data = {
      sessionId: this.sessionId,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/checkout-start", data);
  }

  /**
   * Track purchase completion
   */
  async trackPurchaseComplete(orderId) {
    const data = {
      sessionId: this.sessionId,
      orderId: orderId,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/purchase-complete", data);
  }

  /**
   * Track scroll depth milestone
   */
  async trackScrollDepth(depth) {
    const data = {
      sessionId: this.sessionId,
      eventType: "scroll_depth",
      depth: depth,
      userId: this.userId,
    };

    await this.sendTrackingData("/api/tracking/conversion-event", data);
  }

  /**
   * Extract product ID from URL
   */
  extractProductId(href) {
    const match = href.match(/\/products\/([^\/\?]+)/);
    return match ? match[1] : null;
  }

  /**
   * Extract product ID from element
   */
  extractProductIdFromElement(element) {
    // Try to find product ID in various attributes
    const productId =
      element.getAttribute("data-product-id") ||
      element.getAttribute("data-id") ||
      element.closest("[data-product-id]")?.getAttribute("data-product-id") ||
      element.closest("[data-id]")?.getAttribute("data-id");

    return productId;
  }

  /**
   * Send tracking data to backend
   */
  async sendTrackingData(endpoint, data) {
    try {
      const response = await fetch(endpoint, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(data),
      });

      if (!response.ok) {
        console.warn("Tracking request failed:", response.status);
      }
    } catch (error) {
      console.warn("Tracking request error:", error);
    }
  }

  /**
   * Enable/disable tracking
   */
  setTracking(enabled) {
    this.isTracking = enabled;
    if (enabled) {
      this.init();
    }
  }

  /**
   * Get current session info
   */
  getSessionInfo() {
    return {
      sessionId: this.sessionId,
      pageViews: this.pageViews,
      currentPage: this.currentPage,
      userId: this.userId,
      sessionDuration: Math.floor((Date.now() - this.startTime) / 1000),
    };
  }
}

// Create global instance
window.websiteTracker = new WebsiteTracker();

// Export for module usage
export default WebsiteTracker;
