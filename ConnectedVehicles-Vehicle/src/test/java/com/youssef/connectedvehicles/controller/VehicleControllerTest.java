package com.youssef.connectedvehicles.controller;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.put;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.youssef.connectedvehicles.configuration.ConfigProperties;
import com.youssef.connectedvehicles.entity.Vehicle;
import com.youssef.connectedvehicles.repository.VehicleRepository;
import com.youssef.connectedvehicles.service.VehicleService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class VehicleControllerTest {

	@LocalServerPort
	private int port;

	private String uri;
	
	@MockBean 
	ConfigProperties configProperties;

	@MockBean
	VehicleService vehicleService;

	@MockBean
	VehicleRepository vehicleRepository;

	@PostConstruct
	public void init() {
		uri = "http://localhost:" + port;
		when(configProperties.getConnectionTimeoutMinutes()).thenReturn(1L);
	}

	@Test
	public void whenCallingGetAllVehicles_thenCorrect() {

		List<Vehicle> vehicles = List.of(
				new Vehicle(1L, "VIN1", "REGNO1"),
				new Vehicle(2L, "VIN2", "REGNO2"));

		when(vehicleService.findAll()).thenReturn(new ResponseEntity<>(vehicles, HttpStatus.OK));

		get(uri).then().statusCode(HttpStatus.OK.value()).assertThat().body("size()", is(2));
	}

	@Test
	public void whenFindingExistingVehicleStatusIsOKAndBodyIsCorrect() {

		Vehicle existingVehicle = new Vehicle(1L, "VIN1", "REGNO1");

		ResponseEntity<Vehicle> expectedResponse = new ResponseEntity<>(existingVehicle, HttpStatus.OK);

		when(vehicleService.findByVehicleId(existingVehicle.getVehicleId())).thenReturn(expectedResponse);

		get(uri + "/" + existingVehicle.getVehicleId()).then().statusCode(HttpStatus.OK.value()).assertThat()
				.body("regNo", equalTo(existingVehicle.getRegNo()))
				.body("customerId", equalTo(existingVehicle.getCustomerId()))
				.body("vehicleId", equalTo(existingVehicle.getVehicleId()));
	}

	@Test
	public void whenAddingNewVehicleToExistingCustomer_thenStatusIsCreatedAndBodyIsCorrect() {

		Vehicle newVehicle = new Vehicle(1L, "VIN1", "REGNO1");
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(newVehicle, HttpStatus.CREATED);
		when(vehicleService.insertNewVehicle(any(Vehicle.class))).thenReturn(responseEntity);

		Map<String, String> request = new HashMap<>();
		request.put("customerId", "1");
		request.put("vehicleId", "VIN1");
		request.put("regNo", "REGNO1");
		// when(customerService.findAll()).thenReturn(customers);

		Vehicle retrievedVehicle = given().contentType("application/json").body(request).when().post(uri).then()
				.statusCode(HttpStatus.CREATED.value()).extract().as(Vehicle.class);

		assertAll(() -> assertNotNull(retrievedVehicle),
				() -> assertEquals(retrievedVehicle.getVehicleId(), newVehicle.getVehicleId()),
				() -> assertEquals(retrievedVehicle.getRegNo(), newVehicle.getRegNo()));
	}

	@Test
	public void whenAddingNewVehicleToNonExistingCustomer_thenStatusIsCreatedAndBodyIsCorrect() {
		ResponseEntity<Object> responseEntity = new ResponseEntity<>("Customer id: 100 does not exist",
				HttpStatus.BAD_REQUEST);
		when(vehicleService.insertNewVehicle(any(Vehicle.class))).thenReturn(responseEntity);

		Map<String, String> request = new HashMap<>();
		request.put("customerId", "100");
		request.put("vehicleId", "VIN1");
		request.put("regNo", "REGNO1");

		given().contentType("application/json").body(request).when().post(uri).then()
				.statusCode(HttpStatus.BAD_REQUEST.value()).body(equalTo("Customer id: 100 does not exist"));
	}

	@Test
	public void whenAddingDuplicatedVehicle_thenStatusIsBadRequest() {
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		when(vehicleService.insertNewVehicle(any(Vehicle.class))).thenReturn(responseEntity);
		Map<String, String> request = new HashMap<>();
		request.put("customerId", "1");
		request.put("vehicleId", "VIN1");
		request.put("regNo", "REGNO1");

		given().contentType("application/json").body(request).when().post(uri).then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void whenUpdatingExistingVehicleStatusIsOK() {

		Vehicle updatedVehicle = new Vehicle(1L, "VIN1", "UPDATED REG");
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

		when(vehicleService.updateVehicle(any(Vehicle.class))).thenReturn(responseEntity);

		Map<String, String> request = new HashMap<>();
		request.put("customerId", "1");
		request.put("vehicleId", "VIN1");
		request.put("regNo", "UPDATED REG");

		Vehicle retrievedVehicle = given().contentType("application/json").body(request).when().put(uri).then()
				.statusCode(HttpStatus.OK.value()).extract().as(Vehicle.class);

		assertAll(() -> assertNotNull(retrievedVehicle),
				() -> assertEquals(retrievedVehicle.getVehicleId(), updatedVehicle.getVehicleId()),
				() -> assertEquals(retrievedVehicle.getRegNo(), updatedVehicle.getRegNo()));
	}

	@Test
	public void whenUpdatingNonExistingVehicle_thenStatusIsNotFound() {
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		when(vehicleService.updateVehicle(any(Vehicle.class))).thenReturn(responseEntity);
		Map<String, String> request = new HashMap<>();
		request.put("customerId", "1");
		request.put("vehicleId", "VIN1");
		request.put("regNo", "REGNO1");

		given().contentType("application/json").body(request).when().put(uri).then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void whenDeletingExistingVehicle_thenStatusIsOK() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
		when(vehicleService.deleteVehicle("VIN1")).thenReturn(responseEntity);

		delete(uri + "/VIN1").then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void whenDeletingNonExistingVehicle_thenStatusIsNotFound() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		when(vehicleService.deleteVehicle("VIN1")).thenReturn(responseEntity);

		delete(uri + "/VIN1").then().statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void whenGetExistingConnectedVehicleStatus_thenStatusIsOKAndStatusIsConnected() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>("CONNECTED", HttpStatus.OK);
		when(vehicleService.getVehicleConnectionStatus(any())).thenReturn(responseEntity);

		get(uri + "/connectionstatus/VIN1").then().statusCode(HttpStatus.OK.value()).body(equalTo("CONNECTED"));
	}

	@Test
	public void whenGetNotConnectedVehicleStatus_thenStatusIsOKAndStatusIsNotConnected() {
		ResponseEntity<String> responseEntity = new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);
		when(vehicleService.getVehicleConnectionStatus("VIN1")).thenReturn(responseEntity);

		get(uri + "/connectionstatus/VIN1").then().statusCode(HttpStatus.OK.value()).body(equalTo("NOT CONNECTED"));
	}

	@Test
	public void whenPingingExistingVehicleStatusIsOKAndUpdatedVehicleIsReturned() {
		Instant updatedPingDtm = Instant.now();
		Vehicle updatedVehicle = new Vehicle(1L, "VIN1", "REGNO1");
		updatedVehicle.setPingDtm(updatedPingDtm);
		ResponseEntity<Object> responseEntity = new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

		when(vehicleService.ping("VIN1")).thenReturn(responseEntity);

		Vehicle retrievedVehicle = put(uri + "/ping/VIN1").then().statusCode(HttpStatus.OK.value()).extract()
				.as(Vehicle.class);

		assertAll(() -> assertNotNull(retrievedVehicle),
				() -> assertEquals(retrievedVehicle.getVehicleId(), updatedVehicle.getVehicleId()),
				() -> assertEquals(retrievedVehicle.getRegNo(), updatedVehicle.getRegNo()),
				() -> assertEquals(retrievedVehicle.getPingDtm(), updatedVehicle.getPingDtm()));
	}

	@Test
	public void whenPingingNonExistingVehicleStatusIsNotFound() {

		ResponseEntity<Object> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(vehicleService.ping("VIN1")).thenReturn(responseEntity);

		put(uri + "/ping/VIN1").then().statusCode(HttpStatus.NOT_FOUND.value());
	}
}