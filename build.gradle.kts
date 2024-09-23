plugins {
    java
    `maven-publish`

    id("org.springframework.boot") version "3.3.4" apply  false
    id("io.spring.dependency-management") version "1.1.6"
}

group = "org.cranes.team"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter:3.3.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc:3.3.4")

    implementation("org.slf4j:slf4j-api:2.0.13")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
        }
    }
}