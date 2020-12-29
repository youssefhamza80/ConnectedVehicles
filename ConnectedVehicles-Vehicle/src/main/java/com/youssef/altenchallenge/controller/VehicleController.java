package com.youssef.altenchallenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.service.VehicleService;

@RestController
public class VehicleController {

	private final VehicleService vehicleService;

	public VehicleController(VehicleService vehicleService) {
		super();
		this.vehicleService = vehicleService;
	}

	@GetMapping
	public List<Vehicle> getAllVehicles() {
		return vehicleService.findAll();
	}

	@GetMapping("/{vehicleId}")
	public ResponseEntity<Vehicle> findVehicle(@PathVariable String vehicleId) {
		return vehicleService.findByVehicleId(vehicleId);
	}

	@PostMapping("/new")
	public ResponseEntity<Vehicle> insertVehicle(@RequestBody Vehicle vehicle) {
		return vehicleService.insertNewVehicle(vehicle);
	}

	@DeleteMapping("/delete/{vehicleId}")
	public ResponseEntity<Void> deleteVehicle(@PathVariable String vehicleId) {
		return vehicleService.deleteVehicle(vehicleId);
	}
}
