package com.youssef.altenchallenge.entity;

public class Customer {
	
	private long id;

	private String name;

	private String address;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Customer() {
		super();
	}

	public Customer(long id, String name, String address) {
		this.id = id;
		this.name = name;
		this.address = address;
	}
}
