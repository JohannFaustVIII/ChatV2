FROM gradle:jdk21 as builder
WORKDIR /usr/app
COPY build.gradle.kts ./
COPY src src
#here is a problem as it requires another module, find how to copy it?
RUN gradle bootJar


FROM openjdk:24-ea-21-jdk
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","app.jar"]
COPY --from=builder /usr/app/build/libs/app-*.jar app.jar
