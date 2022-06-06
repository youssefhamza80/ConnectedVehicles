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

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.*;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class VehicleService {

    final ConfigProperties configProperties;

    final VehicleRepository vehicleRepository;

    final CustomerClient customerClient;

    private void setVehicleConnectionStatus(Vehicle vehicle) {
        if (vehicle.getPingDtm() != null
                && Instant.now()
                .minusSeconds(60 * configProperties.getConnectionTimeoutMinutes())
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

            return ResponseEntity.ok(vehicles);
        } catch (Exception ex) {
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Vehicle> findByVehicleId(String vehicleId) {
        try {
            Optional<Vehicle> vehicle = vehicleRepository.findById(vehicleId);
            if (vehicle.isPresent()) {
                setVehicleConnectionStatus(vehicle.get());
                return ResponseEntity.ok(vehicle.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> insertNewVehicle(Vehicle vehicle) {
        try {
            ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicle.getVehicleId());
            if (existingVehicle.getStatusCode() == OK) {
                return ResponseEntity.status(BAD_REQUEST).body(
                        String.format("Vehicle with VIN '%s' already exists", vehicle.getVehicleId()));
            }

            // Check if customer exists
            ResponseEntity<Customer> foundCustomer = customerClient.findCustomer(vehicle.getCustomerId());

            if (foundCustomer == null || foundCustomer.getStatusCode() != OK) {
                return ResponseEntity.status(BAD_REQUEST).body(String.format("Customer id: %d does not exist", vehicle.getCustomerId()));
            }
            vehicle = vehicleRepository.save(vehicle);
            setVehicleConnectionStatus(vehicle);
            return ResponseEntity.status(CREATED).body(vehicle);
        } catch (FeignException feignEx) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Error while retrieving customer data." + feignEx.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> ping(String vehicleId) {
        try {
            Vehicle vehicle;
            ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicleId);
            if (existingVehicle.getStatusCode() == OK) {
                vehicle = existingVehicle.getBody();
                if (vehicle != null) {
                    vehicle.setPingDtm(Instant.now());
                    vehicle = vehicleRepository.save(vehicle);
                    setVehicleConnectionStatus(vehicle);
                    return ResponseEntity.ok(vehicle);
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Object> updateVehicle(Vehicle vehicle) {
        try {
            ResponseEntity<Vehicle> existingVehicle = findByVehicleId(vehicle.getVehicleId());
            if (existingVehicle.getStatusCode() != OK || existingVehicle.getBody() == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            vehicle = vehicleRepository.save(vehicle);
            setVehicleConnectionStatus(vehicle);
            return new ResponseEntity<>(vehicle, OK);
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> deleteVehicle(String vehicleId) {
        try {
            ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
            if (vehicleResponse.getStatusCode() == OK && vehicleResponse.getBody() != null) {
                vehicleRepository.deleteById(vehicleId);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<String> getVehicleConnectionStatus(String vehicleId) {
        try {
            ResponseEntity<Vehicle> vehicleResponse = findByVehicleId(vehicleId);
            if (vehicleResponse.getStatusCode() == OK) {
                Vehicle vehicle = vehicleResponse.getBody();
                if (vehicle != null) {
                    return new ResponseEntity<>(vehicle.getConnectionStatus(), OK);
                }
            }

            return new ResponseEntity<>("NOT CONNECTED", OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex.getMessage(), INTERNAL_SERVER_ERROR);
        }
    }
}
