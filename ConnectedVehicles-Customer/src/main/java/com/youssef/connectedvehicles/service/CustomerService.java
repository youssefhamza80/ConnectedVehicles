package com.youssef.connectedvehicles.service;

import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerService {

	SequenceGeneratorService sequenceGeneratorService;

	CustomerRepository customerRepository;

	public List<Customer> findAll() {
		try {
			return customerRepository.findAll();
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	public Customer findById(int id) {
		Optional<Customer> customer = customerRepository.findById(id);

		return customer.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public Customer insertNewCustomer(Customer customer) {
		try {
			customer.setId(sequenceGeneratorService.generateSequence(Customer.SEQUENCE_NAME));
			return customerRepository.save(customer);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	public Customer updateCustomer(Customer customer) {
		Customer existingCustomer = findById(customer.getId());
		try {
			return customerRepository.save(customer);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	public void deleteCustomer(int customerId) {
		Customer existingCustomer = findById(customerId);
		try {
			customerRepository.deleteById(customerId);
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
}
