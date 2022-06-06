package com.youssef.connectedvehicles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableConfigurationProperties(com.youssef.connectedvehicles.configuration.ConfigProperties.class)
public class ConnectedVehiclesVehicleApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectedVehiclesVehicleApplication.class, args);
	}

}
