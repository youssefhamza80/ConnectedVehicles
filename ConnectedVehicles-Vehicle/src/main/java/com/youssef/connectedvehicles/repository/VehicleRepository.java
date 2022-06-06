package com.youssef.connectedvehicles.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.youssef.connectedvehicles.entity.Vehicle;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String>{
	
}
