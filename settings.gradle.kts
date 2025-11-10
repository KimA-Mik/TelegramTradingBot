pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "TelegramTradingBot"
include(":CommonBotLibrary")
include(":FuturesBot")
include(":SharesServer:CacheServerImplementation")
include(":SharesServer:CacheServerApi")
include(":DailyTrackingBot")
include(":WatcherBot")