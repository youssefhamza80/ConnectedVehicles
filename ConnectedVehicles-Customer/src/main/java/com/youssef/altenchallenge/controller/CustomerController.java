package com.youssef.altenchallenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.service.CustomerService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin("*")
@Api(value = "Customer Controller APIs", tags = { "Customer Controller" })
@RestController
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}

	@ApiOperation(value = "Get all customers")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping
	public ResponseEntity<List<Customer>> getAllCustomers() {
		return customerService.findAll();
	}

	@ApiOperation(value = "Get a specific customer by customer Id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@GetMapping("/{id}")
	public ResponseEntity<Customer> findCustomer(@PathVariable long id) {
		return customerService.findById(id);
	}

	@ApiOperation(value = "Add a new customer to DB")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "CREATED"), @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PostMapping
	public ResponseEntity<Object> insertCustomer(@RequestBody Customer customer) {
		return customerService.insertNewCustomer(customer);
	}

	@ApiOperation(value = "Update an existing customer")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@PutMapping
	public ResponseEntity<Object> updateCustomer(@RequestBody Customer customer) {
		return customerService.updateCustomer(customer);
	}

	@ApiOperation(value = "Delete an existing customer from DB")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	@DeleteMapping("/{customerId}")
	public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
		return customerService.deleteCustomer(customerId);
	}
}
