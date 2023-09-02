package com.example.vote_service;

public record UserInfo(Long id, String email, String name, String phone, Address address) {

	public record Address(String apartmentCode, int building, int unit) {}
}

