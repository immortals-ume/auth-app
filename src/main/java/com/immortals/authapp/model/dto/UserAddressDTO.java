package com.immortals.authapp.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {
    private String addressLine1;
    private String addressLine2;
    private Long city;
    private Long state;
    private Long country;
    private String zipCode;
}