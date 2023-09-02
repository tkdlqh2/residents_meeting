package com.example.user_service.domain.dto;

import com.example.user_service.domain.Address;

public record UserInfo(Long id, String email, String name, String phone, Address address) {
}
