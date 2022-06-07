package com.youssef.connectedvehicles.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.youssef.connectedvehicles.entity.Customer;

@FeignClient(name = "CONNECTED-VEHICLES-CUSTOMER")
public interface CustomerClient {
	@GetMapping("/{id}")
	Customer findCustomer(@PathVariable long id);
}