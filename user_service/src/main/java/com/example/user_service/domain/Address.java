package com.example.user_service.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.io.Serializable;

@Embeddable
@Getter
public class Address implements Serializable {

	private String apartmentCode;
	private int building;
	private int unit;

	public Address(String apartmentCode, int building, int unit) {
		this.apartmentCode = apartmentCode;
		this.building = building;
		this.unit = unit;
	}

	protected Address() {
		this(null, 0, 0);
	}
}