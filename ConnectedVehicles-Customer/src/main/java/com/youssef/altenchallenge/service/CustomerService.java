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
			return new ResponseEntity<>(customer.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Customer> findByName(String name) {
		List<Customer> customers = customerRepository.findByName(name);
		if (!customers.isEmpty()) {
			return new ResponseEntity<>(customers.get(0), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	public ResponseEntity<Object> insertNewCustomer(Customer customer) {
		try {
			ResponseEntity<Customer> existingCustomer = findByName(customer.getName());
			if (existingCustomer.getStatusCode() == HttpStatus.OK) {
				return new ResponseEntity<>(String.format("Customer '%s' already exists", customer.getName()),
						HttpStatus.BAD_REQUEST);
			}
			customer.setId(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME));
			customerRepository.save(customer);
			return new ResponseEntity<>(customer, HttpStatus.CREATED);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Object> updateCustomer(Customer customer) {
		try {
			ResponseEntity<Customer> existingCustomer = findById(customer.getId());
			if (existingCustomer.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>(String.format("Customer '%d' is not found", customer.getId()),
						HttpStatus.NOT_FOUND);
			}
			customerRepository.save(customer);
			return new ResponseEntity<>(customer, HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<String> deleteCustomer(long customerId) {
		try {
			ResponseEntity<Customer> existingCustomer = findById(customerId);
			if (existingCustomer.getStatusCode() != HttpStatus.OK) {
				return new ResponseEntity<>(String.format("Customer '%d' is not found", customerId),
						HttpStatus.NOT_FOUND);
			}
			customerRepository.deleteById(customerId);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
