

# Connected Vehicles Platform

## Introduction

It is required to design and implement vehicle connectivity monitoring dashboard platform. There are a number of connected vehicles that belongs to a number of customers and it is needed to view the status of the connection for these vehicles on a monitoring display.
The vehicles send the status of the connection - ping/heartbeat - one time per minute. If the heartbear was not received by the vehicle for more than 1 minute, it means no connection.

## Requirements
1. Web GUI (Single Page Application Framework/Platform).

   - An overview of all vehicles should be visible on one page (full-screen display), together with their status.

   - It should be able to filter, to only show vehicles for a specific customer.

   - It should be able to filter, to only show vehicles that have a specific status.

2. Random simulation to vehicles status sending.

3. If database design will consume a lot of time, use data in-memory representation.

4. Unit Testing.

5. .NET Core, Java or any native language.

6. Complete analysis for the problem.

 - Full architectural sketch to solution.

 - Analysis behind the solution design, technologies,....

 - How the solution will make use of cloud.

 - Deployment steps.

## Solution Description
This solution make use of modern microservices architecture principles. It is designed to achieve cloud-native scalability and high availability with minimal efforts. Below I am going to demonstrate different aspects of the solution.

## Architecture Diagram
![Architecture Diagram](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Architecture.jpg?raw=true)


As per the above architecture, there are 5 backend microservices including API gateway service which works as the edge service in this solution to be called by the outside world (i.e. front end application).
There's one front-end monitoring application to view statuses of all vehicles in the system.
Each vehicle can communicate individually with the backend via the API Gateway service to send ping requests. 

Hereunder, I am going to describe all components listed in the above architecture in details.

Prior to reading the below sections, it is highly recommended that you run the solution in order to nagivate URLs and explore implemented services interactively.

### Customer Service
This REST service is responsible for handling all customer-related CRUD operations. It connects to a Customer DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advacened scalability and availability options can be provided with paid plans.

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
This REST service is responsible for handling all vehicle-related CRUD operations. It connects to Vehicle DB which is a Mongo DB collection hosted on a cluster provided by [Mongo Atlas](https://www.mongodb.com/cloud/atlas). This free NoSQL DB is high available as it's replicated on multiple hosts in the same region. However, more advacened scalability and availability options can be provided with paid plans.

#### Vehicle Data Model

 1. **Vehicle Id/VIN**: Unique identifier of the vehicle.
 2. **Customer Id**:  Customer Id which links a vehicle object to a customer object. Since this is a NoSQL DB, handling primary/foreign key consistency is done on code level not DB level.
 3. **Registration Number**: Vehicle registration number.
 4. **Ping Date/Time**:  The last ping date/time sent by the vehicle. It is used to detremine connection status with the vehicle. If it is less than the pre-defined duration (defaulted to 1 minute), then connection status is CONNECTED. Otherwise, connection status is NOT CONNECTED.
 5. **Connection Status**: This is a String field to represent "CONNECTED" or "NOT CONNECTED". This field is not stored in the DB and being computed everytime a vehicle is queried by the REST APIs.
   
Standard CRUD operations are provided by this REST service:
 1. **Query all vehicles**: GET method that returns all available vehicles in the DB.
 2. **Query a specific vehicle by Vehicle Id**: GET method that returns a specific vehicle data using Vehicle Id.
 3. **Update an existing vehicle**: PUT method that updates an existing vehicle data (i.e. modifies Name and/or Address if required).
 4. **Delete an existing vehicle by Id**: DELETE method that deletes one vehicle given its vehicle Id/VIN.

Besides standard CRUD operations, there are additional two operations that are specific to this platform.
  1. **Get connection status given vehicle Id**: GET method that returns a vehicle connection status (CONNECTED or NOT CONNECTED) given its vehicle Id/VIN.
  2. **Ping using Vehicle Id**: PUT method that updates vehicle object by setting its ping date/time to the ping request date/time. As described above, the ping date/time detremines whether the vehicle is CONNECTED or NOT CONNECTED.

![Vehicle APIs and data model](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Vehicle%20APIs.JPG?raw=true)

Full REST documentation is available [here](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html) - assuming all services are running on localhost with default ports -.

### Registry/Discovery service: 
This service works as a discovery/registry service. It used to provide a discovery mechanism for any client that needs to communicate to a specific service without needing to know the specific end-point(s) for the target service. Any target service needs to register to this discovery/registry service so that it's accessible by any client.

This discovery service also provides a dashboard with some useful information about registered services and their statuses as shown below.

[Netflix Eureka](https://github.com/Netflix/eureka) is used as registry/discovery server for this solution.

![Discovery Service Dashboard](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Discovery.JPG?raw=true)

The above dashboard is available [here](http://localhost:8761/) - assuming that all services are running on localhost with default ports -.

### Configuration service:
To comply with microservices architecture best practices, all services configurations should be centralized and dettached from the services source code. I.e. whenever a service boots up, it should fetch its all related configuration from an external provider. This is the use of this Configuration service.
To maximize the benefit of this externalized configurations, all services configs for this solution are uplaoded and tracked by github [here](https://github.com/youssefhamza80/ConnectedVehicles_ConfigRepo). 
When this configuration service starts up, it loads all client services (such as Vehicle and Customer services)  from the above github repository.
When any client service starts up, it will fetch the configurations from this configuration service. For example, Customer service fetches its configuration during startup from [here](http://localhost:9000/connected-vehicles-customer/default) - assuming that all services are running on local host with default ports.   
 
### API Gateway: 
This service works as a proxy, edge service and routing application for the underlying services (such as Customer and Vehicle services). Instead of exposing diffirent ports for each service, outside world can communicate only with this API gateway without worrying about implementation details of the actual running services. This gives us flexibility when it comes to developing new services or changing the underlying codebase structure.
This API gateway communicates with discovery/registry service to get end-points for registered client services. It also provides unified end-points to be exposed to the outside-world consumers.

In this platforms, outside-world consumers are:
- [ ] **Vehicle Monitoring Web App**: This application communicates with the API gateway end-pojnts to extract vehicles and customer related information to be displayed to the user.
- [ ] **Vehicles**:  Vehicles communicates with the API gateway to provide heartbeats - i.e. pings - to the system and thus vehicles statuses are CONNECTED.

[Netflix Zuul](https://github.com/Netflix/zuul) is used as the API gateway for this solution.

### Vehicle Monitoring Web App:
This web application is the front-end part of the solution. [React JS](https://reactjs.org/) framework is used for UI development.

![Screenshot](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/MonitoringApp.JPG?raw=true) 
 
 You can run the application [here](http://localhost:3000/) assuming that all services are running on local host with default ports.
 
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
	 
 - **REST APIs documentation**: [Swagger-UI](https://swagger.io/tools/swagger-ui/) is used to provide full REST APIs documentation with the ability to execute varioud HTTP methods for each API.

 - **Code quality analyzers**: 
     - [ ] [SonarLint ](https://www.sonarlint.org/) is used to check the code quality locally. That is to make sure that local source code does not have any quality issues prior to committing/pushing to the SCM repository. 
     - [ ] [SonarCloud ](https://sonarcloud.io/) is used to check source code quality after it's pushed to remote SCM (Github).
     
SonarCloud code quality analysis reports and statistics can be tracked using the below links:[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=alert_status)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=coverage)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=ncloc)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=security_rating)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=youssefhamza80_ConnectedVehicles&metric=code_smells)](https://sonarcloud.io/dashboard?id=youssefhamza80_ConnectedVehicles)


- **Containerization**: [Docker ](https://www.docker.com/) is used to build and push services images to [Docker hub repository](https://hub.docker.com/) with each code push to github. This facilitates easy deployment on any machine that have Docker installed.

  All services are dockarized with each build and images are pushed automatically to [Docker Hub](https://hub.docker.com/).
  Docker images can be pulled to local machine using the following commands:
  **Vehicle Service**: 
  `docker pull yousifkamal/cv_vehicle`
  **Customer Service**: 
  `docker pull yousifkamal/cv_customer`
  **Service Discovery and Registry service**: 
  `docker pull yousifkamal/cv_discovery`
  **API Gateway Service**: 
  `docker pull yousifkamal/cv_apigateway`
  **Configuration Server**: 
  `docker pull yousifkamal/cv_configserver`
  **Front-end Vehicle Monitoring Application**: 
  `docker pull yousifkamal/cv_monitoring_app`


 - **CI/CD**: [Travis CI](https://travis-ci.com/) is used as a continuous integration plaftorm. It performs the following tasks whenever new code is pushed to [github repository](https://github.com/youssefhamza80/ConnectedVehicles):
	  - [ ] Builds the source code using Maven.
	  - [ ] Runs all defined unit/integration test cases.
	  - [ ] Generates code coverage reports - using [Jacoco Maven plugin](https://www.eclemma.org/jacoco/trunk/doc/maven.html)-.
	  - [ ] Invokes SonarCloud code quality checks and publish results to SonarCloud.
	  - [ ] Buildes Docker images for all services and push them to docker hub remote repository.   

    Lat build status can be tracked here: [![Build Status](https://travis-ci.com/youssefhamza80/ConnectedVehicles.svg?branch=main)](https://travis-ci.com/youssefhamza80/ConnectedVehicles)
   
## How To Build

To build and run this solution locally using command line/shell terminal:
- **Pre-requisites**: [git](https://git-scm.com/downloads), [Maven](https://maven.apache.org/download.cgi), and [yarn](https://classic.yarnpkg.com/en/docs/install) tools are installed.

- **Steps to build**:
  - Copy repository to local machine: `git clone https://github.com/youssefhamza80/ConnectedVehicles.git`
  - Go to repository root directory: `cd ConnectedVehicles`
  - Run Maven to build all backend services: `mvn clean install`
 
## How To Run
 
 - You can run the solution simply by pulling docker images as follow:
    - **Copy repository to local machine**: `git clone https://github.com/youssefhamza80/ConnectedVehicles.git`
    - **Go to repository root directory**: `cd ConnectedVehicles`
    - **Run Docker Compose to bring all services up**: `docker-compose -f .\docker-compose.yml up -d`
      ![](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/Docker%20Up.jpg?raw=true)

      After all docker containers are up as per the above screenshot, you can run the monitoring application [here](http://localhost:3000/).

2. You can run the project by starting up spring boot services using command line/shell with the following sequence:
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

To simulate vehicle status sending, you can place a ping request using vehicle Id using this [link](http://localhost:7000/connected_vehicles/vehicle/swagger-ui/index.html#/Vehicle%20Controller/pingUsingPUT). Or you may use [Postman](https://www.postman.com/downloads/) to place a PUT request on this url: http://localhost:7000/connected_vehicles/vehicle/ping/{vehicleId}

***Note***: The monitoring application refreshes automatically every 5 seconds. So, after ***ping*** request is received, vehicle status will become ***"CONNECTED"*** until 1 minute is elapsed, afterwards status will become ***"NOT CONNECTED"*** until another ***ping*** request is received.


## Deployment On Public Clouds
It is highly recommended to deploy all services on public cloud to leverage to leverage the great scalability and availability provided by global privders such as Amazon AWS, Microsoft Azure, and Google Cloud Platform.

As a proof of concept, I have deployed the configuration server as a dockarizedimage on AWS Elastic Beanstalk. With just simple parameters and configurations, application has been deployed successfully on the AWS cloud with many useful and powerful features, such as:
- Load balancer "ELB" has been allocated automatically.
- Auto scaling group has been defined automatically.
- Security group has been defined automatically.
- EC2 instance(s) are allocated automatically.

![enter image description here](https://github.com/youssefhamza80/ConnectedVehicles/blob/main/Diagrams/AWS%20Elastic%20Beanstalk.png?raw=true)

I have shutdown the application on AWS so that I won't be charged. However, I can turn it on and send you the URL if needed.

Of course, there are many other usedul services on AWS and other public clouds that can fulfill this solution needs. For example, AWS Dynamo DB could be used instead of Mongo DB.
Also, PING can be deployed as a separate service using AWS lambda serverless architecture. This may help for saving costs as billing will be based on number of calls instead of server UP time.







<br><br><br><br>
>Written with [StackEdit](https://stackedit.io/)