package com.immortals.authapp.controller.users;


import com.immortals.authapp.model.dto.RegisterRequestDTO;
import com.immortals.authapp.model.dto.UserAddressDTO;
import com.immortals.authapp.model.entity.User;
import com.immortals.authapp.service.user.UserService;
import com.immortals.authapp.utils.JsonUtils;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody RegisterRequestDTO dto) {
        log.info("Registering user: {}", dto.email());
        return userService.register(dto);
    }

    @PreAuthorize(" hasRole('ADMIN') or hasRole('ROLE_USER') and hasAnyAuthority('READ')")
    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public User register( @PathVariable String username) {
        return userService.getUserByUsername(username);
    }



    @PreAuthorize(" hasRole('ADMIN') or hasRole('ROLE_USER') and hasAnyAuthority('UPDATE')")
    @PatchMapping("/address")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody UserAddressDTO dto) {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated");
        }

        String username = auth.getName();

        Long userId = userService.getUserByUsername(username).getUserId();

        return ResponseEntity.ok(JsonUtils.toJson(userService.updateAddress(userId, dto)));
    }
}
