package com.youssef.altenchallenge.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Vehicles")
public class Vehicle {
	
	@Transient
    public static final String SEQUENCE_NAME = "vehicles_sequence";

	private long customerId;
	
	@Id
	private String vehicleId;
	
	private String regNo;

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vin) {
		this.vehicleId = vin;
	}

	public String getRegNo() {
		return regNo;
	}

	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}

	public Vehicle(long customerId, String vin, String regNo) {
		super();
		this.customerId = customerId;
		this.vehicleId = vin;
		this.regNo = regNo;
	}

	public Vehicle() {
		super();
	}
}
