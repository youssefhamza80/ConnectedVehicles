package com.youssef.altenchallenge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.altenchallenge.entity.Customer;
import com.youssef.altenchallenge.repository.CustomerRepository;

@Service
public class CustomerService {

	private final SequenceGeneratorService sequenceGeneratorService;

	private final CustomerRepository customerRepository;

	public CustomerService(CustomerRepository customerRepository, SequenceGeneratorService sequenceGeneratorService) {
		super();
		this.sequenceGeneratorService = sequenceGeneratorService;
		this.customerRepository = customerRepository;
	}

	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	public ResponseEntity<Customer> findById(long id) {
		Optional<Customer> customer = customerRepository.findById(id);

		if (customer.isPresent()) {
			return new ResponseEntity<>(customer.get(), HttpStatus.FOUND);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Customer> findByName(String name) {
		List<Customer> customers = customerRepository.findByName(name);

		if (!customers.isEmpty()) {
			return new ResponseEntity<>(customers.get(0), HttpStatus.FOUND);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Customer> insertNewCustomer(Customer customer) {
		try {
			ResponseEntity<Customer> existingCustomer = findByName(customer.getName());
			if (existingCustomer.getStatusCode() == HttpStatus.FOUND) {
				throw new IllegalArgumentException(String.format("Customer '%s' already exists", customer.getName()));
			}
			customer.setId(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME));
			customer = customerRepository.save(customer);
			return new ResponseEntity<>(customer, HttpStatus.CREATED);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
	}

	public ResponseEntity<Void> updateCustomer(Customer customer) {
		try {
			customerRepository.save(customer);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Void> deleteCustomer(long customerId) {
		try {
			customerRepository.deleteById(customerId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
