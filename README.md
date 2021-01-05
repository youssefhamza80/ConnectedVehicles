# Connected Vehicles Platform

## Introduction

This platform is used for monitoring vehicles statuses (Connected/Not Connected) via one dashboard. It uses modern software architectural principles and tools to provide a reliable well-built functionalities.

## Architecture

## How To Build

## How To Run

## Exposed APIs

### Vehicle APIs

### Customer APIs

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