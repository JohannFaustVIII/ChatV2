# ChatV2

Simple chat application (Work In Progress).

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
 - JOOQ - communication with a database
 - Liquibase - version control of database

### Frontend
 - Angular

### Other
 - Keycloak - authentication and authorization
 - Docker - containerization
 - PostgreSQL - database for backend