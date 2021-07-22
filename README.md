# [WIP] FP PetClinic Sample Application

## Implementation of [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) through Typelevel Scala

## Running petclinic locally:

### Steps:

* run postgres

        docker run --rm -p 5432:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=petclinic -d postgres:alpine

* run app

        sbt run