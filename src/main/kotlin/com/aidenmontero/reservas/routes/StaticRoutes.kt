package com.aidenmontero.reservas.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.defaultResource
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing

fun Application.configureStaticRoutes() {
    routing {
        staticResources("/", "static") {
            defaultResource("index.html")
        }
    }
}

