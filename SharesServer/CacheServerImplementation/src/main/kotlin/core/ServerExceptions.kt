package ru.kima.cacheserver.implementation.core

sealed class ServerExceptions : Exception() {
    class SecurityNotFoundException(val ticker: String) : ServerExceptions()
}