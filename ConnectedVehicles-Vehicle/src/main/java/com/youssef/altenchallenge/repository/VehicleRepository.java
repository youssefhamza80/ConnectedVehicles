package com.youssef.altenchallenge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.youssef.altenchallenge.entity.Vehicle;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String>{
	
}
