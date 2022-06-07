package com.youssef.connectedvehicles.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.youssef.connectedvehicles.client.CustomerClient;
import com.youssef.connectedvehicles.configuration.ConfigProperties;
import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.entity.Vehicle;
import com.youssef.connectedvehicles.repository.VehicleRepository;

import feign.FeignException;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@RunWith(SpringRunner.class)
class VehicleServiceTest {

	@MockBean
	ConfigProperties configProperties;

	@MockBean
	VehicleRepository vehicleRepository;

	@MockBean
	CustomerClient customerClient;

	@Autowired
	VehicleService vehicleService;

	@Test
	void whenCallingGetAllVehicles_thenReturnAllVehicles() {

		List<Vehicle> expectedVehicles = new ArrayList<>();

		expectedVehicles.add(new Vehicle(1, "VIN1", "REGNO1"));
		expectedVehicles.add(new Vehicle(2, "VIN2", "REGNO2"));

		ResponseEntity<List<Vehicle>> expectedResponse = new ResponseEntity<>(expectedVehicles, HttpStatus.OK);

		when(vehicleRepository.findAll()).thenReturn(expectedVehicles);

		ResponseEntity<List<Vehicle>> actualResponse = vehicleService.findAll();

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenAddingNewVehicleToExistingCustomer_thenStatusIsCreatedAndVehicleObjectIsReturned() {
		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(newVehicle, HttpStatus.CREATED);

		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.empty());

		when(vehicleRepository.save(newVehicle)).thenReturn(newVehicle);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(newVehicle.getCustomerId(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getCustomerId()),
				() -> assertEquals(newVehicle.getPingDtm(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getPingDtm()),
				() -> assertEquals(newVehicle.getRegNo(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getRegNo()),
				() -> assertEquals(newVehicle.getVehicleId(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getVehicleId()));
	}
	@Test
	void whenAddingNewVehicleAndCustomerClientExceptionIsRaised_thenStatusIsInternalServerErrorAndProperErrorMessageIsThere() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>("Error while retrieving customer data.",
				HttpStatus.INTERNAL_SERVER_ERROR);

		Vehicle newVehicle = new Vehicle(100, "VIN1", "REGNO1");

		when(customerClient.findCustomer(newVehicle.getCustomerId())).thenThrow(FeignException.class);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()), () -> assertTrue(
						Objects.requireNonNull(actualResponse.getBody()).toString().startsWith(Objects.requireNonNull(expectedResponse.getBody()).toString())));
	}

	@Test
	void whenAddingNewVehicleRepositoryException_thenStatusIsInternalServerError() {
		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(newVehicle,
				HttpStatus.INTERNAL_SERVER_ERROR);

		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.empty());

		when(vehicleRepository.save(newVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	void whenAddingDuplicatedVehicle_thenStatusIsBadRequest() {

		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(
				String.format("Vehicle with VIN '%s' already exists", newVehicle.getVehicleId()),
				HttpStatus.BAD_REQUEST);

		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional
				.of(new Vehicle(newVehicle.getCustomerId(), newVehicle.getVehicleId(), newVehicle.getRegNo())));

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));

	}

	@Test
	void whenUpdatingExistingVehicleStatusIsOK() {

		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(updatedVehicle.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(1, "VIN1", "OLD REG")));

		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.save(updatedVehicle)).thenReturn(updatedVehicle);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(updatedVehicle.getCustomerId(),
						((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getCustomerId()),
				() -> assertEquals(updatedVehicle.getPingDtm(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getPingDtm()),
				() -> assertEquals(updatedVehicle.getRegNo(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getRegNo()),
				() -> assertEquals(updatedVehicle.getVehicleId(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getVehicleId()));
	}

	@Test
	void whenUpdatingVehicleRepositoryException_thenStatusIsInternalServerError() {

		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedVehicle,
				HttpStatus.INTERNAL_SERVER_ERROR);

		when(vehicleRepository.findById(updatedVehicle.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(1, "VIN1", "OLD REG")));

		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.save(updatedVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode());
	}

	@Test
	void whenUpdatingNonExistingVehicle_thenStatusIsNotFound() {
		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG");
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(NOT_FOUND);
		when(vehicleRepository.findById(updatedVehicle.getVehicleId())).thenReturn(Optional.empty());
		Customer customerClientResponse =
				new Customer(1, "Youssef", "Doha Qatar");

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));

	}

	@Test
	void whenDeletingExistingVehicle_thenStatusIsOK() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG");
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.OK);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(vehicleToDelete.getCustomerId(), vehicleToDelete.getVehicleId(),
						vehicleToDelete.getRegNo())));

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));
	}

	@Test
	void whenDeletingNonExistingVehicle_thenStatusIsNotFound() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG");
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(NOT_FOUND);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));
	}

	@Test
	void whenDeleteVehicleRepositoryException_thenStatusIsInternalServerError() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG");
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId())).thenReturn(Optional.of(vehicleToDelete));

		doThrow(new RuntimeException("Exception while retrieving data from repository")).when(vehicleRepository)
				.deleteById(vehicleToDelete.getVehicleId());

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusExistingAndConnected_thenStatusIsOKAndStatusIsConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<>("CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");
		existingVehicle.setPingDtm(Instant.now());

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		when(configProperties.getConnectionTimeoutMinutes()).thenReturn(1);

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusNullPingDTM_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusOldPingDTM_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");
		existingVehicle.setPingDtm(Instant.now().minusSeconds(2*60*60));


		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusNonExisting_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");
		existingVehicle.setPingDtm(Instant.now().minusSeconds(2*60*60));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenPingExistingVehicleStatusIsOKAndUpdatedVehicleIsReturned() {
		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(existingVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		Vehicle expectedVehicle = new Vehicle(1, "VIN1", "REGNO1");
		expectedVehicle.setPingDtm(Instant.now());

		when(vehicleRepository.save(existingVehicle)).thenReturn(expectedVehicle);

		ResponseEntity<Object> actualResponse = vehicleService.ping(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertTrue(actualResponse.getBody() instanceof Vehicle),
				() -> assertEquals(existingVehicle.getCustomerId(),
						((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getCustomerId()),
				() -> assertNotNull(((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getPingDtm()),
				() -> assertEquals(existingVehicle.getRegNo(), ((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getRegNo()),
				() -> assertEquals(existingVehicle.getVehicleId(),
						((Vehicle) Objects.requireNonNull(actualResponse.getBody())).getVehicleId()));
	}

	@Test
	void whenPingExistingVehicleAndRepositoryException_thenInternalServerErrorIsReturned() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1");

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		when(vehicleRepository.save(existingVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.ping(existingVehicle.getVehicleId());

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	void whenPingNonExistingVehicleStatusIsNotFound() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(NOT_FOUND);

		when(vehicleRepository.findById("VIN1")).thenReturn(Optional.empty());

		ResponseEntity<Object> actualResponse = vehicleService.ping("VIN1");

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()));
	}
}
