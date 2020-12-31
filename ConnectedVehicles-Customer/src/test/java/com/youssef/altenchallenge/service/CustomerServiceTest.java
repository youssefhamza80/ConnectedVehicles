package com.youssef.altenchallenge.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.repository.CustomerRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
class CustomerServiceTest {

	@MockBean
	CustomerRepository customerRepository;

	@MockBean
	SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	CustomerService customerService;

	@Test
	void whenCallingGetAllCustomers_thenReturnAllCustomers() {
		List<Customer> expectedCustomers = new ArrayList<>();

		expectedCustomers.add(new Customer(1, "Youssef", "Doha Qatar"));
		expectedCustomers.add(new Customer(2, "Daniel", "Gothenberg Sweden"));

		when(customerRepository.findAll()).thenReturn(expectedCustomers);

		List<Customer> actualCustomers = customerService.findAll();

		assertAll(() -> assertNotNull(actualCustomers), () -> assertEquals(2, actualCustomers.size()),
				() -> assertEquals(expectedCustomers, actualCustomers));
	}

	@Test
	void whenAddingNewCustomer_thenStatusIsCreatedAndBodyIsCorrect() {

		Customer newCustomer = new Customer(0, "Youssef", "Doha Qatar");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(newCustomer, HttpStatus.CREATED);

		when(customerRepository.findByName(newCustomer.getName())).thenReturn(new ArrayList<Customer>());

		when(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME)).thenReturn(1L);

		ResponseEntity<Object> actualResponse = customerService.insertNewCustomer(newCustomer);

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertTrue(actualResponse.getBody() instanceof Customer),
				() -> assertEquals(1L, ((Customer) actualResponse.getBody()).getId()),
				() -> assertEquals(((Customer) expectedResponse.getBody()).getName(),
						((Customer) actualResponse.getBody()).getName()),
				() -> assertEquals(((Customer) expectedResponse.getBody()).getName(),
						((Customer) actualResponse.getBody()).getName()));
	};

	@Test
	void whenAddingDuplicatedCustomer_thenStatusIsBadRequest() {

		List<Customer> existingCustomers = new ArrayList<>();

		existingCustomers.add(new Customer(1, "Youssef", "Doha Qatar"));

		Customer newCustomer = new Customer(0, "Youssef", "Doha Qatar");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<Object>(
				String.format("Customer '%s' already exists", newCustomer.getName()), HttpStatus.BAD_REQUEST);

		when(customerRepository.findByName(newCustomer.getName())).thenReturn(existingCustomers);

		when(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME)).thenReturn(2L);

		ResponseEntity<Object> actualResponse = customerService.insertNewCustomer(newCustomer);

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenUpdatingExistingCustomer_thenStatusIsOK() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");
		Customer updatedCustomer = new Customer(1, "Youssef Hamza", "Doha Qatar");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(updatedCustomer, HttpStatus.OK);

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));

		ResponseEntity<Object> actualResponse = customerService.updateCustomer(updatedCustomer);

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertTrue(actualResponse.getBody() instanceof Customer),
				() -> assertEquals(((Customer) expectedResponse.getBody()).getId(),
						((Customer) actualResponse.getBody()).getId()),
				() -> assertEquals(((Customer) expectedResponse.getBody()).getName(),
						((Customer) actualResponse.getBody()).getName()),
				() -> assertEquals(((Customer) expectedResponse.getBody()).getName(),
						((Customer) actualResponse.getBody()).getName()));
	}

	@Test
	void whenUpdatingNonExistingCustomer_thenStatusIsNotFound() {

		Customer customerToUpdate = new Customer(1, "Youssef Hamza", "Doha Qatar");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(
				String.format("Customer '%d' is not found", customerToUpdate.getId()), HttpStatus.NOT_FOUND);

		when(customerRepository.findById(customerToUpdate.getId())).thenReturn(Optional.empty());

		ResponseEntity<Object> actualResponse = customerService.updateCustomer(customerToUpdate);

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenDeletingExistingCustomer_thenStatusIsOK() {

		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>(HttpStatus.OK);

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));

		ResponseEntity<String> actualResponse = customerService.deleteCustomer(existingCustomer.getId());

		assertAll(() -> assertNotNull(actualResponse), () -> assertNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()));

	}

	@Test
	void whenDeletingNonExistingCustomer_thenStatusIsNotFound() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>(
				String.format("Customer '%d' is not found", existingCustomer.getId()), HttpStatus.NOT_FOUND);

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.empty());

		ResponseEntity<String> actualResponse = customerService.deleteCustomer(existingCustomer.getId());

		assertAll(() -> assertNotNull(actualResponse),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()),
				() -> assertEquals(expectedResponse.getBody(), actualResponse.getBody()));
	}

	@Test
	void whenCustomerRepositoryDeleteException_thenStatusIsInternalServerError() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		when(customerRepository.findById(existingCustomer.getId()))
				.thenThrow(new RuntimeException("Exception while retrieving data from repository"));

		ResponseEntity<String> actualResponse = customerService.deleteCustomer(existingCustomer.getId());

		assertAll(() -> assertNotNull(actualResponse), () -> assertNotNull(actualResponse.getBody()),
				() -> assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode()));
	}
}
