package com.immortals.authapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements  GuestService{
    @Override
    public String generateGuestLogin() {
        return "";
    }
}
