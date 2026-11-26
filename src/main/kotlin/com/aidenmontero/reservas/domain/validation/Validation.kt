package com.aidenmontero.reservas.domain.validation

import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.ReservationRequest
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

class ValidationException(message: String) : IllegalArgumentException(message)

object Validator {
    fun laboratory(request: LaboratoryRequest) {
        requireText(request.name, "El nombre del laboratorio es obligatorio.")
        if (request.capacity <= 0) throw ValidationException("La capacidad debe ser mayor que cero.")
        requireText(request.location, "La ubicación es obligatoria.")
        requireText(request.color, "El color es obligatorio.")
    }

    fun reservation(request: ReservationRequest) {
        if (request.laboratoryId <= 0) throw ValidationException("Selecciona un laboratorio válido.")
        requireText(request.responsible, "El responsable es obligatorio.")
        requireText(request.date, "La fecha es obligatoria.")
        requireText(request.startTime, "La hora de inicio es obligatoria.")
        requireText(request.endTime, "La hora de fin es obligatoria.")
        requireText(request.reason, "El motivo es obligatorio.")

        val date = try { LocalDate.parse(request.date) } catch (_: DateTimeParseException) {
            throw ValidationException("La fecha debe tener el formato YYYY-MM-DD.")
        }
        if (date.isBefore(LocalDate.now())) throw ValidationException("No se puede reservar en el pasado.")

        val start = parseTime(request.startTime, "La hora de inicio no es válida.")
        val end = parseTime(request.endTime, "La hora de fin no es válida.")
        if (!end.isAfter(start)) throw ValidationException("La hora de fin debe ser posterior a la hora de inicio.")
    }

    private fun parseTime(value: String, message: String): LocalTime = try {
        LocalTime.parse(value)
    } catch (_: DateTimeParseException) {
        throw ValidationException(message)
    }

    private fun requireText(value: String, message: String) {
        if (value.trim().isEmpty()) throw ValidationException(message)
    }
}

