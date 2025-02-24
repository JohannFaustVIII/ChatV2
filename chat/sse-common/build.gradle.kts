plugins {
    id("java")
}

group = "org.faust"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-streams:3.9.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.1")
}

tasks.test {
    useJUnitPlatform()
}