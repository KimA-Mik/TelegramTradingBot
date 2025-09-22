plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "ru.kima.cacheserver.implementation"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":SharesServer:CacheServerApi"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
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

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}