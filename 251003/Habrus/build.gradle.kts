plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.freefair.lombok") version "8.12.2.1"
}

group = "by.bsuir"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

allprojects {
	plugins.apply("java")
	plugins.apply("org.springframework.boot")
	plugins.apply("io.spring.dependency-management")
	plugins.apply("io.freefair.lombok")

	repositories {
		mavenCentral()
	}

	java {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	dependencies {
		implementation("org.springframework.boot:spring-boot-starter-web")
		implementation("org.mapstruct:mapstruct:1.6.3")
		implementation("org.springframework.boot:spring-boot-starter-validation")
		annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.springframework.security:spring-security-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}
}


tasks.withType<Test> {
	useJUnitPlatform()
}
