package com.anurag.SweetShopBackend.exception;

import org.springframework.security.access.AccessDeniedException;

 
public class AdminAccessDeniedException extends AccessDeniedException {
    
    public AdminAccessDeniedException(String msg) {
        super("Admin privileges required: " + msg);
    }
    
    public AdminAccessDeniedException() {
        super("Admin privileges required for this operation");
    }
}
