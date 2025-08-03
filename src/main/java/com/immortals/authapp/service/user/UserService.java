package com.immortals.authapp.service.user;

import com.immortals.authapp.model.dto.RegisterRequestDTO;
import com.immortals.authapp.model.dto.ResetCredentials;
import com.immortals.authapp.model.dto.UserAddressDTO;
import com.immortals.authapp.model.dto.UserDto;
import com.immortals.authapp.model.entity.User;
import com.immortals.authapp.model.entity.UserAddress;

public interface UserService {
    UserDto register(RegisterRequestDTO dto);

    String resetPassword(ResetCredentials resetCredentials);

    void updateLoginStatus(String username);

    void updateLogoutStatus(String username);

    UserAddress updateOrAddUserAddress(String userId, UserAddressDTO addressDTO);

    User getUserByUsername(String username);

    String sendChangePasswordEmail(String username);
}
