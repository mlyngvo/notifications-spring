import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"

    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"

    `maven-publish`
    signing
}

group = "dev.mlyngvo"
if (version == "unspecified") {
    version = "0.0.1-SNAPSHOT"
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = "notifications-spring"

            pom {
                name.set("notifications-spring")
                description.set("Various notification implementations for Spring Boot application.")
                url.set("https://github.com/mlyngvo/notifications-spring")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("mlyngvo")
                        name.set("Ly Nguyen")
                        email.set("contact@minhlynguyenvo.com")
                    }
                }

                scm {
                    url.set("https://github.com/mlyngvo/notifications-spring")
                    connection.set("scm:git:git://github.com/mlyngvo/notifications-spring.git")
                    developerConnection.set("scm:git:ssh://git@github.com/mlyngvo/notifications-spring.git")
                }
            }
        }
    }

    repositories {
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

signing {
    val signingKey = findProperty("signingInMemoryKey") as String?
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
    val signingPassword = findProperty("signingInMemoryKeyPassword") as String?
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
    isRequired = !signingKey.isNullOrBlank()
    if (!signingKey.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    }
}

repositories {
    mavenCentral()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xjsr305=strict"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    api("org.springframework.boot:spring-boot-starter-mail")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
     testImplementation("com.icegreen:greenmail-spring:2.1.8")
    testImplementation("org.awaitility:awaitility:4.2.0")

}

kotlin {
    jvmToolchain(17)
}
