package com.youssef.altenchallenge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.altenchallenge.entity.VehicleStatus;
import com.youssef.altenchallenge.repository.VehicleStatusRepository;

@Service
public class VehicleStatusService {

	private final VehicleStatusRepository vehicleStatusRepository;

	public VehicleStatusService(VehicleStatusRepository vehicleStatusRepository) {
		super();
		this.vehicleStatusRepository = vehicleStatusRepository;
	}

	public List<VehicleStatus> findAll() {
		return vehicleStatusRepository.findAll();
	}

	public ResponseEntity<VehicleStatus> findByVehicleId(String vehicleId) {
		Optional<VehicleStatus> vehicleStatus = vehicleStatusRepository.findById(vehicleId);

		if (vehicleStatus.isPresent()) {
			return new ResponseEntity<>(vehicleStatus.get(), HttpStatus.FOUND);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<VehicleStatus> ping(String vehicleId) {
		try {
			VehicleStatus vehicleStatus = null;
			ResponseEntity<VehicleStatus> existingVehicleStatus = findByVehicleId(vehicleId);
			if (existingVehicleStatus.getStatusCode() == HttpStatus.FOUND) {
				vehicleStatus = existingVehicleStatus.getBody();
				vehicleStatus.setPingDtm(LocalDateTime.now());
			} else {
				vehicleStatus = new VehicleStatus(vehicleId, LocalDateTime.now());
			}
			vehicleStatusRepository.save(vehicleStatus);
			return new ResponseEntity<>(vehicleStatus, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}
}
