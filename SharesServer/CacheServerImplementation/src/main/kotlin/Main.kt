package ru.kima.cacheserver.implementation

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import ru.kima.cacheserver.implementation.data.TinkoffDataSource
import ru.kima.cacheserver.implementation.routing.sharesRouting

fun main() {
    val tinkoffToken = System.getenv("TINKOFF_TOKEN")
    if (tinkoffToken == null) {
        println("[ОШИБКА] Для работы программы необходимо предоставить токен для Read-only доступа к Тинькофф-инвестициям через переменную среды `TINKOFF_TOKEN`")
        return
    }
    val tinkoffDataSource = TinkoffDataSource(tinkoffToken)
    embeddedServer(Netty, port = 6969) {
        install(ContentNegotiation) {
            json(Json)
        }
        sharesRouting(tinkoffDataSource)
    }.start(wait = true)
}