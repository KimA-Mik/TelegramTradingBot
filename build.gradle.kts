plugins {
    kotlin("jvm") version "1.9.23"
    application
}

group = "ru.kima"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.slf4j:slf4j-simple:2.0.12")

    implementation("ru.tinkoff.piapi:java-sdk-core:1.5")

    val koinVersion = "3.5.5"
    implementation("io.insert-koin:koin-core:$koinVersion")

    val exposedVersion: String by project
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0-RC.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}