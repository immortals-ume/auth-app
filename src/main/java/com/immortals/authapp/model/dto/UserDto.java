package com.immortals.authapp.model.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.immortals.authapp.model.entity.User}
 */
public record UserDto(String createdBy, LocalDateTime createdDate, String updatedBy, LocalDateTime updatedDate, String deletedBy,
                      LocalDateTime deletedDate, Long userId, @NotBlank String firstName, @NotBlank String middleName,
                      @NotBlank String lastName, @Size(min = 3, max = 16) @NotBlank String userName,
                      @NotNull(message = "Password cannot be empty") String password,
                      @Email(message = "Email is not in correct format") @NotBlank String email,
                      @Email(message = "Email is not in correct format") String alternateEmail, Boolean emailVerified,
                      @NotBlank String phoneCode,
                      @Pattern(message = "Contact number invalid", regexp = "^(\\+91)?[6-9][0-9]{9}$") String contactNumber,
                      @Pattern(message = "Alternate contact invalid", regexp = "^(\\+91)?[6-9][0-9]{9}$") String alternateContact,
                      Boolean phoneNumberVerified, Instant login, Instant logout, Boolean accountNonExpired,
                      Boolean accountNonLocked, Boolean accountLocked, Boolean credentialsNonExpired,
                      Boolean activeInd) implements Serializable {
}