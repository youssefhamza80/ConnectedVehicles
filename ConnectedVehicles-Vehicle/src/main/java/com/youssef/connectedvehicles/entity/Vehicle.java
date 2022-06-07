package com.youssef.connectedvehicles.entity;

import java.time.Instant;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "Vehicles")
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@FieldDefaults(level = PRIVATE)
public class Vehicle {

	@Transient
	public static final String SEQUENCE_NAME = "vehicles_sequence";

	@NonNull
	Integer customerId;

	@Id
	@NonNull
	String vehicleId;

	@NonNull
	String regNo;

	Instant pingDtm;

	@Transient
	String connectionStatus;
}
