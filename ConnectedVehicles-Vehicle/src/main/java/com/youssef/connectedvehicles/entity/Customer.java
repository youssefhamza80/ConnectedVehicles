package com.youssef.connectedvehicles.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class Customer {

	@NonNull
	Integer id;

	@NonNull
	String name;

	@NonNull
	String address;
}
