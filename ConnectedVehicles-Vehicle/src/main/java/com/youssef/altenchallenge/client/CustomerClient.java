package com.youssef.altenchallenge.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.youssef.altenchallenge.entity.Customer;

@FeignClient(name = "CONNECTED-VEHICLES-CUSTOMER")
public interface CustomerClient {
	@GetMapping("/{id}")
	public ResponseEntity<Customer> findCustomer(@PathVariable long id);
}