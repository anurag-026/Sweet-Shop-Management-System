package com.anurag.SweetShopBackend;

import com.anurag.SweetShopBackend.model.User;
import com.anurag.SweetShopBackend.repository.UserRepository;
import com.anurag.SweetShopBackend.security.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseTestClass {
    
    @Autowired
    protected UserRepository userRepository;
    
    @Autowired
    protected JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    protected UserDetailsService userDetailsService;
    
    protected User testUser;
    protected User testAdmin;
    protected String userToken;
    protected String adminToken;
    
    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi"); // "password"
        testUser.setRole("ROLE_USER");
        testUser.setPhone("1234567890");
        testUser.setAddress("Test Address");
        testUser = userRepository.save(testUser);
        
        // Create test admin
        testAdmin = new User();
        testAdmin.setId(UUID.randomUUID());
        testAdmin.setFullName("Test Admin");
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi"); // "password"
        testAdmin.setRole("ROLE_ADMIN");
        testAdmin.setPhone("0987654321");
        testAdmin.setAddress("Admin Address");
        testAdmin = userRepository.save(testAdmin);
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(testUser.getEmail());
        UserDetails adminDetails = userDetailsService.loadUserByUsername(testAdmin.getEmail());
        
        userToken = jwtTokenUtil.generateToken(userDetails);
        adminToken = jwtTokenUtil.generateToken(adminDetails);
    }
    
    protected UserDetails createUserDetails(String email, String role) {
        return new org.springframework.security.core.userdetails.User(
            email,
            "password",
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}
