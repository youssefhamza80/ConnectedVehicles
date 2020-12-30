package com.youssef.altenchallenge.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.client.CustomerClient;
import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.repository.VehicleRepository;

import feign.FeignException;

@Service
public class VehicleService {

	private final VehicleRepository vehicleRepository;

	private final CustomerClient customerClient;

	public VehicleService(VehicleRepository vehicleRepository, CustomerClient customerClient) {
		super();
		this.vehicleRepository = vehicleRepository;
		this.customerClient = customerClient;
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
				throw new IllegalArgumentException(
						String.format("Vehicle with VIN '%s'  and Registration No. '%s' already exists",
								vehicle.getVehicleId(), vehicle.getRegNo()));
			}

			// Check if customer exists
			ResponseEntity<Customer> foundCustomer = customerClient.findCustomer(vehicle.getCustomerId());

			if (foundCustomer == null || foundCustomer.getStatusCode() != HttpStatus.OK) {
				throw new IllegalArgumentException(
						String.format("Customer id: %d does not exist", vehicle.getCustomerId()));
			}
			vehicleRepository.save(vehicle);
			return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
		} catch (FeignException feignEx) {
			return new ResponseEntity<>("Error while retrieving customer data." + feignEx.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Object> ping(String vehicleId) {
		try {
			Vehicle vehicle = null;
			ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicleId);
			if (existingVehicle.getStatusCode() == HttpStatus.OK && existingVehicle.getBody() != null) {
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

	public ResponseEntity<String> getVehicleConnectionStatus(String vehicleId) {
		ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
		if (vehicleResponse.getStatusCode() == HttpStatus.OK) {
			Vehicle vehicle = vehicleResponse.getBody();
			if (vehicle != null) {
				LocalDateTime pingDtm = vehicle.getPingDtm();

				if (pingDtm != null && LocalDateTime.now().minusMinutes(1).compareTo(pingDtm) <= 0) {
					return new ResponseEntity<>("CONNECTED", HttpStatus.OK);
				}
			}
		}

		return new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);
	}
}
