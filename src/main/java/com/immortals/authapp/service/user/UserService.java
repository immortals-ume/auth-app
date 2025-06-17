package com.immortals.authapp.service.user;

import com.immortals.authapp.model.dto.RegisterRequestDTO;
import com.immortals.authapp.model.dto.UserAddressDTO;
import com.immortals.authapp.model.dto.UserDto;
import com.immortals.authapp.model.entity.User;
import com.immortals.authapp.model.entity.UserAddress;

public interface UserService {
    UserDto register(RegisterRequestDTO dto);

    User updateLoginStatus(String username);
    User updateLogoutStatus(String username);

    UserAddress updateAddress(String userId, UserAddressDTO addressDTO);

    User getUserByUsername(String username);
}
