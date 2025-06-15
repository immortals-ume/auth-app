package com.immortals.authapp.model.dto;

public record RegisterRequestDTO(
        String firstName,
        String middleName,
        String lastName,
        String userName,
        String password,
        String reTypePassword,

        String email,
        String phoneCode,
        String contactNumber
) {
}


