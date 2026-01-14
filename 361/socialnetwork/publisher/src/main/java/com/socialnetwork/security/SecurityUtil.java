package com.socialnetwork.security;

import com.socialnetwork.model.Role;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getId() : null;
    }

    public static Role getCurrentUserRole() {
        CustomUserDetails userDetails = getCurrentUser();
        return userDetails != null ? userDetails.getRole() : null;
    }

    public static boolean isAdmin() {
        return getCurrentUserRole() == Role.ADMIN;
    }

    public static boolean isCustomer() {
        return getCurrentUserRole() == Role.CUSTOMER;
    }

    public static boolean isOwner(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }
}

