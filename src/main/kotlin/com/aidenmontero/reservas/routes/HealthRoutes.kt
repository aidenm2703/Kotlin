package com.aidenmontero.reservas.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String,
    val service: String
)

fun Application.configureHealthRoutes() {
    routing {
        get("/api/health") {
            call.respond(
                HttpStatusCode.OK,
                HealthResponse(status = "ok", service = "reservas-laboratorio")
            )
        }
    }
}

