package com.immortals.authapp.service.user;

import com.immortals.authapp.model.dto.RegisterRequestDTO;
import com.immortals.authapp.model.dto.UserAddressDTO;
import com.immortals.authapp.model.dto.UserDto;
import com.immortals.authapp.model.entity.User;
import com.immortals.authapp.model.entity.UserAddress;
import com.immortals.authapp.model.enums.AddressStatus;
import com.immortals.authapp.model.enums.UserTypes;
import com.immortals.authapp.repository.UserAddressRepository;
import com.immortals.authapp.repository.UserRepository;
import com.immortals.authapp.service.cache.CacheService;
import com.immortals.authapp.service.exception.AuthException;
import com.immortals.authapp.service.exception.ResourceNotFoundException;
import com.immortals.authapp.service.exception.UserException;
import com.immortals.authapp.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserAddressRepository userAddressRepo;
    @Qualifier("passwordEncoder")
    private final PasswordEncoder passwordEncoder;

    private final CityService cityService;
    private final CountryService countryService;
    private final StateService stateService;
    private final UserRepository userRepository;

    private final CacheService<String,User> cacheService;

    public UserServiceImpl(UserRepository userRepo, UserAddressRepository userAddressRepo, PasswordEncoder passwordEncoder, CityService cityService, CountryService countryService, StateService stateService, UserRepository userRepository, CacheService<String, User> cacheService) {
        this.userRepo = userRepo;
        this.userAddressRepo = userAddressRepo;
        this.passwordEncoder = passwordEncoder;
        this.cityService = cityService;
        this.countryService = countryService;
        this.stateService = stateService;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    @Override
    public User register(RegisterRequestDTO dto) {
        try {
            if (userRepo.existsByEmail(dto.email()) || userRepo.existsByUserName(dto.userName())) {
                throw new UserException("Email or Username already exists");
            }

            if (!dto.password()
                    .equals(dto.reTypePassword())) {
                throw new UserException("Password and re-typed password do not match. Please try again.");
            }
            User user = User.builder()
                    .firstName(dto.firstName())
                    .middleName(dto.middleName())
                    .lastName(dto.lastName())
                    .userName(dto.userName())
                    .password(passwordEncoder.encode(dto.password()))
                    .email(dto.email())
                    .phoneCode(dto.phoneCode())
                    .contactNumber(dto.contactNumber())
                    .emailVerified(Boolean.FALSE)
                    .phoneNumberVerified(Boolean.FALSE)
                    .accountNonExpired(Boolean.TRUE)
                    .accountNonLocked(Boolean.TRUE)
                    .accountLocked(Boolean.FALSE)
                    .credentialsNonExpired(Boolean.TRUE)
                    .createdBy(UserTypes.SYSTEM.name())
                    .createdDate(DateTimeUtils.now())
                    .activeInd(Boolean.TRUE)
                    .build();


            log.info("Registered user: {}", user.getUserName());

            return userRepo.saveAndFlush(user);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            throw new UserException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during user registration", e);
            throw new UserException(e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    @Override
    public User updateLoginStatus(String username) {
        try {
            User user = getUserByUsername(username);

            user.setLogin(Instant.now());
            user.setUpdatedBy(UserTypes.SYSTEM.name());
            user.setUpdatedDate(DateTimeUtils.now());

            log.info("Login time updated for user ID: {}", username);
            return userRepository.saveAndFlush(user);


        } catch (ResourceNotFoundException e) {
            log.warn("Login time  update failed - user not found: {}", username);
            throw new UserException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during Login time  update for user ID {}", username, e);
            throw new UserException("Failed to update Login time. Please try again later.");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    public User updateLogoutStatus(String username) {
        try {
            User user = getUserByUsername(username);

            user.setLogout(Instant.now());
            user.setUpdatedBy(UserTypes.SYSTEM.name());
            user.setUpdatedDate(DateTimeUtils.now());

            log.info("LogOut time updated for user ID: {}", username);
            return userRepository.saveAndFlush(user);


        } catch (ResourceNotFoundException e) {
            log.warn("LogOut time  update failed - user not found: {}", username);
            throw new UserException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during LogOut time  update for user ID {}", username, e);
            throw new UserException("Failed to update LogOut time. Please try again later.");
        }
    }

    @Transactional
    @Override
    public User getUserByUsername(String username) {
        try {
            User cachedUser = cacheService.get(username, username);
            if (cachedUser != null) {
                return cachedUser;
            }


            return userRepo.findUser(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User", username));
        } catch (RuntimeException e) {
            throw new AuthException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = {Exception.class})
    @Override
    public UserAddress updateAddress(Long userId, UserAddressDTO dto) {
        try {
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId));

            UserAddress address = UserAddress.builder()
                    .user(user)
                    .addressLine1(dto.getAddressLine1())
                    .addressLine2(dto.getAddressLine2())
                    .city(cityService.toEntity(cityService.getById(dto.getCity())))
                    .states(stateService.toEntity(stateService.getById(dto.getState())))
                    .country(countryService.toEntity(countryService.getById(dto.getCountry())))
                    .pincode(dto.getZipCode())
                    .status(AddressStatus.ACTIVE)
                    .timezone(ZoneId.systemDefault()
                            .toString())
                    .createdDate(DateTimeUtils.now())
                    .createdBy(UserTypes.SYSTEM.name())
                    .build();

            user.getUserAddresses()
                    .add(address);
            log.info("Address updated for user ID: {}", userId);
            return userAddressRepo.saveAndFlush(address);


        } catch (ResourceNotFoundException e) {
            log.warn("Address update failed - user not found: {}", userId);
            throw new UserException(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during address update for user ID {}", userId, e);
            throw new UserException("Failed to update address. Please try again later.");
        }
    }
}
