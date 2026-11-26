package com.aidenmontero.reservas.routes

import com.aidenmontero.reservas.domain.model.ApiError
import com.aidenmontero.reservas.domain.model.DashboardResponse
import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.ReservationRequest
import com.aidenmontero.reservas.domain.validation.ValidationException
import com.aidenmontero.reservas.service.LaboratoryService
import com.aidenmontero.reservas.service.ReservationService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.time.LocalDate

fun Route.configureApiRoutes(
    laboratoryService: LaboratoryService,
    reservationService: ReservationService
) {
    route("/api") {
        get("/laboratorios") { call.respond(laboratoryService.list()) }
        post("/laboratorios") {
            call.handleValidation { call.respond(HttpStatusCode.Created, laboratoryService.create(call.receive<LaboratoryRequest>())) }
        }
        put("/laboratorios/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, ApiError("El id del laboratorio no es válido."))
            call.handleValidation { call.respond(laboratoryService.update(id, call.receive())) }
        }
        delete("/laboratorios/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiError("El id del laboratorio no es válido."))
            call.handleValidation { laboratoryService.delete(id); call.respond(HttpStatusCode.NoContent) }
        }

        get("/reservas") {
            val laboratoryId = call.request.queryParameters["laboratoryId"]?.toLongOrNull()
            call.respond(reservationService.list(
                call.request.queryParameters["date"],
                laboratoryId,
                call.request.queryParameters["status"],
                call.request.queryParameters["search"]
            ))
        }
        post("/reservas") {
            call.handleValidation { call.respond(HttpStatusCode.Created, reservationService.create(call.receive<ReservationRequest>())) }
        }
        put("/reservas/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest, ApiError("El id de la reserva no es válido."))
            call.handleValidation { call.respond(reservationService.update(id, call.receive())) }
        }
        delete("/reservas/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: return@delete call.respond(HttpStatusCode.BadRequest, ApiError("El id de la reserva no es válido."))
            call.handleValidation { reservationService.cancel(id); call.respond(HttpStatusCode.NoContent) }
        }

        get("/dashboard") {
            val today = LocalDate.now().toString()
            val reservations = reservationService.list(null, null, null, null)
            val todayReservations = reservations.filter { it.date == today && it.status == "CONFIRMED" }
            val weekEnd = LocalDate.now().plusDays(6)
            val weekly = reservations.filter {
                val date = runCatching { LocalDate.parse(it.date) }.getOrNull()
                date != null && date >= LocalDate.now() && date <= weekEnd
            }
            val labs = laboratoryService.list()
            val occupancy = if (labs.isEmpty()) 0 else (todayReservations.size * 100 / labs.size).coerceAtMost(100)
            call.respond(DashboardResponse(labs.size, todayReservations.size, (labs.size - todayReservations.size).coerceAtLeast(0), occupancy, weekly))
        }
    }
}

private suspend fun io.ktor.server.application.ApplicationCall.handleValidation(block: suspend () -> Unit) {
    try {
        block()
    } catch (error: ValidationException) {
        respond(HttpStatusCode.BadRequest, ApiError(error.message ?: "Los datos no son válidos."))
    }
}
