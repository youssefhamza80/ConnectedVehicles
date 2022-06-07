package com.youssef.connectedvehicles.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.youssef.connectedvehicles.entity.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, Integer>{
	List<Customer> findByName(String name);
}
