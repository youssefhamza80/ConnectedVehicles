package com.youssef.connectedvehicles.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import static lombok.AccessLevel.PRIVATE;

@Document(collection = "Customers")
@Data
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Customer {
	
	@Transient
    public static final String SEQUENCE_NAME = "customers_sequence";

	@Id
	long id;

	String name;

	String address;

}
