package com.youssef.connectedvehicles.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.connectedvehicles.client.CustomerClient;
import com.youssef.connectedvehicles.configuration.ConfigProperties;
import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.entity.Vehicle;
import com.youssef.connectedvehicles.repository.VehicleRepository;

import feign.FeignException;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleService {

	ConfigProperties configProperties;

	VehicleRepository vehicleRepository;

	CustomerClient customerClient;

	private void setVehicleConnectionStatus(Vehicle vehicle) {
		if (vehicle.getPingDtm() != null
				&& Instant.now().minusSeconds(60 * configProperties.getConnectionTimeoutMinutes())
						.compareTo(vehicle.getPingDtm()) <= 0) {
			vehicle.setConnectionStatus("CONNECTED");
		} else {
			vehicle.setConnectionStatus("NOT CONNECTED");
		}
	}

	public ResponseEntity<List<Vehicle>> findAll() {
		try {
			List<Vehicle> vehicles = vehicleRepository.findAll();

			vehicles.forEach(this::setVehicleConnectionStatus);

			return new ResponseEntity<>(vehicles, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Vehicle> findByVehicleId(String vehicleId) {
		try {
			Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
			if (vehicle.isPresent()) {
				setVehicleConnectionStatus(vehicle.get());
				return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> insertNewVehicle(Vehicle vehicle) {
		try {
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicle.getVehicleId());
			if (existingVehicle.getStatusCode() == HttpStatus.OK) {
				return new ResponseEntity<>(
						String.format("Vehicle with VIN '%s' already exists", vehicle.getVehicleId()),
						HttpStatus.BAD_REQUEST);
			}

			// Check if customer exists
			ResponseEntity<Customer> foundCustomer = customerClient.findCustomer(vehicle.getCustomerId());

			if (foundCustomer == null || foundCustomer.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>(String.format("Customer id: %d does not exist", vehicle.getCustomerId()),
						HttpStatus.BAD_REQUEST);
			}
			vehicle = vehicleRepository.save(vehicle);
			setVehicleConnectionStatus(vehicle);
			return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
		} catch (FeignException feignEx) {
			return new ResponseEntity<>("Error while retrieving customer data." + feignEx.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> ping(String vehicleId) {
		try {
			Vehicle vehicle;
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicleId);
			if (existingVehicle.getStatusCode() == HttpStatus.OK) {
				vehicle = existingVehicle.getBody();
				if (vehicle != null) {
					vehicle.setPingDtm(Instant.now());
					vehicle = vehicleRepository.save(vehicle);
					setVehicleConnectionStatus(vehicle);
					return new ResponseEntity<>(vehicle, HttpStatus.OK);
				}
			}
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> updateVehicle(Vehicle vehicle) {
		try {
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicle.getVehicleId());
			if (existingVehicle.getStatusCode() != HttpStatus.OK || existingVehicle.getBody() == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			vehicle = vehicleRepository.save(vehicle);
			setVehicleConnectionStatus(vehicle);
			return new ResponseEntity<>(vehicle, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> deleteVehicle(String vehicleId) {
		try {
			ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
			if (vehicleResponse.getStatusCode() == HttpStatus.OK && vehicleResponse.getBody() != null) {
				vehicleRepository.deleteById(vehicleId);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> getVehicleConnectionStatus(String vehicleId) {
		try {
			ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
			if (vehicleResponse.getStatusCode() == HttpStatus.OK) {
				Vehicle vehicle = vehicleResponse.getBody();
				if (vehicle != null) {
					return new ResponseEntity<>(vehicle.getConnectionStatus(), HttpStatus.OK);
				}
			}

			return new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
