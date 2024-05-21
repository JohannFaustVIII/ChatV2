# ChatV2

Simple chat application (Work In Progress).

## Environment Variables

### Backend

 - `FRONT_URL` - frontend URL to enable CORS, default is `http://localhost:4200`
 - `JWT_PROVIDER_URI` - Keycloak's URL to certificates, default is `http://localhost:8180/realms/ChatV2Realm/protocol/openid-connect/certs`

## Technologies

### Backend
 - Java
 - Spring Boot
 - Spring Webflux - for Server Sent Events
 - Spring AOP - aspects to send events without interfering into the business code

### Frontend
 - Angular

### Other
 - Keycloak - authentication and authorization
 - Docker - containerization
