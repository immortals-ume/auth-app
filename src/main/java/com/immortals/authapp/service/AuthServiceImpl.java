package com.immortals.authapp.service;

import com.immortals.authapp.model.dto.LoginDto;
import com.immortals.authapp.model.dto.LoginResponse;
import com.immortals.authapp.security.UserDetailsServiceImpl;
import com.immortals.authapp.security.jwt.JwtProvider;
import com.immortals.authapp.service.cache.CacheService;
import com.immortals.authapp.service.exception.AuthException;
import com.immortals.authapp.service.user.UserService;
import com.immortals.authapp.utils.CookieUtils;
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
import java.util.Optional;

import static com.immortals.authapp.constants.CacheConstants.REFRESH_TOKEN_HASH_KEY;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtProvider jwtProvider;
    private final CacheService<String, String, String> hashCacheService;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CookieUtils cookieUtils;
    private final LoginAttempt loginAttempt;

    @Value("${auth.refresh-token-expiry-ms}")
    private int refreshTokenExpiryMs;


    @Transactional
    @Override
    public LoginResponse login(LoginDto loginInfoDto) {
        try {
            String username = loginInfoDto.username();

            if (loginAttempt.isBlocked(username)) {
                log.warn("🚫 Login attempt blocked for user '{}'", username);
                throw new AuthException("Your account is temporarily locked due to too many failed login attempts. Please try again later.");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginInfoDto.username(), loginInfoDto.password())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (!authentication.isAuthenticated()) {
                loginAttempt.loginFailed(username);
                throw new AuthException("Authentication failed. Please check your credentials.");
            }

            loginAttempt.loginSucceeded(username);
            String accessToken = jwtProvider.generateAccessToken(authentication);
            log.info("✅ User '{}' successfully authenticated.", loginInfoDto.username());

            String refreshToken = "";
            if (loginInfoDto.rememberMe()) {
                refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);
                hashCacheService.put(
                        REFRESH_TOKEN_HASH_KEY,
                        loginInfoDto.username(),
                        refreshToken,
                        Duration.ofMillis(refreshTokenExpiryMs),
                        loginInfoDto.username()
                );
                log.info("🔁 Refresh token generated and cached for user '{}'.", loginInfoDto.username());
            }

            userService.updateLoginStatus(loginInfoDto.username());
            return new LoginResponse(loginInfoDto.username(), accessToken, refreshToken,"Successfully generated the Login Token");

        } catch (RuntimeException | IOException | NoSuchAlgorithmException | InvalidKeySpecException |
                 JOSEException e) {
            log.error("❌ Login failed for user '{}': {}", loginInfoDto.username(), e.getMessage(), e);
            throw new AuthException("Login failed. Please verify your credentials and try again.", e);
        }
    }

    @Override
    public LoginResponse generateRefreshToken(String username) {
        try {
            if (hashCacheService.containsKey(REFRESH_TOKEN_HASH_KEY, username, username)) {
                log.info("ℹ️ Refresh token already exists for user '{}'.", username);
                return new LoginResponse(username,null,null,"Refresh token already exists.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            String refreshToken = jwtProvider.generateRefreshToken(authentication, refreshTokenExpiryMs);
            hashCacheService.put(
                    REFRESH_TOKEN_HASH_KEY,
                    username,
                    refreshToken,
                    Duration.ofMillis(refreshTokenExpiryMs),
                    username
            );

            log.info("🔁 Refresh token generated for user '{}'.", username);
            return new LoginResponse(username,null,refreshToken,"Refresh token generated for user");
        } catch (Exception e) {
            log.error("❌ Failed to generate refresh token for user '{}': {}", username, e.getMessage(), e);
            throw new AuthException("Unable to generate refresh token. Please try again later.", e);
        }
    }

    @Override
    public void logout(HttpServletRequest request) {
        try {

            Optional<String> token = cookieUtils.getRefreshTokenFromCookie(request) ;
            if (token.isPresent()) {
                tokenBlacklistService.blacklistToken(token.get(), refreshTokenExpiryMs);
                userService.updateLogoutStatus(jwtProvider.getUsernameFromToken(token.get()));
                log.info("Logout successful");
            }

        } catch (RuntimeException | ParseException e) {
            log.error("❌ Logout failed due to token parsing error.", e);
            throw new AuthException("Logout failed. Unable to process token.");
        }

    }
}
