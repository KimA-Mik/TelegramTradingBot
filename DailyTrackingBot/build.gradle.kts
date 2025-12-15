plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "ru.kima.dailytrackingbot"
version = "unspecified"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlin.telegram.bot)

    implementation(libs.koin.core)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.java.time)
    implementation(libs.exposed.migration.core)
    implementation(libs.exposed.migration.jdbc)
    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.sqlite.jdbc)
    implementation(libs.ta4j.core)
    implementation(libs.ktor.client.logging)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.clikt)

//    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.8.0")

    implementation("org.jfree:jfreechart:1.5.3")
//    implementation("org.jetbrains.kotlinx:kandy-api:0.8.0")
//    implementation("org.jetbrains.kotlinx:kandy-echarts:0.8.0")

    implementation(project(":SharesServer:CacheServerApi"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(libs.versions.toolchain.get().toInt())
}

application {
    mainClass.set("MainKt")
}
