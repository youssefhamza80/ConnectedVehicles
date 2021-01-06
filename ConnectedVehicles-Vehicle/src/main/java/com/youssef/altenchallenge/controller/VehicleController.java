package com.youssef.altenchallenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.service.VehicleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "Vehicle Controller APIs", tags = { "Vehicle Controller" })
public class VehicleController {

	private final VehicleService vehicleService;

	public VehicleController(VehicleService vehicleService) {
		super();
		this.vehicleService = vehicleService;
	}

	@ApiOperation(value = "Get all vehicles")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping()
	public ResponseEntity<List<Vehicle>> getAllVehicles() {
		return vehicleService.findAll();
	}

	@ApiOperation(value = "Get a specific vehicle by vehicle ID (VIN)")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping("/{vehicleId}")
	public ResponseEntity<Vehicle> findVehicle(@PathVariable String vehicleId) {
		return vehicleService.findByVehicleId(vehicleId);
	}

	@ApiOperation(value = "Ping vehicle using vehicle ID (VIN). This is like sending a heartbeat notification to indicate that vehicle is CONNECTED."
			+ "\r\n If ping was not called for a predefined period of time - defaulted to 1 minutes - then vehicle is considered NOT CONNECTED")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PutMapping("/ping/{vehicleId}")
	public ResponseEntity<Object> ping(@PathVariable String vehicleId) {
		return vehicleService.ping(vehicleId);
	}

	@ApiOperation(value = "Get connection status for a specific vehicle using vehicle ID (VIN). This methods checks the last ping date/time for a specific vehicle, and consider it CONNECTED if ping has been sent within a predefined period - defaulted to 1 minute -."
			+ "\r\n If ping was not called for this predefined period of time - to 1 minute - then vehicle status is NOT CONNECTED")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "CONNECTED"), @ApiResponse(code = 200, message = "NOT CONNECTED"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping("/connectionstatus/{vehicleId}")
	public ResponseEntity<String> getVehicleConnectionStatus(@PathVariable String vehicleId) {
		return vehicleService.getVehicleConnectionStatus(vehicleId);
	}

	@ApiOperation(value = "Update an existing vehicle")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PutMapping
	public ResponseEntity<Object> updateVehicle(@RequestBody Vehicle vehicle) {
		return vehicleService.updateVehicle(vehicle);
	}

	@ApiOperation(value = "Add a new vehicle to DB")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "CREATED"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PostMapping
	public ResponseEntity<Object> insertVehicle(@RequestBody Vehicle vehicle) {
		return vehicleService.insertNewVehicle(vehicle);
	}

	@ApiOperation(value = "Delete an existing vehicle from DB")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@DeleteMapping("/{vehicleId}")
	public ResponseEntity<String> deleteVehicle(@PathVariable String vehicleId) {
		return vehicleService.deleteVehicle(vehicleId);
	}

}
