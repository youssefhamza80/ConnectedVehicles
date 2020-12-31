package com.youssef.altenchallenge.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.youssef.altenchallenge.client.CustomerClient;
import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.repository.VehicleRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class VehicleServiceTest {

	@MockBean
	VehicleRepository vehicleRepository;

	@MockBean
	CustomerClient customerClient;

	@Autowired
	VehicleService vehicleService;

	@Test
	public void whenCallingGetAllVehicles_thenAllVehiclesAreReturned() {

		List<Vehicle> expectedVehicles = new ArrayList<>();

		expectedVehicles.add(new Vehicle(1, "VIN1", "REGNO1", null));
		expectedVehicles.add(new Vehicle(2, "VIN2", "REGNO2", null));

		when(vehicleRepository.findAll()).thenReturn(expectedVehicles);

		List<Vehicle> actualVehicles = vehicleService.findAll();

		assertAll(() -> assertNotNull(actualVehicles), () -> assertTrue(actualVehicles.size() > 0),
				() -> assertEquals(expectedVehicles, actualVehicles));
	}

	@Test
	public void whenAddingNewVehicleToExistingCustomer_thenStatusIsCreatedAndVehicleObjectIsReturned() {
		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(newVehicle, HttpStatus.CREATED);

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(newVehicle.getCustomerId(), ((Vehicle) actualResponse.getBody()).getCustomerId()),
				() -> assertEquals(newVehicle.getPingDtm(), ((Vehicle) actualResponse.getBody()).getPingDtm()),
				() -> assertEquals(newVehicle.getRegNo(), ((Vehicle) actualResponse.getBody()).getRegNo()),
				() -> assertEquals(newVehicle.getVehicleId(), ((Vehicle) actualResponse.getBody()).getVehicleId()));
	};

	@Test
	public void whenAddingNewVehicleToNonExistingCustomer_thenStatusIsBadRequestAndErrorMessageIsThere() {
		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>("Customer id: 100 does not exist",
				HttpStatus.BAD_REQUEST);
		Vehicle newVehicle = new Vehicle(100, "VIN1", "REGNO1", null);

		ResponseEntity<Customer> notFoundCustomerResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		when(customerClient.findCustomer(100)).thenReturn(notFoundCustomerResponse);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	};

	@Test
	public void whenAddingDuplicatedVehicle_thenStatusIsBadRequest() {

		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(
				String.format("Vehicle with VIN '%s' already exists", newVehicle.getVehicleId()),
				HttpStatus.BAD_REQUEST);

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional
				.of(new Vehicle(newVehicle.getCustomerId(), newVehicle.getVehicleId(), newVehicle.getRegNo(), null)));

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));

	}

	@Test
	public void whenUpdatingExistingVehicleStatusIsOK() {

		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(updatedVehicle.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(1, "VIN1", "OLD REG", null)));

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(updatedVehicle.getCustomerId(),
						((Vehicle) actualResponse.getBody()).getCustomerId()),
				() -> assertEquals(updatedVehicle.getPingDtm(), ((Vehicle) actualResponse.getBody()).getPingDtm()),
				() -> assertEquals(updatedVehicle.getRegNo(), ((Vehicle) actualResponse.getBody()).getRegNo()),
				() -> assertEquals(updatedVehicle.getVehicleId(), ((Vehicle) actualResponse.getBody()).getVehicleId()));
	}

	@Test
	public void whenUpdatingNonExistingVehicle_thenStatusIsNotFound() {
		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG", null);
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		when(vehicleRepository.findById(updatedVehicle.getVehicleId())).thenReturn(Optional.empty());
		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));

	}

	@Test
	public void whenDeletingExistingVehicle_thenStatusIsOK() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG", null);
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.OK);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(vehicleToDelete.getCustomerId(), vehicleToDelete.getVehicleId(),
						vehicleToDelete.getRegNo(), null)));

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));
	}

	@Test
	public void whenDeletingNonExistingVehicle_thenStatusIsNotFound() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG", null);
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));
	}

	@Test
	public void whenGetExistingConnectedVehicleStatus_thenStatusIsOKAndStatusIsConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", LocalDateTime.now().plusMinutes(1));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	public void whenGetExistingVehicleStatusNullPingDTM_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	public void whenGetExistingVehicleStatusOldPingDTM_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", LocalDateTime.now().minusHours(2));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	public void whenGetNonExistingVehicleStatus_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", LocalDateTime.now().minusHours(2));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	public void whenPingingExistingVehicleStatusIsOKAndUpdatedVehicleIsReturned() {
		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(existingVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<Object> actualResponse = vehicleService.ping(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(existingVehicle.getCustomerId(),
						((Vehicle) actualResponse.getBody()).getCustomerId()),
				() -> assertNotNull(((Vehicle) actualResponse.getBody()).getPingDtm()),
				() -> assertEquals(existingVehicle.getRegNo(), ((Vehicle) actualResponse.getBody()).getRegNo()),
				() -> assertEquals(existingVehicle.getVehicleId(),
						((Vehicle) actualResponse.getBody()).getVehicleId()));
	}

	@Test
	public void whenPingingNonExistingVehicleStatusIsNotFound() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(vehicleRepository.findById("VIN1")).thenReturn(Optional.empty());

		ResponseEntity<Object> actualResponse = vehicleService.ping("VIN1");

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()));
	}
}
