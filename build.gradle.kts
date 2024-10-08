plugins {
    kotlin("jvm") version "2.0.0"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.1.0")

    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation("ru.tinkoff.piapi:java-sdk-core:1.5")
    implementation("ru.mail.im:bot-api:1.2.3")

    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    val koinVersion = "3.5.6"
    implementation("io.insert-koin:koin-core:$koinVersion")

    val exposedVersion: String by project
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")

    implementation("org.xerial:sqlite-jdbc:3.46.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    val ta4jVersion: String by project
    implementation("org.ta4j:ta4j-core:$ta4jVersion")
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

// https://stackoverflow.com/a/71092054
tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map { zipTree(it) }
    from(dependencies)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}