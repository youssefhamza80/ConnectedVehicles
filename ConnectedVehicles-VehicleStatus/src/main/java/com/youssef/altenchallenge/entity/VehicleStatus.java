package com.youssef.altenchallenge.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "VehiclesStatuses")
public class VehicleStatus {

	@Transient
	public static final String SEQUENCE_NAME = "vehicles_statuses_sequence";

	@Id
	private String vehicleId;

	private LocalDateTime pingDtm;

	@Transient
	private boolean isConnected;

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public LocalDateTime getPingDtm() {
		return pingDtm;
	}

	public void setPingDtm(LocalDateTime pingDtm) {
		this.pingDtm = pingDtm;
		
		LocalDateTime nowTime= LocalDateTime.now();
		
		//nowTime.minusMinutes(1).compareTo(pingDtm)<0?isConnected=true:false;
	}

	public VehicleStatus() {
		super();
	}

	public VehicleStatus(String vehicleId, LocalDateTime pingDtm) {
		super();
		this.vehicleId = vehicleId;
		setPingDtm(pingDtm);
	}
}
