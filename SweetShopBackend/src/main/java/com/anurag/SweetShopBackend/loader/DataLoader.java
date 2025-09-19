package com.anurag.SweetShopBackend.loader;

import com.anurag.SweetShopBackend.model.Sweet;
import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.SweetRepository;
import com.anurag.SweetShopBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SweetRepository sweetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load default users if none exist
        if (userRepository.count() == 0) {
            loadUsers();
        }

        // Load sample sweets if none exist
        if (sweetRepository.count() == 0) {
            loadSweets();
        }
    }

    private void loadUsers() {
        System.out.println("Loading users...");

        User adminUser = new User();
        adminUser.setFullName("Anurag");
        adminUser.setEmail("admin@sweetshop.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole("ROLE_ADMIN");
        userRepository.save(adminUser);

        User regularUser = new User();
        regularUser.setFullName("Regular User");
        regularUser.setEmail("user@sweetshop.com");
        regularUser.setPassword(passwordEncoder.encode("user123"));
        regularUser.setRole("ROLE_USER");
        userRepository.save(regularUser);

        System.out.println("Users loaded successfully!");
    }

    private void loadSweets() {
        System.out.println("Loading sweets...");

        // Load sweets from mock data
        sweetRepository.save(new Sweet(null, "Belgian Dark Chocolate Truffles", "chocolate", 24.99, 15, 
            "Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.", 
            "elegant-dark-chocolate-truffles.jpg"));
        
        sweetRepository.save(new Sweet(null, "French Macarons Assortment", "macarons", 32.99, 8, 
            "Delicate almond-based cookies with smooth ganache filling in six exquisite flavors.", 
            "colorful-french-macarons-assortment.jpg"));
        
        sweetRepository.save(new Sweet(null, "Artisan Gummy Bears", "gummies", 12.99, 25, 
            "Premium gummy bears made with real fruit juices and natural flavors.", 
            "artisan-gummy-bears-colorful.jpg"));
        
        sweetRepository.save(new Sweet(null, "Salted Caramel Bonbons", "caramel", 28.99, 12, 
            "Rich caramel centers enrobed in smooth milk chocolate with a hint of sea salt.", 
            "salted-caramel-bonbons-chocolate.jpg"));
        
        sweetRepository.save(new Sweet(null, "Strawberry Cream Fudge", "fudge", 18.99, 20, 
            "Creamy vanilla fudge swirled with real strawberry puree and white chocolate chips.", 
            "strawberry-cream-fudge-pink-white.jpg"));
        
        sweetRepository.save(new Sweet(null, "Honey Lavender Lollipops", "lollipops", 15.99, 30, 
            "Handcrafted lollipops infused with organic honey and dried lavender flowers.", 
            "honey-lavender-lollipops-purple.jpg"));
        
        sweetRepository.save(new Sweet(null, "Pistachio Rose Turkish Delight", "turkish-delight", 22.99, 10, 
            "Traditional Turkish delight flavored with pistachios and rose water, dusted with powdered sugar.", 
            "pistachio-rose-turkish-delight-pink.jpg"));
        
        sweetRepository.save(new Sweet(null, "Mint Chocolate Chip Brittle", "brittle", 16.99, 18, 
            "Crunchy peanut brittle infused with fresh mint and studded with dark chocolate chips.", 
            "mint-chocolate-chip-brittle-green.jpg"));
        
        sweetRepository.save(new Sweet(null, "Champagne Gummy Rings", "gummies", 19.99, 0, 
            "Sophisticated gummy rings with real champagne flavor and edible gold flakes.", 
            "champagne-gummy-rings-gold-elegant.jpg"));
        
        sweetRepository.save(new Sweet(null, "Coconut Lime Macaroons", "macaroons", 21.99, 14, 
            "Chewy coconut macaroons with zesty lime zest and white chocolate drizzle.", 
            "coconut-lime-macaroons-white-green.jpg"));

        System.out.println("Sweets loaded successfully!");
    }
}
