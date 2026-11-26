package com.aidenmontero.reservas.persistence

import com.aidenmontero.reservas.domain.model.Laboratory
import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.Reservation
import com.aidenmontero.reservas.domain.model.ReservationRequest
import java.time.LocalDate
import java.time.LocalTime

interface LaboratoryRepository {
    fun list(): List<Laboratory>
    fun find(id: Long): Laboratory?
    fun create(request: LaboratoryRequest): Laboratory
    fun update(id: Long, request: LaboratoryRequest): Laboratory?
    fun delete(id: Long): Boolean
}

interface ReservationRepository {
    fun list(date: String? = null, laboratoryId: Long? = null, status: String? = null, search: String? = null): List<Reservation>
    fun find(id: Long): Reservation?
    fun create(request: ReservationRequest): Reservation
    fun update(id: Long, request: ReservationRequest): Reservation?
    fun cancel(id: Long): Boolean
    fun hasOverlap(laboratoryId: Long, date: LocalDate, start: LocalTime, end: LocalTime, ignoreId: Long? = null): Boolean
}

