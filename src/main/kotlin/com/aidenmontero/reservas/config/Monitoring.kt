package com.aidenmontero.reservas.config

import io.ktor.server.application.Application
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import org.slf4j.event.Level

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            log.error("Error no controlado en la solicitud", cause)
            call.respondText("Ocurrió un error interno.")
        }
    }
}

