package com.youssef.altenchallenge.controller;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.delete;
import static io.restassured.RestAssured.given;
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
import java.util.Optional;

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

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.repository.CustomerRepository;
import com.youssef.altenchallenge.service.CustomerService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CustomerControllerTest {

	@LocalServerPort
	private int port;

	private String uri;

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

		List<Customer> customers = new ArrayList<>();

		customers.add(new Customer(1, "Youssef", "Doha Qatar"));
		customers.add(new Customer(2, "Daniel", "Gothenberg Sweden"));

		when(customerService.findAll()).thenReturn(customers);

		get(uri).then().statusCode(HttpStatus.OK.value()).assertThat().body("size()", is(2));
	}

	@Test
	public void whenAddingNewCustomer_thenStatusIsCreatedAndBodyIsCorrect() {

		List<Customer> customers = new ArrayList<>();
		Customer newCustomer = new Customer(1, "Youssef", "Doha Qatar");
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(newCustomer, HttpStatus.CREATED);
		when(customerService.insertNewCustomer((Customer) any(Customer.class))).thenReturn(responseEntity);

		Map<String, String> request = new HashMap<>();
		request.put("name", "Youssef");
		request.put("address", "Doha Qatar");
		// when(customerService.findAll()).thenReturn(customers);

		Customer retrievedCustomer = given().contentType("application/json").body(request).when().post(uri).then()
				.statusCode(HttpStatus.CREATED.value()).extract().as(Customer.class);

		assertAll(() -> assertNotNull(retrievedCustomer),
				() -> assertEquals(retrievedCustomer.getId(), newCustomer.getId()),
				() -> assertEquals(retrievedCustomer.getName(), newCustomer.getName()),
				() -> assertEquals(retrievedCustomer.getAddress(), newCustomer.getAddress()));
	};

	@Test
	public void whenAddingDuplicatedCustomer_thenStatusIsBadRequest() {

		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
		when(customerService.insertNewCustomer(any(Customer.class))).thenReturn(responseEntity);
		Map<String, String> request = new HashMap<>();
		request.put("name", "Youssef");
		request.put("address", "Doha Qatar");
		
		given().contentType("application/json").body(request).when().post(uri).then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void whenUpdatingExistingCustomer_thenStatusIsOk() {

		ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.OK);
		when(customerService.updateCustomer(any(Customer.class))).thenReturn(responseEntity);
		Map<String, String> request = new HashMap<>();
		request.put("name", "Youssef");
		request.put("address", "Doha Qatar");
		
		given().contentType("application/json").body(request).when().put(uri).then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void whenUpdatingNonExistingCustomer_thenStatusIsNotFound() {
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		when(customerService.updateCustomer(any(Customer.class))).thenReturn(responseEntity);
		Map<String, String> request = new HashMap<>();
		request.put("name", "Youssef");
		request.put("address", "Doha Qatar");
		
		given().contentType("application/json").body(request).when().put(uri).then()
				.statusCode(HttpStatus.BAD_REQUEST.value());
	}

	@Test
	public void whenDeletingExistingCustomer_thenStatusIsOk() {

		ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.OK);
		when(customerService.deleteCustomer(1)).thenReturn(responseEntity);
		
		delete(uri+"/1").then().statusCode(HttpStatus.OK.value());
	}

	@Test
	public void whenDeletingNonExistingCustomer_thenStatusIsNotFound() {
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		when(customerService.deleteCustomer(1)).thenReturn(responseEntity);

		delete(uri+"/1").then().statusCode(HttpStatus.NOT_FOUND.value());
	}
}