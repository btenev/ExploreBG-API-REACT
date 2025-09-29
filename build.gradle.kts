plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
}

group = "bg.exploreBG"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.apache.commons:commons-pool2")
	// https://mvnrepository.com/artifact/com.auth0/java-jwt
	implementation("com.auth0:java-jwt:4.4.0")
	// https://mvnrepository.com/artifact/com.cloudinary/cloudinary-http44
	implementation("com.cloudinary:cloudinary-http44:1.38.0")
	// https://mvnrepository.com/artifact/software.amazon.awssdk/s3
	implementation("software.amazon.awssdk:s3:2.27.7")
	// https://mvnrepository.com/artifact/org.apache.tika/tika-core
	implementation("org.apache.tika:tika-core:2.9.2")
	// https://mvnrepository.com/artifact/org.apache.tika/tika-parsers-standard-package
	implementation("org.apache.tika:tika-parsers-standard-package:2.9.2")

    implementation("org.hibernate.orm:hibernate-spatial")
    implementation("org.locationtech.jts:jts-core:1.19.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	runtimeOnly("com.mysql:mysql-connector-j")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
