# Connected Vehicles Platform

## Introduction

It is required to design and implement vehicle connectivity monitoring dashboard platform. There are a number of connected vehicles that belongs to a number of customers and it is needed to view the status of the connection for these vehicles on a monitoring display.
The vehicles send the status of the connection - ping/heartbeat - one time per minute. If the heartbeat was not received by the vehicle for more than 1 minute, it means no connection.


## Solution Description
This solution applies modern microservices architecture principles. It is designed to achieve cloud-native scalability and high availability with minimal efforts. Below I am going to demonstrate the different aspects of the solution.

## Architecture Diagram
![Architecture Diagram](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Architecture.jpg?raw=true)


As per the above architecture, there are 5 backend microservices including API gateway service which works as the edge service in this solution for communication with the outside world (i.e. front end application and vehicles IOT devices).
There's one front-end monitoring application to view statuses of all vehicles in the system.
Also each vehicle can communicate individually with the backend via the API Gateway service to send ping/heartbeat messages.

Hereunder, I am going to describe in details all components listed in the above architecture diagram.

***Note***: Prior to reading the below sections, it is highly recommended that you run the solution locally by following in order to navigate URLs and explore implemented services interactively. 
To run the solution locally, please follow [these steps](#how-to-run).

### Customer Service
This REST service is responsible for handling all customer-related CRUD operations. It connects to a Customer DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advanced scalability and availability options can be provided with paid plans.

#### Customer Data Model

 1. **Id**: Unique identifier of the customer.
 2. **Name**:  Full customer name.
 3. **Address**: Customer address.

Standard CRUD operations are provided by this REST service:
 1. **Query all customers**: GET method that returns all available customers in the DB.
 2. **Query a specific customer by Id**: GET method that returns a specific customer data using Id.
 3. **Update an existing customer**: PUT method that updates an existing customer data (i.e. modifies Name and/or Address if required).
 4. **Delete an existing customer by Id**: DELETE method that deletes one customer given his/her Id.

![Customer APIs and data model](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Customer%20APIs.JPG?raw=true)

Full REST documentation is available [here](http://localhost:7000/connected_vehicles/customer/swagger-ui/index.html) - assuming all services are running on localhost with default ports -.

### Vehicle Service
This REST service is responsible for handling all vehicle-related CRUD operations. It connects to Vehicle DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advanced scalability and availability options can be provided with paid plans.

#### Vehicle Data Model

 1. **Vehicle Id/VIN**: Unique identifier of the vehicle.
 2. **Customer Id**:  Customer Id which links a vehicle object to a customer object. Since this is a NoSQL DB, handling primary/foreign key consistency is done on code level not DB level.
 3. **Registration Number**: Vehicle registration number.
 4. **Ping Date/Time**:  The last ping date/time sent by the vehicle. It is used to determine connection status with the vehicle. If it is less than the pre-defined duration (defaulted to 1 minute), then connection status is CONNECTED. Otherwise, connection status is NOT CONNECTED.
 5. **Connection Status**: This is a String field to represent "CONNECTED" or "NOT CONNECTED". This field is not stored in the DB and being computed every time a vehicle is queried by the REST APIs.
   
Standard CRUD operations are provided by this REST service:
 1. **Query all vehicles**: GET method that returns all available vehicles in the DB.
 2. **Query a specific vehicle by Vehicle Id**: GET method that returns a specific vehicle data using Vehicle Id.
 3. **Update an existing vehicle**: PUT method that updates an existing vehicle data (i.e. modifies Name and/or Address if required).
 4. **Delete an existing vehicle by Id**: DELETE method that deletes one vehicle given its vehicle Id/VIN.

Besides standard CRUD operations, there are additional two operations that are specific to this platform.
  1. **Get connection status given vehicle Id**: GET method that returns a vehicle connection status (CONNECTED or NOT CONNECTED) given its vehicle Id/VIN.
  2. **Ping using Vehicle Id**: PUT method that updates vehicle object by setting its ping date/time to the ping request date/time. As described above, the ping date/time determines whether the vehicle is CONNECTED or NOT CONNECTED.

![Vehicle APIs and data model](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Vehicle%20APIs.JPG?raw=true)

Full REST documentation is available [here](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html) - assuming all services are running on localhost with default ports -.

### Registry/Discovery service: 
This service works as a discovery/registry service. It used to provide a discovery mechanism for any client that needs to communicate to a specific service without needing to know the specific end-point(s) for the target service. Any target service needs to register to this discovery/registry service so that it's accessible by any client.

This discovery service also provides a dashboard with some useful information about registered services and their statuses as shown below.

[Netflix Eureka](https://github.com/Netflix/eureka) is used as registry/discovery server for this solution.

![Discovery Service Dashboard](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Discovery.JPG?raw=true)

The above dashboard is available [here](http://localhost:8761/) - assuming that all services are running on localhost with default ports -.

### Configuration service:
To comply with microservices architecture best practices, all services configurations should be centralized and detached from the services source code. I.e. whenever a service boots up, it should fetch its all related configuration from an external provider. This is the use of this Configuration service.
To maximize the benefit of this externalized configurations, all services configurations for this solution are uploaded and tracked by github [here](https://github.com/youssefhamza80/ConnectedVehicles_ConfigRepo). 
When this configuration service starts up, it loads all client services (such as Vehicle and Customer services)  from the above github repository.
When any client service starts up, it will fetch the configurations from this configuration service. For example, Customer service fetches its configuration during startup from [here](http://localhost:9000/connected-vehicles-customer/default) - assuming that all services are running on local host with default ports.   
 
### API Gateway: 
This service works as a proxy, edge service and routing application for the underlying services (such as Customer and Vehicle services). Instead of exposing different ports for each service, outside world can communicate only with this API gateway without worrying about implementation details of the actual running services. This gives us flexibility when it comes to developing new services or changing the underlying code-base structure.
This API gateway communicates with discovery/registry service to get end-points for registered client services. It also provides unified end-points to be exposed to the outside-world consumers.

In this platforms, outside-world consumers are:
- [ ] **Vehicle Monitoring Web App**: This application communicates with the API gateway end-points to extract vehicles and customer related information to be displayed to the user.
- [ ] **Vehicles**:  Vehicles communicates with the API gateway to provide heartbeats - i.e. pings - to the system and thus vehicles statuses are CONNECTED.

[Netflix Zuul](https://github.com/Netflix/zuul) is used as the API gateway for this solution. 
It is worth to mention the great scalability/availability options that are provided when integrating Netflix Zuul with Netflix Eureka; if we need to deploy and run another instance of any client service all is needed is to register it to service registry and then [Netflix Ribbon](https://github.com/Netflix/ribbon) load balancer will distribute the load between client services seamlessly.

### Vehicle Monitoring Web App:
This web application is the front-end part of the solution. [React JS](https://reactjs.org/) framework is used for UI development.

![Screenshot](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/MonitoringApp.JPG?raw=true) 
 
 You can run the application [here](http://localhost:3000/) assuming that all services are running on local host with default ports.
 
***Note***: I've integrated the web app with only vehicle service. That's why only customer ID appears in the web app. Web app can be improved further by integrating with customer service to display all customer details in the web page.
 
## Used Technologies

 - **Development framework**: [Spring boot](https://spring.io/projects/spring-boot) is used for all backend services development. Spring Boot is a [Spring](https://spring.io/) module that is widely used to develop cloud-native applications/services. It's backed with various integration mechanisms with many cloud applications providers - such as [Netflix cloud framework](https://spring.io/projects/spring-cloud-netflix) -. 

 - **Software Control Management**: 
   - Source code is tracked on Github repository [here](https://github.com/youssefhamza80/ConnectedVehicles).
   - Services configurations are published on Github repository [here](https://github.com/youssefhamza80/ConnectedVehicles_ConfigRepo).
   
 - **Building framework**: [Apache Maven](https://maven.apache.org/) is used to build and run automated tests for the project.
 - **Testing frameworks**: 
	 - [ ] [JUnit 5](https://junit.org/junit5/) framework is used for running unit/integration test suites.
	 - [ ] [RestAssured framework ](https://rest-assured.io/)is used for testing RESTful APIs controllers. 
	 - [ ] [Mockito framework](https://site.mockito.org/) is used to mock external dependencies. It's used for REST controllers as well services testing. 
	 
 - **REST APIs documentation**: [Swagger-UI](https://swagger.io/tools/swagger-ui/) is used to provide full REST APIs documentation with the ability to execute various HTTP methods for each API.

 - **Code quality analyzers**: 
     - [ ] [SonarLint ](https://www.sonarlint.org/) is used to check the code quality locally. That is to make sure that local source code does not have any quality issues prior to committing/pushing to the SCM repository. 
     - [ ] [SonarCloud ](https://sonarcloud.io/) is used to check source code quality after it's pushed to remote SCM (Github).
     
SonarCloud last build code quality analysis reports and statistics can be tracked using the below links:
>[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=alert_status)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=coverage)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=ncloc)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=security_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=code_smells)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)


- **Containerization**: [Docker ](https://www.docker.com/) is used to build and push services images to [Docker hub repository](https://hub.docker.com/) with each code push to github. This facilitates easy deployment on any machine that have Docker installed.

  All services are dockerized with each build and images are pushed automatically to [Docker Hub](https://hub.docker.com/).
  Docker images can be pulled to local machine using the following commands:
  
  >**Vehicle Service**: 
  `docker pull yousifkamal/cv_vehicle`
  >**Customer Service**: 
  `docker pull yousifkamal/cv_customer`
  >**Service Discovery and Registry service**: 
  `docker pull yousifkamal/cv_discovery`
  >**API Gateway Service**: 
  `docker pull yousifkamal/cv_apigateway`
  >**Configuration Server**: 
  `docker pull yousifkamal/cv_configserver`
  >**Front-end Vehicle Monitoring Application**: 
  `docker pull yousifkamal/cv_monitoring_app`


 - **CI/CD**: [Travis CI](https://travis-ci.com/) is used as the continuous integration platform for this solution. It performs the following tasks whenever new code is pushed to [github repository](https://github.com/youssefhamza80/ConnectedVehicles):
	  - [ ] Builds the source code using Maven.
	  - [ ] Runs automated test suites with all defined unit/integration test cases.
	  - [ ] Generates code coverage reports - using [Jacoco Maven plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)-.
	  - [ ] Invokes SonarCloud code quality checks and publish results to SonarCloud.
	  - [ ] Builds Docker images for all services and push them to docker hub remote repository.   

    Lat build status can be tracked here: 
    >[![Build Status](https://travis-ci.com/youssefhamza80/ConnectedVehicles.svg?branch=main)](https://travis-ci.com/youssefhamza80/ConnectedVehicles)
   
## How To Build

To build and run this solution locally using command line/shell terminal:
- **Pre-requisites**: [git](https://git-scm.com/downloads), [Maven](https://maven.apache.org/download.cgi), and [yarn](https://classic.yarnpkg.com/en/docs/install) tools are installed.

- **Steps to build**:
  - Copy repository to local machine: `git clone https://github.com/youssefhamza80/ConnectedVehicles.git`
  - Go to repository root directory: `cd ConnectedVehicles`
  - Run Maven to build all backend services: `mvn clean install`
 
## How To Run
 This solution can run by either running the dockerized solution, or starting up each service locally.
 
 1. To run the dockerized solution:
    - **Copy repository to local machine**: `git clone https://github.com/youssefhamza80/ConnectedVehicles.git`
    - **Go to repository root directory**: `cd ConnectedVehicles`
    - **Run Docker Compose to bring all services up**: `docker-compose -f .\docker-compose.yml up -d`
      ![](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Docker%20Up.jpg?raw=true)

      After all docker containers are up as per the above screenshot, you can run the monitoring application [here](http://localhost:3000/).

2. To start up services using command line/shell, please use the the following sequence:
     1. **Config server**: 
      `cd ConnectedVehicles-ConfigServer` 
      `mvn spring-boot:run`
     2. **Discovery/Registry server**:
	 `cd ../ConnectedVehicles-Discovery`
	 `mvn spring-boot:run`
     3. **Vehicle service**:
	 `cd ../ConnectedVehicles-Vehicle`
	 `mvn spring-boot:run`
     4. **Customer service**:
	 `cd ../ConnectedVehicles-Vehicle`
	 `mvn spring-boot:run`
     5. **API gateway**:
     `cd ../ConnectedVehicles-APIGateway`
     `mvn spring-boot:run`
     6. To startup the web application:
     `cd ../ConnectedVehicles-MonitoringDashboard`
     `yarn`
     `yarn start`
	 Application default URL is: [http://localhost:3000/](http://localhost:3000/)

To simulate vehicle status sending, you can place a ping request using vehicle Id using this [link](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html#/Vehicle%20Controller/pingUsingPUT). Or you may use [Postman](https://www.postman.com/downloads/) to place a PUT request to this url: http://localhost:7000/connected_vehicles/vehicle/ping/{vehicleId}

***Note***: The monitoring application refreshes automatically every 5 seconds. So, after ***ping*** request is received, vehicle status should become ***"CONNECTED"*** until 1 minute is elapsed, afterwards status should become ***"NOT CONNECTED"*** until another ***ping*** request is received.
I have used Google chrome for testing, if the monitoring application did not refresh automatically after issuing ping requests, please do a manual refresh to the web app or query vehicle by ID [here](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html#/Vehicle%20Controller/findVehicleUsingGET) to make sure that status has been changed to ***"CONNECTED"*** after issuing ping request.


<br><br><br><br>
>Written with [StackEdit](https://stackedit.io/)
