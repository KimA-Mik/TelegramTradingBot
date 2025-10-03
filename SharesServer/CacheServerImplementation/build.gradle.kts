plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "ru.kima.cacheserver.implementation"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.tinkoff.piapi.java.sdk.core)

    implementation(libs.slf4j.simple)

    implementation(project(":SharesServer:CacheServerApi"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(libs.versions.toolchain.get().toInt())
}

// https://stackoverflow.com/a/71092054
tasks.jar {
    dependsOn(":SharesServer:CacheServerApi:jar")
    manifest {
        attributes["Main-Class"] = "ru.kima.cacheserver.implementation.MainKt"
    }
    val dependencies = configurations
        .runtimeClasspath
        .get()
        .map { zipTree(it) }
    from(dependencies)

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}