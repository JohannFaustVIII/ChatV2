FROM openjdk:24-ea-21-jdk
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod"]
COPY build/libs/chat-repository-*.jar app.jar
