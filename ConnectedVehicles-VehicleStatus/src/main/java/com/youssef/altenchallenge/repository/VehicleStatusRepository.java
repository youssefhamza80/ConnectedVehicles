package com.youssef.altenchallenge.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.youssef.altenchallenge.entity.VehicleStatus;

@Repository
public interface VehicleStatusRepository extends MongoRepository<VehicleStatus, String>{
	
}
