package com.immortals.authapp.service;


import com.immortals.authapp.model.dto.LoginDto;
import com.immortals.authapp.model.dto.LoginResponse;
import com.immortals.authapp.security.UserDetailsServiceImpl;
import com.immortals.authapp.security.jwt.JwtProvider;
import com.immortals.authapp.service.cache.CacheService;
import com.immortals.authapp.service.exception.AuthException;
import com.immortals.authapp.service.user.UserService;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.time.Duration;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProvider jwtProvider;
    private final CacheService<String,String> cacheService;
    private final UserService userService;
    private final HttpServletRequest request;
    private final TokenBlacklistService tokenBlacklistService;


    @Value("${auth.refresh-token-expiry-ms}")
    private int refreshTokenExpiryMs;

    @Transactional
    @Override
    public LoginResponse login(LoginDto loginInfoDto) {
        Authentication authentication;
        String token = "";
        String refreshToken = "";
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginInfoDto.username(), loginInfoDto.password()));
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
            if (authentication.isAuthenticated()) {
                token = jwtProvider.generateAccessToken(authentication);
                log.info("User {} logged in successfully", loginInfoDto.username());

                String refreshTokenKey = "refreshToken:" + loginInfoDto.username();
                if (loginInfoDto.rememberMe()) {
                    refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);
                    cacheService.put(refreshTokenKey, refreshToken, jwtProvider.getExpiryTimeFromToken(refreshToken),loginInfoDto.username());
                }
            }
            userService.updateLoginStatus(loginInfoDto.username());
            return new LoginResponse(loginInfoDto.username(),token,refreshToken);
        } catch (RuntimeException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                 JOSEException | ParseException e) {
            throw new AuthException(e.getMessage(), e);
        }
    }

    @Override
    public String generateRefreshToken(String username) {
        try {

            String refreshTokenKey = "refreshToken:" + username;
            if (cacheService.containsKey(refreshTokenKey,username)) {
                log.info("Refresh token already exists for user: {}", username);
                return "Refresh token already exists for user";
            }
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            String refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);

            cacheService.put("refreshToken:" + username, refreshToken, jwtProvider.getExpiryTimeFromToken(refreshToken),username);

            return refreshToken;
        } catch (Exception e) {
            log.error("Failed to generate refresh token for user {}", username, e);
            throw new RuntimeException("Unable to generate refresh token", e);
        }
    }

    @Override
    public void logout() {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Logout failed: Missing or invalid Authorization header.");
                throw new AuthException("Invalid Authorization header.");
            }

            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                log.warn("Logout failed: Invalid or expired token.");
                throw new AuthException("Invalid token.");
            }

            Duration expiry = jwtProvider.getExpiryTimeFromToken(token);
            long ttl = expiry.toMillis() - System.currentTimeMillis();

            if (ttl > 0) {
                tokenBlacklistService.blacklistToken(token, ttl);
                userService.updateLogoutStatus(jwtProvider.getUsernameFromToken(token));
                log.info("Token successfully blacklisted for logout.");
            } else {
                log.warn("Logout skipped: Token already expired.");
                userService.updateLogoutStatus(jwtProvider.getUsernameFromToken(token));
            }
        }catch (ParseException  e){
            log.warn("Logout failed: {}", "Logout failed");
            throw new AuthException("Logout failed");
        }
    }
}
