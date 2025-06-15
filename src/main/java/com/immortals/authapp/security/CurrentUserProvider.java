package com.immortals.authapp.security;

import com.immortals.authapp.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserProvider {
    public UserPrincipal getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        return (UserPrincipal) auth.getPrincipal();
    }
}