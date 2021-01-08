package com.youssef.altenchallenge.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
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
import com.youssef.altenchallenge.configuration.ConfigProperties;
import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.entity.Vehicle;
import com.youssef.altenchallenge.repository.VehicleRepository;

import feign.FeignException;

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

		expectedVehicles.add(new Vehicle(1, "VIN1", "REGNO1", null));
		expectedVehicles.add(new Vehicle(2, "VIN2", "REGNO2", null));

		ResponseEntity<List<Vehicle>> expectedResponse = new ResponseEntity<>(expectedVehicles, HttpStatus.OK);

		when(vehicleRepository.findAll()).thenReturn(expectedVehicles);

		ResponseEntity<List<Vehicle>> actualResponse = vehicleService.findAll();

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenAddingNewVehicleToExistingCustomer_thenStatusIsCreatedAndVehicleObjectIsReturned() {
		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(newVehicle, HttpStatus.CREATED);

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.empty());

		when(vehicleRepository.save(newVehicle)).thenReturn(newVehicle);

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
	void whenAddingNewVehicleToNonExistingCustomer_thenStatusIsBadRequestAndErrorMessageIsThere() {
		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>("Customer id: 100 does not exist",
				HttpStatus.BAD_REQUEST);
		Vehicle newVehicle = new Vehicle(100, "VIN1", "REGNO1", null);

		ResponseEntity<Customer> notFoundCustomerResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);
		when(customerClient.findCustomer(100)).thenReturn(notFoundCustomerResponse);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));

		when(customerClient.findCustomer(100)).thenReturn(null);

		ResponseEntity<Object> actualResponse2 = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse2.getStatusCode()),
				() -> assertNotNull(actualResponse2.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse2.getBody()));
	};

	@Test
	void whenAddingNewVehicleAndCustomerClientExceptionIsRaised_thenStatusIsInternalServerErrorAndProperErrorMessageIsThere() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>("Error while retrieving customer data.",
				HttpStatus.INTERNAL_SERVER_ERROR);

		Vehicle newVehicle = new Vehicle(100, "VIN1", "REGNO1", null);

		when(customerClient.findCustomer(newVehicle.getCustomerId())).thenThrow(FeignException.class);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()), () -> assertTrue(
						actualResponse.getBody().toString().startsWith(expectedResponse.getBody().toString())));
	};

	@Test
	void whenAddingNewVehicleRepositoryException_thenStatusIsInternalServerError() {
		Vehicle newVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(newVehicle,
				HttpStatus.INTERNAL_SERVER_ERROR);

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.findById(newVehicle.getVehicleId())).thenReturn(Optional.empty());

		when(vehicleRepository.save(newVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.insertNewVehicle(newVehicle);

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
	};

	@Test
	void whenAddingDuplicatedVehicle_thenStatusIsBadRequest() {

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
	void whenUpdatingExistingVehicleStatusIsOK() {

		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(updatedVehicle.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(1, "VIN1", "OLD REG", null)));

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.save(updatedVehicle)).thenReturn(updatedVehicle);

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
	void whenUpdatingVehicleRepositoryException_thenStatusIsInternalServerError() {

		Vehicle updatedVehicle = new Vehicle(1, "VIN1", "UPDATED REG", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedVehicle,
				HttpStatus.INTERNAL_SERVER_ERROR);

		when(vehicleRepository.findById(updatedVehicle.getVehicleId()))
				.thenReturn(Optional.of(new Vehicle(1, "VIN1", "OLD REG", null)));

		ResponseEntity<Customer> customerClientResponse = new ResponseEntity<Customer>(
				new Customer(1, "Youssef", "Doha Qatar"), HttpStatus.OK);

		when(customerClient.findCustomer(1)).thenReturn(customerClientResponse);

		when(vehicleRepository.save(updatedVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.updateVehicle(updatedVehicle);

		assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode());
	}

	@Test
	void whenUpdatingNonExistingVehicle_thenStatusIsNotFound() {
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
	void whenDeletingExistingVehicle_thenStatusIsOK() {
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
	void whenDeletingNonExistingVehicle_thenStatusIsNotFound() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG", null);
		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(vehicleRepository.findById(vehicleToDelete.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService.deleteVehicle(vehicleToDelete.getVehicleId());

		assertAll(() -> assertEquals(actualResponse.getStatusCode(), expectedResponse.getStatusCode()),
				() -> assertNull(actualResponse.getBody()));
	}

	@Test
	void whenDeleteVehicleRepositoryException_thenStatusIsInternalServerError() {
		Vehicle vehicleToDelete = new Vehicle(1, "VIN1", "UPDATED REG", null);
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
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", Instant.now());

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		when(configProperties.getConnectionTimeoutMinutes()).thenReturn(1L);

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusNullPingDTM_thenStatusIsOKAndStringIsNotConnected() {
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
	void whenGetVehicleStatusOldPingDTM_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", Instant.now().minusSeconds(2*60*60));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenGetVehicleStatusNonExisting_thenStatusIsOKAndStringIsNotConnected() {
		ResponseEntity<String> expectedResponse = new ResponseEntity<String>("NOT CONNECTED", HttpStatus.OK);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", Instant.now().minusSeconds(2*60*60));

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = vehicleService
				.getVehicleConnectionStatus(existingVehicle.getVehicleId());

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenPingExistingVehicleStatusIsOKAndUpdatedVehicleIsReturned() {
		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(existingVehicle, HttpStatus.OK);

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		when(vehicleRepository.save(existingVehicle)).thenReturn(new Vehicle(1, "VIN1", "REGNO1", Instant.now()));

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
	void whenPingExistingVehicleAndRepositoryException_thenInternalServerErrorIsReturned() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		Vehicle existingVehicle = new Vehicle(1, "VIN1", "REGNO1", null);

		when(vehicleRepository.findById(existingVehicle.getVehicleId())).thenReturn(Optional.of(existingVehicle));

		when(vehicleRepository.save(existingVehicle)).thenThrow(RuntimeException.class);

		ResponseEntity<Object> actualResponse = vehicleService.ping(existingVehicle.getVehicleId());

		assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
	}

	@Test
	void whenPingNonExistingVehicleStatusIsNotFound() {

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.NOT_FOUND);

		when(vehicleRepository.findById("VIN1")).thenReturn(Optional.empty());

		ResponseEntity<Object> actualResponse = vehicleService.ping("VIN1");

		assertAll(() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()));
	}
}
