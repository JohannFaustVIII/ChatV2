# ChatV2

TODO: README need to be updated, backend was split to microservices.

Simple chat application. Main focus is put on backend side. Frontend side is kept simple and may not implement all possibilities given by backen.

## Environment Variables

### Backend

 - `FRONT_URL` - frontend URL to enable CORS, default is `http://localhost:4200`
 - `JWT_PROVIDER_URI` - Keycloak's URL to certificates, default is `http://localhost:8180/realms/ChatV2Realm/protocol/openid-connect/certs`
 - `POSTGRES_URL` - URL to database in JDBC format, default is `jdbc:postgresql://localhost:5432/postgres`
 - `POSTGRES_USER` - username to database, default is `postgres`
 - `POSTGRES_PASSWORD` - password to database, default is `postgres`

## Technologies

### Backend
 - Java
 - Spring Boot
 - Spring Webflux - for Server Sent Events
 - Spring AOP - aspects to send events without interfering into the business code
 - Spring OAuth2 Resource Server - to get info about users from Keycloak
 - JOOQ - communication with a database
 - Liquibase - version control of database
 - Caffeine - cache with time to live implemented
 - JUnit 5 - testing (TODO)
 - Mockito - mocking in tests (TODO) 

### Frontend
 - Angular
 - HTML
 - CSS
 - Typescript

### Other
 - Keycloak - authentication and authorization
 - Docker - containerization
 - PostgreSQL - database for backend
 - Gradle
