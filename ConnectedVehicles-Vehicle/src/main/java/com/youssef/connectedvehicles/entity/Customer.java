package com.youssef.connectedvehicles.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class Customer {

	@NonNull
	long id;

	@NonNull
	String name;

	String address;
}
