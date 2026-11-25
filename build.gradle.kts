import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version "4.0.10"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"

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

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("reflect"))

    api("org.springframework.boot:spring-boot-starter-mail")
    api("org.springframework.boot:spring-boot-starter-thymeleaf")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.icegreen:greenmail-spring:2.1.8")
    testImplementation("org.awaitility:awaitility:4.2.0")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
