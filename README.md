# Sun HttpServer WebApp

This repository contains a small web application that uses the Sun 
HttpServer class included in the JRE / JDK.

The App contains a small REST API to manage users (Basic CRUD) and a 
basic authentication / authorization frontend flow using those users.

## Prerequisites

In order to compile, build and run the code you will need at least the 
following tools installed and ready:

* Maven
* JRE / JDK 8
* Docker (Optionally)
* Docker-Compose (Optionally)

Optionally, the maven-docker-plugin is configured using a provided 
Docker File that can be run with a provided Docker-Compose YAML file.

## Execution

The Web Application is an executable JAR file that can be executed 
through command line or using Docker.

### Command Line execution

After cloning the repository, go to the root folder of the project and 
build the project with:

`mvn clean install`

After a successful build, go to the 'target' folder and execute:

`java -jar technical-test-1.0.0-SNAPSHOT.jar`

The server will start to listen upcoming requests to port 8000 by default.

### Docker execution

After cloning the repository, go to the root folder of the project and 
build the project with:

`mvn clean install`

After a successful build, execute the following maven.docker-plugin goal:

`mvn docker:build`

A docker image called 'technical-test' will be created. (The process can
take a while if it's the first time that you use a java:8 image and It's
not already downloaded in your local registry)

In order to run the created image, go to the 'docker' folder in the root
folder of the project and execute:

`docker-compose up`

The port 8000 is exposed by default:
 * Linux -> localhost:8000 
 * Windows/Mac -> <your-default-docker-engine-machine-ip>:8000

Interact with the service / image using the usual compose commands and flags
(logs, -d, etc)

## Structure

The project is divided in several packages.

The most important ones are:
* 'model' package -> Model of the app.
* 'repository' package -> Repositories of the app.
* 'service' package -> Business and other useful services. 
* 'web' package -> Handlers, Views and Authenticators

The layout and name of the classes should be self explanatory.

## REST Api documentation

The REST API behaviour has been designed inspired by the following 
decision diagram:

![HTTP headers status](https://rawgithub.com/for-GET/http-decision-diagram/master/httpdd.png)

### cUrl examples

* GET /api/users
 `curl -v -u Admin:admin -X GET localhost:8000/api/users`
 
* POST /api/users
 `curl -v -u Admin:admin -d name=New -d password=new -d roles=ADMIN -X POST localhost:8000/api/users`
 
* GET /api/users/{id}
 `curl -v -u Admin:admin -X GET localhost:8000/api/users/1`
 
* PUT /api/users/{id}
 `curl -v -u Admin:admin -d name=Test -d password=test -d roles=ADMIN -X PUT localhost:8000/api/users/4`
 
* DELETE /api/users/{id}
 `curl -v -u Admin:admin -X DELETE localhost:8000/api/users/5`

## Tests

The project contains unit and integration test:
* Unit Tests -> Classes with the suffix *Should
* Integrations Tests -> Classes with the suffix *ITShould

HttpURLConnection class is used as the  client for Integration testing.
Tests use Junit, Mockito and Hamcrest.

The method name of each test should give enough information to understand 
what is being tested

## Improvements

* Finish Views related to Roles from PAGE_1 to PAGE_3
* Add more logs
* Add concurrency tests












