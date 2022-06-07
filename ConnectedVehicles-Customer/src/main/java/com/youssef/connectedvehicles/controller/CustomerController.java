package com.youssef.connectedvehicles.controller;

import com.youssef.connectedvehicles.entity.Customer;
import com.youssef.connectedvehicles.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@Api(value = "Customer Controller APIs", tags = {"Customer Controller"})
@RestController
@AllArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerController {

    CustomerService customerService;

    @ApiOperation(value = "Get all customers")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping
    @ResponseStatus(OK)
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @ApiOperation(value = "Get a specific customer by customer Id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public Customer findCustomer(@PathVariable Integer id) {
        return customerService.findById(id);
    }

    @ApiOperation(value = "Add a new customer to DB")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "CREATED"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @PostMapping
    @ResponseStatus(CREATED)
    public Customer insertCustomer(@RequestBody Customer customer) {
        return customerService.insertNewCustomer(customer);
    }

    @ApiOperation(value = "Update an existing customer")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @PutMapping
    @ResponseStatus(OK)
    public Customer updateCustomer(@RequestBody Customer customer) {
        return customerService.updateCustomer(customer);
    }

    @ApiOperation(value = "Delete an existing customer from DB")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @DeleteMapping("/{customerId}")
    @ResponseStatus(OK)
    public void deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
    }
}
