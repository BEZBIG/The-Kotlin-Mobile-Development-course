plugins {
    kotlin("jvm") version "2.0.21"
    id("io.ktor.plugin") version "3.0.0"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.example"
version = "1.0.0"

application {
    mainClass.set("com.example.module6taskspart4.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Ktor server
    implementation("io.ktor:ktor-server-core-jvm:3.0.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.0.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.0")
    implementation("io.ktor:ktor-server-auth-jvm:3.0.0")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.0")
    implementation("io.ktor:ktor-server-call-logging-jvm:3.0.0")
    implementation("io.ktor:ktor-server-status-pages-jvm:3.0.0")
    implementation("io.ktor:ktor-server-cors-jvm:3.0.0")

    // Exposed + PostgreSQL
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:0.55.0")
    implementation("org.postgresql:postgresql:42.7.4")

    // HikariCP — пул соединений
    implementation("com.zaxxer:HikariCP:6.0.0")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // BCrypt для хэширования паролей
    implementation("at.favre.lib:bcrypt:0.10.2")

    // Swagger / OpenAPI документация
    implementation("io.github.smiley4:ktor-swagger-ui:2.10.0")

    // Logback
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
}