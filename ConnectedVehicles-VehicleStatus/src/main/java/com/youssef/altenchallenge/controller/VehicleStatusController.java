package com.youssef.altenchallenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.youssef.altenchallenge.entity.VehicleStatus;
import com.youssef.altenchallenge.service.VehicleStatusService;

@RestController
public class VehicleStatusController {

	private final VehicleStatusService vehicleStatusService;

	public VehicleStatusController(VehicleStatusService vehicleStatusService) {
		super();
		this.vehicleStatusService = vehicleStatusService;
	}

	@GetMapping
	public List<VehicleStatus> getAllVehiclesStatuses() {
		return vehicleStatusService.findAll();
	}

	@GetMapping("/{vehicleId}")
	public ResponseEntity<VehicleStatus> findVehicleStatus(@PathVariable String vehicleId) {
		return vehicleStatusService.findByVehicleId(vehicleId);
	}
	
	@PutMapping("/ping/{vehicleId}")
	public ResponseEntity<VehicleStatus> ping(@PathVariable String vehicleId) {
		return vehicleStatusService.ping(vehicleId);
	}
}
