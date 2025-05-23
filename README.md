# ChatV2

Simple chat application. Main focus is put on backend side - split to microservices. Frontend side is kept simple and may not implement all possibilities given by backend.

## Environment Variables

### Backend

| Name               | Description                    | Default value                                                          | For which services                  | 
|--------------------|--------------------------------|------------------------------------------------------------------------|-------------------------------------|
| EUREKA_URL         | Eureka discovery URL           | http://discovery:8091/eureka/                                          | channel-controller, channel-repository, chat-controller, chat-repository, chat-request-filter, gateway, keycloak-repository, shared-config, sse, user-repository, user-sse |
| EUREKA_ROUTE_URL   | Eureka outside access URL      | http://discovery:8091                                                  | gateway                             |
| FRONT_URL          | frontend URL to enable CORS    | http://localhost:4200                                                  | gateway                             |
| JWT_PROVIDER_URI   | Keycloak's URL to certificates | http://localhost:8180/realms/ChatV2Realm/protocol/openid-connect/certs | gateway                             |
| KAFKA_HOSTNAME     | Kafka's hostname               | kafka                                                                  | shared-config                       |
| KAFKA_PORT         | Kafka's port                   | 29092                                                                  | shared-config                       |
| KEYCLOAK_URI       | URI to Keycloak                | http://keycloak:8080                                                   | keycloak-repository                 |
| KEYCLOAK_REALM     | Realm used in Keycloak         | ChatV2Realm                                                            | keycloak-repository                 |
| KEYCLOAK_ID        | client used in Keycloak        | cv2-backend                                                            | keycloak-repository                 |
| KEYCLOAK_SECRET    | client's secret                | GJJuJksZfQUpiAV4X8QprPXmMDx0B0Dq                                       | keycloak-repository                 |
| POSTGRES_URL       | URL to database in JDBC format | jdbc:postgresql://localhost:5432/postgres                              | chat-repository, channel-repository |
| POSTGRES_USER      | username to database           | postgres                                                               | chat-repository, channel-repository |
| POSTGRES_PASSWORD  | password to database           | postgres                                                               | chat-repository, channel-repository |

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
 - Kafka - event broker
 - Keycloak - authentication and authorization
 - Docker - containerization
 - Docker-compose - to run the application
 - PostgreSQL - database for backend
 - Gradle

## How to run?

### 1. Build images

There are two scripts defined to build required images: 
- `chat/build-images.sh` - to build backend images. Requires built boot jars to work, can be achieved by running before `chat/build-boot-jars.sh`
- `chat-front/build-image.sh` - to build frontend image.

### 2. Start with docker-compose

After building all required images, the whole application can be started by using `docker-compose up`.

### Quick way to start the application

Previous steps can be done together by using a command like below:

```shell
cd chat && ./build-boot-jars.sh && ./build-images.sh && cd .. && cd chat-front && ./build-image.sh && cd .. &&  docker compose up -d
```

This will build all required boot-jars, build images and start with using docker-compose.

To stop the whole application, can simply use:

```shell
docker compose stop
```

or to even stop with removing containers:

```shell
docker compose down
```


All containers with volumes can be removed by command below (please, be aware that it will also remove all not used volumes).

```shell
docker compose rm -f && docker volume rm $(docker volume ls -q)
```



## What microservices are defined on backend?

- `gateway` - entrypoint for outside applications
- `discovery` - discovery service for microservices inside
- `shared-config` - keeps configuration shared between microservices (for now, Kafka url)
- `sse` - handles Server-Sent Events about events on the backend, notifies about changes inside channels, messages or errors
- `channel-controller` - handles all commands modifying channels (for now, only adding)
- `channel-repository` - handles saving changes and queries
- `chat-controller` - handles all commands modifying messages (adding/editing/deleting)
- `chat-request-filter` - checks correctness of message's commands, and pass them further or returns error (Kafka Stream)
- `chat-repository` - handles saving changes and queries about messages
- `keycloak-repository` - access point to keycloak to read users by other microservices
- `user-sse` - handles all commands changing user state and SSE checking user activity
- `user-repository` - handles saving changes and queries about user activity