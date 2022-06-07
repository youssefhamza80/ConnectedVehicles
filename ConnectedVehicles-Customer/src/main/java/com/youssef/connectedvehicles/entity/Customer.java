package com.youssef.connectedvehicles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "Customers")
@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class Customer {
	
	@Transient
    public static final String SEQUENCE_NAME = "customers_sequence";

	@Id
	Integer id;

	String name;

	String address;

}
