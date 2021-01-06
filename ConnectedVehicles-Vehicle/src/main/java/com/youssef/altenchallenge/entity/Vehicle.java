package com.youssef.altenchallenge.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.youssef.altenchallenge.configuration.ConfigProperties;

@Document(collection = "Vehicles")
public class Vehicle {

	public Vehicle() {
		super();
		configProperties = new ConfigProperties();
	}

	private ConfigProperties configProperties;

	@Transient
	public static final String SEQUENCE_NAME = "vehicles_sequence";

	private long customerId;

	@Id
	private String vehicleId;

	private String regNo;

	private LocalDateTime pingDtm;

	@Transient
	private String connectionStatus;

	public String getConnectionStatus() {
		if (pingDtm != null && LocalDateTime.now().minusMinutes(configProperties.getConnectionTimeoutMinutes())
				.compareTo(pingDtm) <= 0) {
			return "CONNECTED";
		}
		return "NOT CONNECTED";
	}

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

	public Vehicle(long customerId, String vin, String regNo, LocalDateTime pingDtm, ConfigProperties configProperties) {
		super();
		this.customerId = customerId;
		this.vehicleId = vin;
		this.regNo = regNo;
		setPingDtm(pingDtm);
		this.configProperties = configProperties;
	}

	public LocalDateTime getPingDtm() {
		return pingDtm;
	}

	public void setPingDtm(LocalDateTime pingDtm) {
		this.pingDtm = pingDtm;
	}
}
