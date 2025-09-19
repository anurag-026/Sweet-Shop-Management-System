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
        Sweet s1 = new Sweet();
        s1.setName("Belgian Dark Chocolate Truffles");
        s1.setCategory("chocolate");
        s1.setPrice(24.99);
        s1.setQuantity(15);
        s1.setDescription("Luxurious handcrafted truffles made with 70% Belgian dark chocolate and premium cocoa powder.");
        s1.setImage("https://i.postimg.cc/dZRqFQLv/elegant-dark-chocolate-truffles.jpg");
        sweetRepository.save(s1);
        
        Sweet s2 = new Sweet();
        s2.setName("French Macarons Assortment");
        s2.setCategory("macarons");
        s2.setPrice(32.99);
        s2.setQuantity(8);
        s2.setDescription("Delicate almond-based cookies with smooth ganache filling in six exquisite flavors.");
        s2.setImage("https://i.postimg.cc/JHC1SH7P/colorful-french-macarons-assortment.jpg");
        sweetRepository.save(s2);
        
        Sweet s3 = new Sweet();
        s3.setName("Artisan Gummy Bears");
        s3.setCategory("gummies");
        s3.setPrice(12.99);
        s3.setQuantity(25);
        s3.setDescription("Premium gummy bears made with real fruit juices and natural flavors.");
        s3.setImage("https://i.postimg.cc/sMTg12Kg/artisan-gummy-bears-colorful.jpg");
        sweetRepository.save(s3);
        
        Sweet s4 = new Sweet();
        s4.setName("Salted Caramel Bonbons");
        s4.setCategory("caramel");
        s4.setPrice(28.99);
        s4.setQuantity(12);
        s4.setDescription("Rich caramel centers enrobed in smooth milk chocolate with a hint of sea salt.");
        s4.setImage("https://i.postimg.cc/KKKxzDR0/salted-caramel-bonbons-chocolate.jpg");
        sweetRepository.save(s4);
        
        Sweet s5 = new Sweet();
        s5.setName("Strawberry Cream Fudge");
        s5.setCategory("fudge");
        s5.setPrice(18.99);
        s5.setQuantity(20);
        s5.setDescription("Creamy vanilla fudge swirled with real strawberry puree and white chocolate chips.");
        s5.setImage("https://i.postimg.cc/7CpDTJzw/strawberry-cream-fudge-pink-white.jpg");
        sweetRepository.save(s5);
        
        Sweet s6 = new Sweet();
        s6.setName("Honey Lavender Lollipops");
        s6.setCategory("lollipops");
        s6.setPrice(15.99);
        s6.setQuantity(30);
        s6.setDescription("Handcrafted lollipops infused with organic honey and dried lavender flowers.");
        s6.setImage("https://i.postimg.cc/z3TDK7pM/honey-lavender-lollipops-purple.jpg");
        sweetRepository.save(s6);
        
        Sweet s7 = new Sweet();
        s7.setName("Pistachio Rose Turkish Delight");
        s7.setCategory("turkish-delight");
        s7.setPrice(22.99);
        s7.setQuantity(10);
        s7.setDescription("Traditional Turkish delight flavored with pistachios and rose water, dusted with powdered sugar.");
        s7.setImage("https://i.postimg.cc/rKJ8YpBN/pistachio-rose-turkish-delight-pink.jpg");
        sweetRepository.save(s7);
        
        Sweet s8 = new Sweet();
        s8.setName("Mint Chocolate Chip Brittle");
        s8.setCategory("brittle");
        s8.setPrice(16.99);
        s8.setQuantity(18);
        s8.setDescription("Crunchy peanut brittle infused with fresh mint and studded with dark chocolate chips.");
        s8.setImage("https://i.postimg.cc/Yhkr3FsS/mint-chocolate-chip-brittle-green.jpg");
        sweetRepository.save(s8);
        
        Sweet s9 = new Sweet();
        s9.setName("Champagne Gummy Rings");
        s9.setCategory("gummies");
        s9.setPrice(19.99);
        s9.setQuantity(0);
        s9.setDescription("Sophisticated gummy rings with real champagne flavor and edible gold flakes.");
        s9.setImage("https://i.postimg.cc/7G2HBn45/champagne-gummy-rings-gold-elegant.jpg");
        sweetRepository.save(s9);
        
        Sweet s10 = new Sweet();
        s10.setName("Coconut Lime Macaroons");
        s10.setCategory("macaroons");
        s10.setPrice(21.99);
        s10.setQuantity(14);
        s10.setDescription("Chewy coconut macaroons with zesty lime zest and white chocolate drizzle.");
        s10.setImage("https://i.postimg.cc/TyhYbXHq/coconut-lime-macaroons-white-green.jpg");
        sweetRepository.save(s10);

        System.out.println("Sweets loaded successfully!");
    }
}
