plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

group = "ru.kima.cacheserver.api"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
