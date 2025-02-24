plugins {
    id("java")
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"

}

group = "org.faust"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jooq:jooq:3.19.14")
    implementation("org.springframework:spring-context:6.1.14")
}

tasks.test {
    useJUnitPlatform()
}