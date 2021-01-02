package com.youssef.altenchallenge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.client.CustomerClient;
import com.youssef.altenchallenge.configuration.ConfigProperties;
import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.repository.VehicleRepository;

import feign.FeignException;

@Service
public class VehicleService {

	private final VehicleRepository vehicleRepository;

	private final CustomerClient customerClient;

	private final ConfigProperties configProperties;

	public VehicleService(VehicleRepository vehicleRepository, CustomerClient customerClient,
			ConfigProperties configProperties) {
		super();
		this.vehicleRepository = vehicleRepository;
		this.customerClient = customerClient;
		this.configProperties = configProperties;
	}

	public List<Vehicle> findAll() {
		return vehicleRepository.findAll();
	}

	public ResponseEntity<Vehicle> findByVehicleId(String vehicleId) {
		Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
		if (vehicle.isPresent()) {
			return new ResponseEntity<>(vehicle.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
			Vehicle vehicle = null;
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicleId);
			if (existingVehicle.getStatusCode() == HttpStatus.OK && (vehicle = existingVehicle.getBody()) != null) {

				vehicle.setPingDtm(LocalDateTime.now());
				vehicle = vehicleRepository.save(vehicle);
				return new ResponseEntity<>(vehicle, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
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
		ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
		if (vehicleResponse.getStatusCode() == HttpStatus.OK) {
			Vehicle vehicle = vehicleResponse.getBody();
			if (vehicle != null) {
				LocalDateTime pingDtm = vehicle.getPingDtm();
				if (pingDtm != null && LocalDateTime.now().minusMinutes(configProperties.getConnectionTimeoutMinutes())
						.compareTo(pingDtm) <= 0) {
					return new ResponseEntity<>("CONNECTED", HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);
	}
}
