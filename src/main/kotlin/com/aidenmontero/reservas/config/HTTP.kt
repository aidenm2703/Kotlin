package com.aidenmontero.reservas.config

import io.ktor.server.application.Application
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.defaultheaders.defaultHeaders

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("X-Frame-Options", "DENY")
    }
}

