package com.youssef.connectedvehicles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class ConnectedVehiclesDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectedVehiclesDiscoveryApplication.class, args);
	}

}
