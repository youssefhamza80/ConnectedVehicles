package com.youssef.altenchallenge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.entity.VehicleStatus;
import com.youssef.altenchallenge.repository.VehicleRepository;

@Service
public class VehicleService {

	private final VehicleRepository vehicleRepository;

	public VehicleService(VehicleRepository vehicleRepository) {
		super();
		this.vehicleRepository = vehicleRepository;
	}

	public List<Vehicle> findAll() {
		return vehicleRepository.findAll();
	}

	public ResponseEntity<Vehicle> findByVehicleId(String vehicleId) {
		Optional<Vehicle> customer = vehicleRepository.findById(vehicleId);
		if (customer.isPresent()) {
			return new ResponseEntity<>(customer.get(), HttpStatus.FOUND);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Object> insertNewVehicle(Vehicle vehicle) {
		try {
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicle.getVehicleId());
			if (existingVehicle.getStatusCode() == HttpStatus.FOUND) {
				throw new IllegalArgumentException(
						String.format("Vehicle with VIN '%s'  and Registration No. '%s' already exists",
								vehicle.getVehicleId(), vehicle.getRegNo()));
			}
			vehicleRepository.save(vehicle);
			return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
		}
	}

	public ResponseEntity<Object> ping(String vehicleId) {
		try {
			Vehicle vehicle = null;
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicleId);
			if (existingVehicle.getStatusCode() == HttpStatus.FOUND && existingVehicle.getBody() != null) {
				vehicle = existingVehicle.getBody();
				vehicle.setPingDtm(LocalDateTime.now());
				vehicleRepository.save(vehicle);
				return new ResponseEntity<>(vehicle, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Object> updateVehicle(Vehicle vehicle) {
		try {
			vehicleRepository.save(vehicle);
			return new ResponseEntity<>(vehicle, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Void> deleteVehicle(String vehicleId) {
		try {
			vehicleRepository.deleteById(vehicleId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
