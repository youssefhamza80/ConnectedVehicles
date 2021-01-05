# Connected Vehicles Platform

## Introduction

This platform is used for monitoring vehicles statuses (Connected/Not Connected) via one dashboard. It uses modern software architectural principles and tools to provide a reliable well-built functionalities.

## Architecture
![Architecture Diagram](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Architecture.jpg?raw=true)

## Solution Description
This solution make use of modern microservices architecture principles. It is designed to achieve cloud-native scalability and high availability with minimal efforts.

Hereunder, I am going to describe each component of this diagram.

### Customer Service
This REST service is responsible for handling all customer-related CRUD operations. It connects to Customer DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advacened scalability and availability options can be provided with paid plans.

### Customer Data Model

 1. **Id**: Unique identifier for the customer.
 2. **Name**:  Full customer name.
 3. **Address**: Customer address.

Standard CRUD operations are provided by this REST service:
 1. **Query all customers**: GET method that returns all available customers in the DB.
 2. **Query a specific customer by Id**: GET method that returns a specific customer data using Id.
 3. **Update an existing customer**: PUT method that updates an existing customer data (i.e. modifies Name and/or Address if required).
 4. **Delete an existing customer by Id**: DELETE method that deletes one customer given his/her Id.

Full REST documentation with ability to execute requests are provided by navigating [here](http://localhost:7000/connected_vehicles/customer/swagger-ui/index.html) - assuming  all services run on localhost with default ports -.

### Vehicle Service
This REST service is responsible for handling all vehicle-related CRUD operations. It connects to Vehicle DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advacened scalability and availability options can be provided with paid plans.

### Vehicle Data Model

 1. **Vehicle Id/VIN**: Unique identifier for the vehicle.
 2. **Customer Id**:  Customer Id which links a vehicle object to a customer object . 
 3. **Registration Number**: Vehicle registration number.
 4. **Ping Date/Time**:  The last ping date/time sent by the vehicle. It is used to detremine connection status with the vehicle. If it is less than the pre-defined duration (defaulted to 1 minute), then connection status is CONNECTED. Otherwise, connection status is NOT CONNECTED.

Standard CRUD operations are provided by this REST service:
 1. **Query all vehicles**: GET method that returns all available vehicles in the DB.
 2. **Query a specific vehicle by Vehicle Id**: GET method that returns a specific vehicle data using Vehicle Id.
 3. **Update an existing vehicle**: PUT method that updates an existing vehicle data (i.e. modifies Name and/or Address if required).
 4. **Delete an existing vehicle by Id**: DELETE method that deletes one vehicle given its vehicle Id/VIN.

Besides standard CRUD operations, there are additional two operations that are specific to this platform.
  1. **Get connection status given vehicle Id**: GET method that returns a vehicle connection status (CONNECTED or NOT CONNECTED) given its vehicle Id/VIN.
  2. **Ping using Vehicle Id**: PUT method that updates vehicle object by setting its ping date/time to the ping request date/time. As describe earlier, the ping date/time detremines whether the vehicle is CONNECTED or NOT CONNECTED.

Full REST documentation are provided by navigating [here](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html) - assuming all services run on localhost with default ports -.


## Technologies

## How To Build


## How To Run


## Code Quality
>[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=alert_status)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=coverage)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=ncloc)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=security_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)
>[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=code_smells)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)

## Continuous Integration

> Project is built continuously using [Travis CI](https://travis-ci.com/).
Build status can be tracker here: [![Build Status](https://travis-ci.com/youssefhamza80/ConnectedVehicles.svg?branch=main)](https://travis-ci.com/youssefhamza80/ConnectedVehicles)

> All services are dockarized with each build and images are pushed automatically to [Docker Hub](https://hub.docker.com/).
> Images can be pulled to local machine using the following commands:
> Vehicle Service: `docker pull yousifkamal/cv_vehicle`
> Customer Service: `docker pull yousifkamal/cv_customer`
> Service Discovery and Registry service: `docker pull yousifkamal/cv_discovery`
> API Gateway Service: `docker pull yousifkamal/cv_apigateway`
> Configuration Server: `docker pull yousifkamal/cv_configserver`

## Documentation


>
>
> Written with [StackEdit](https://stackedit.io/)
