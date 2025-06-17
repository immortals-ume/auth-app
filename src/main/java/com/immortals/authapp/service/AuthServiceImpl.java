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
    private final CacheService<String, String> cacheService;
    private final UserService userService;
    private final HttpServletRequest request;
    private final TokenBlacklistService tokenBlacklistService;

    @Value("${auth.refresh-token-expiry-ms}")
    private int refreshTokenExpiryMs;

    @Transactional
    @Override
    public LoginResponse login(LoginDto loginInfoDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfoDto.username(), loginInfoDto.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!authentication.isAuthenticated()) {
                throw new AuthException("Authentication failed. Please check your credentials.");
            }

            String accessToken = jwtProvider.generateAccessToken(authentication);
            log.info("✅ User '{}' successfully authenticated.", loginInfoDto.username());

            String refreshToken = "";
            if (loginInfoDto.rememberMe()) {
                refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);
                String refreshTokenKey = "refreshToken:" + loginInfoDto.username();
                cacheService.put(refreshTokenKey, refreshToken, jwtProvider.getExpiryTimeFromToken(refreshToken), loginInfoDto.username());
                log.info("🔁 Refresh token generated and cached for user '{}'.", loginInfoDto.username());
            }

            userService.updateLoginStatus(loginInfoDto.username());
            return new LoginResponse(loginInfoDto.username(), accessToken, refreshToken);

        } catch (RuntimeException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                 JOSEException | ParseException e) {
            log.error("❌ Login failed for user '{}': {}", loginInfoDto.username(), e.getMessage(), e);
            throw new AuthException("Login failed. Please verify your credentials and try again.", e);
        }
    }

    @Override
    public String generateRefreshToken(String username) {
        try {
            String refreshTokenKey = "refreshToken:" + username;
            if (cacheService.containsKey(refreshTokenKey, username)) {
                log.info("ℹ️ Refresh token already exists for user '{}'.", username);
                return "Refresh token already exists.";
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            String refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);
            cacheService.put(refreshTokenKey, refreshToken, jwtProvider.getExpiryTimeFromToken(refreshToken), username);

            log.info("🔁 Refresh token generated for user '{}'.", username);
            return refreshToken;

        } catch (Exception e) {
            log.error("❌ Failed to generate refresh token for user '{}': {}", username, e.getMessage(), e);
            throw new AuthException("Unable to generate refresh token. Please try again later.", e);
        }
    }

    @Override
    public void logout() {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("⚠️ Logout failed: Missing or invalid Authorization header.");
                throw new AuthException("Invalid or missing Authorization header.");
            }

            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                log.warn("⚠️ Logout failed: Token is invalid or already expired.");
                throw new AuthException("Invalid or expired token.");
            }

            Duration expiry = jwtProvider.getExpiryTimeFromToken(token);
            long ttlMillis = expiry.toMillis() - System.currentTimeMillis();

            String username = jwtProvider.getUsernameFromToken(token);
            if (ttlMillis > 0) {
                tokenBlacklistService.blacklistToken(token, ttlMillis);
                log.info("🛑 Token blacklisted for user '{}'.", username);
            } else {
                log.info("⏳ Token for user '{}' already expired. Skipping blacklist.", username);
            }

            userService.updateLogoutStatus(username);

        } catch (ParseException e) {
            log.error("❌ Logout failed due to token parsing error.", e);
            throw new AuthException("Logout failed. Unable to process token.");
        }
    }
}

