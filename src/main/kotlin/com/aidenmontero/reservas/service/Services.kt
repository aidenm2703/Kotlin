package com.aidenmontero.reservas.service

import com.aidenmontero.reservas.domain.model.Laboratory
import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.Reservation
import com.aidenmontero.reservas.domain.model.ReservationRequest
import com.aidenmontero.reservas.domain.validation.ValidationException
import com.aidenmontero.reservas.domain.validation.Validator
import com.aidenmontero.reservas.persistence.LaboratoryRepository
import com.aidenmontero.reservas.persistence.ReservationRepository
import java.time.LocalDate
import java.time.LocalTime

class LaboratoryService(private val repository: LaboratoryRepository) {
    fun list() = repository.list()
    fun create(request: LaboratoryRequest): Laboratory { Validator.laboratory(request); return repository.create(request) }
    fun update(id: Long, request: LaboratoryRequest): Laboratory = repository.update(id, request) ?: throw ValidationException("El laboratorio no existe.")
    fun delete(id: Long) { if (!repository.delete(id)) throw ValidationException("El laboratorio no existe.") }
}

class ReservationService(private val repository: ReservationRepository, private val laboratories: LaboratoryRepository) {
    fun list(date: String?, laboratoryId: Long?, status: String?, search: String?) = repository.list(date, laboratoryId, status, search)
    fun create(request: ReservationRequest): Reservation {
        validate(request)
        return repository.create(request)
    }
    fun update(id: Long, request: ReservationRequest): Reservation {
        validate(request, id)
        return repository.update(id, request) ?: throw ValidationException("La reserva no existe.")
    }
    fun cancel(id: Long) { if (!repository.cancel(id)) throw ValidationException("La reserva no existe.") }

    private fun validate(request: ReservationRequest, ignoreId: Long? = null) {
        Validator.reservation(request)
        if (laboratories.find(request.laboratoryId) == null) throw ValidationException("El laboratorio seleccionado no existe.")
        val date = LocalDate.parse(request.date)
        val start = LocalTime.parse(request.startTime)
        val end = LocalTime.parse(request.endTime)
        if (repository.hasOverlap(request.laboratoryId, date, start, end, ignoreId)) throw ValidationException("Ya existe una reserva que se solapa en ese laboratorio y horario.")
    }
}

