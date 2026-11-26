package com.aidenmontero.reservas.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

fun Application.configureStaticRoutes() {
    routing {
        staticResources("/", "static", index = "index.html")
    }
}

