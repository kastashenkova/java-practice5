plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:6.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.1.0")

    testImplementation("org.junit.platform:junit-platform-suite-engine:6.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.1.0")

    testImplementation("org.junit.platform:junit-platform-console-standalone:6.1.0")
}

tasks.test {
    useJUnitPlatform()
}