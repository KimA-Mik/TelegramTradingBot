plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

group = "ru.kima.telegrambot.common"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    api(libs.ta4j.core)
    implementation(libs.kotlinx.datetime)
    implementation(project(":SharesServer:CacheServerApi"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(libs.versions.toolchain.get().toInt())
}