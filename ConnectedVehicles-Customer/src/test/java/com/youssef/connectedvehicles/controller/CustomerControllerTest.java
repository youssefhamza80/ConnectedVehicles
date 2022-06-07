package com.youssef.connectedvehicles.controller;

import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static lombok.AccessLevel.PRIVATE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.experimental.FieldDefaults;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.repository.CustomerRepository;
import com.youssef.connectedvehicles.service.CustomerService;
import org.springframework.web.server.ResponseStatusException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@FieldDefaults(level = PRIVATE)
public class CustomerControllerTest {

	@LocalServerPort
	int port;

	String uri;

	@MockBean
	CustomerService customerService;

	@MockBean
	CustomerRepository customerRepository;

	@PostConstruct
	public void init() {
		uri = "http://localhost:" + port;
	}

	@Test
	public void whenCallingGetAllCustomers_thenCorrect() {

		List<Customer> existingCustomers = List.of(
				new Customer(1, "Youssef", "Doha Qatar"),
				new Customer(2, "Daniel", "Berlin Germany"));

		when(customerService.findAll()).thenReturn(existingCustomers);

		get(uri).then().statusCode(HttpStatus.OK.value()).assertThat().body("size()", is(2));
	}

	@Test
	public void whenFindingExistingCustomer_thenStatusIsOKAndBodyIsCorrect() {

		Customer existingCustomer = new Customer(1, "Youssef", "Doha Qatar");

		when(customerService.findById(existingCustomer.getId())).thenReturn(existingCustomer);

		Customer retrievedCustomer = get(uri + "/1").then().statusCode(HttpStatus.OK.value()).extract()
				.as(Customer.class);

		assertAll(() -> assertNotNull(retrievedCustomer),
				() -> assertEquals(existingCustomer.getId(), retrievedCustomer.getId()),
				() -> assertEquals(existingCustomer.getName(), retrievedCustomer.getName()),
				() -> assertEquals(existingCustomer.getAddress(), retrievedCustomer.getAddress()));
	}

	@Test
	public void whenAddingNewCustomer_thenStatusIsCreatedAndBodyIsCorrect() {
		Customer newCustomer = new Customer(1, "Youssef", "Doha Qatar");
		when(customerService.insertNewCustomer(any(Customer.class))).thenReturn(newCustomer);

		Customer retrievedCustomer = given().contentType("application/json").body(newCustomer).when().post(uri).then()
				.statusCode(HttpStatus.CREATED.value()).extract().as(Customer.class);

		assertAll(() -> assertNotNull(retrievedCustomer),
				() -> assertEquals(retrievedCustomer.getId(), newCustomer.getId()),
				() -> assertEquals(retrievedCustomer.getName(), newCustomer.getName()),
				() -> assertEquals(retrievedCustomer.getAddress(), newCustomer.getAddress()));
	}

	@Test
	public void whenUpdatingExistingCustomer_thenStatusIsOK() {
		Customer existingCustomer = new Customer(1, "Youssef", "Germany");
		when(customerService.updateCustomer(any(Customer.class))).thenReturn(existingCustomer);

		given().contentType("application/json").body(existingCustomer).when().put(uri).then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void whenUpdatingNonExistingCustomer_thenStatusIsNotFound() {

		Customer customerToUpdate = new Customer(1, "Youssef", "Germany");

		when(customerService.updateCustomer(customerToUpdate)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

		given().contentType("application/json").body(customerToUpdate).when().put(uri).then()
				.statusCode(HttpStatus.NOT_FOUND.value());
	}

	@Test
	public void whenDeletingExistingCustomer_thenStatusIsOK() {
		Customer existingCustomer = new Customer(1, "Youssef", "Germany");

		when(customerService.findById(1)).thenReturn(existingCustomer);

		delete(uri + "/1").then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void whenDeletingNonExistingCustomer_thenStatusIsNotFound() {

		Mockito.doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(customerService).deleteCustomer(1);

		delete(uri + "/1").then().statusCode(HttpStatus.NOT_FOUND.value());
	}
}