group = "by.bsuir"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-cassandra")
}

tasks.test {
    useJUnitPlatform()
}