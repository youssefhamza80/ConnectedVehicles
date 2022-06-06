package com.youssef.connectedvehicles.service;

import java.util.List;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.repository.CustomerRepository;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class CustomerService {

	final SequenceGeneratorService sequenceGeneratorService;

	final CustomerRepository customerRepository;

	public ResponseEntity<List<Customer>> findAll() {
		try {
			return new ResponseEntity<>(customerRepository.findAll(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public ResponseEntity<Customer> findById(long id) {
		Optional<Customer> customer = customerRepository.findById(id);

		return customer
				.map(foundCustomer -> ResponseEntity.ok(foundCustomer))
				.orElseGet(() -> ResponseEntity.notFound().build());
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
			customer = customerRepository.save(customer);
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
			customer = customerRepository.save(customer);
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
