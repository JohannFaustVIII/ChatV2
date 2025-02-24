FROM openjdk:24-ea-21-jdk
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","app.jar"]
COPY build/libs/channel-controller-*.jar app.jar
