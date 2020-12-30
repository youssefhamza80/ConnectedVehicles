package com.youssef.altenchallenge.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.service.CustomerService;

@RestController
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		super();
		this.customerService = customerService;
	}

	@GetMapping
	public List<Customer> getAllCustomers() {
		return customerService.findAll();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Customer> findCustomer(@PathVariable long id) {
		return customerService.findById(id);
	}

	@PostMapping("/new")
	public ResponseEntity<Customer> insertCustomer(@RequestBody Customer customer) {
		return customerService.insertNewCustomer(customer);
	}
	
	@DeleteMapping("/{customerId}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long customerId){
		return customerService.deleteCustomer(customerId);
	}
}
