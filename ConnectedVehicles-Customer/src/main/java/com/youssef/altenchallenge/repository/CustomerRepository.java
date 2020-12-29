package com.youssef.altenchallenge.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.youssef.altenchallenge.entity.Customer;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, Long>{
	List<Customer> findByName(String name);
}
