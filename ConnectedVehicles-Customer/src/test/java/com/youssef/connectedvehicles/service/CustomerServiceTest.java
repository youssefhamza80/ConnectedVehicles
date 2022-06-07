package com.youssef.connectedvehicles.service;

import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.repository.CustomerRepository;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SpringBootTest
@RunWith(SpringRunner.class)
@FieldDefaults(level = PRIVATE)
class CustomerServiceTest {

	@MockBean
	CustomerRepository customerRepository;

	@MockBean
	SequenceGeneratorService sequenceGeneratorService;

	@Autowired
	CustomerService customerService;

	@Test
	void whenCallingGetAllCustomers_thenReturnAllCustomers() {
		List<Customer> expectedCustomers = List.of(
				new Customer(1, "Youssef", "Doha Qatar"),
				new Customer(2, "Daniel", "Berlin Germany")
		);

		when(customerRepository.findAll()).thenReturn(expectedCustomers);

		List<Customer> actualCustomers = customerService.findAll();

		assertEquals(expectedCustomers, actualCustomers);
	}

	@Test
	void whenAddingNewCustomer_thenStatusIsCreatedAndBodyIsCorrect() {

		Customer newCustomer = new Customer(0, "Youssef", "Doha Qatar");

		Customer expectedNewCustomer = new Customer(1, "Youssef", "Doha Qatar");

		when(customerRepository.save(newCustomer)).thenReturn(expectedNewCustomer);

		when(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME)).thenReturn(1);

		Customer actualReturnedCustomer = customerService.insertNewCustomer(newCustomer);

		assertEquals(expectedNewCustomer, actualReturnedCustomer);

	}

	@Test
	void whenAddingNewCustomerAndInternalExceptionThrown_thenStatusIsInternalServerError() {

		Customer newCustomer = new Customer(0, "Youssef", "Doha Qatar");

		Customer expectedCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<Object> expectedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

		when(customerRepository.findByName(newCustomer.getName()))
				.thenThrow(new RuntimeException("Cannot retrieve customer data from DB"));

		when(customerRepository.save(newCustomer)).thenReturn(expectedCustomer);

		when(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME)).thenReturn(1);

		Customer actualReturnedCustomer = customerService.insertNewCustomer(newCustomer);

		assertEquals(expectedCustomer, actualReturnedCustomer);
	}

	@Test
	void whenUpdatingExistingCustomer_thenStatusIsOK() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");
		Customer expectedUpdatedCustomer = new Customer(1, "Youssef Hamza", "Doha Qatar");

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));

		when(customerRepository.save(expectedUpdatedCustomer)).thenReturn(expectedUpdatedCustomer);

		Customer actualUpdatedCustomer = customerService.updateCustomer(expectedUpdatedCustomer);

		assertEquals(expectedUpdatedCustomer, actualUpdatedCustomer);
	}

	@Test
	void whenUpdatingExistingCustomerAndInternalExceptionThrown_thenStatusIsInternalServerError() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");
		Customer updatedCustomer = new Customer(1, "Youssef Hamza", "Doha Qatar");

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));
		when(customerRepository.save(updatedCustomer))
				.thenThrow(new RuntimeException("Exception while updating customer instance"));

		ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, ()-> customerService.updateCustomer(updatedCustomer));

		assertEquals(INTERNAL_SERVER_ERROR, thrownException.getStatus());

	}

	@Test
	void whenUpdatingNonExistingCustomer_thenStatusIsNotFound() {

		Customer customerToUpdate = new Customer(1, "Youssef Hamza", "Doha Qatar");

		when(customerRepository.findById(customerToUpdate.getId())).thenReturn(Optional.empty());

		ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, ()->customerService.updateCustomer(customerToUpdate));

		assertEquals(NOT_FOUND, thrownException.getStatus());
	}

	@Test
	void whenDeletingExistingCustomer_thenStatusIsOK() {

		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>(HttpStatus.OK);

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));

		assertDoesNotThrow(()->customerService.deleteCustomer(existingCustomer.getId()));

	}

	@Test
	void whenDeletingNonExistingCustomer_thenStatusIsNotFound() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		ResponseEntity<String> expectedResponse = new ResponseEntity<>(
				String.format("Customer '%d' is not found", existingCustomer.getId()), HttpStatus.NOT_FOUND);

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.empty());


		ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, ()->customerService.deleteCustomer(existingCustomer.getId()));

		assertEquals(NOT_FOUND, thrownException.getStatus());
	}

	@Test
	void whenCustomerRepositoryDeleteException_thenStatusIsInternalServerError() {
		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		when(customerRepository.findById(existingCustomer.getId())).thenReturn(Optional.of(existingCustomer));

		doThrow(RuntimeException.class).when(customerRepository).deleteById(existingCustomer.getId());

		ResponseStatusException thrownException = assertThrows(ResponseStatusException.class, ()->customerService.deleteCustomer(existingCustomer.getId()));

		assertEquals(INTERNAL_SERVER_ERROR, thrownException.getStatus());
	}
}
