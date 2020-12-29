package com.youssef.altenchallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConnectedVehiclesVehicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectedVehiclesVehicleApplication.class, args);
	}

}
