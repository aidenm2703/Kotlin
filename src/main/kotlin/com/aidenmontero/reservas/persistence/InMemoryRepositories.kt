package com.aidenmontero.reservas.persistence

import com.aidenmontero.reservas.domain.model.Laboratory
import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.Reservation
import com.aidenmontero.reservas.domain.model.ReservationRequest
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.atomic.AtomicLong

class InMemoryLaboratoryRepository : LaboratoryRepository {
    private val sequence = AtomicLong(3)
    private val items = mutableListOf(
        Laboratory(1, "Ciencias Naturales", 30, "Edificio A · 101", "Microscopios, kits de química", "#6C63FF"),
        Laboratory(2, "Robótica", 24, "Edificio B · 204", "Kits Arduino, impresora 3D", "#00BFA6")
    )

    @Synchronized override fun list() = items.toList()
    @Synchronized override fun find(id: Long) = items.find { it.id == id }
    @Synchronized override fun create(request: LaboratoryRequest): Laboratory {
        val item = Laboratory(sequence.getAndIncrement(), request.name.trim(), request.capacity, request.location.trim(), request.equipment.trim(), request.color)
        items += item
        return item
    }
    @Synchronized override fun update(id: Long, request: LaboratoryRequest): Laboratory? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) return null
        val item = Laboratory(id, request.name.trim(), request.capacity, request.location.trim(), request.equipment.trim(), request.color)
        items[index] = item
        return item
    }
    @Synchronized override fun delete(id: Long): Boolean = items.removeIf { it.id == id }
}

class InMemoryReservationRepository(private val laboratories: LaboratoryRepository) : ReservationRepository {
    private val sequence = AtomicLong(3)
    private val items = mutableListOf(
        Reservation(1, 1, "Ciencias Naturales", "Ana Rodríguez", LocalDate.now().toString(), "09:00", "10:30", "Práctica de química", "CONFIRMED"),
        Reservation(2, 2, "Robótica", "Luis Gómez", LocalDate.now().plusDays(1).toString(), "14:00", "16:00", "Taller de prototipos", "CONFIRMED")
    )

    @Synchronized override fun list(date: String?, laboratoryId: Long?, status: String?, search: String?): List<Reservation> = items.filter {
        (date.isNullOrBlank() || it.date == date) &&
            (laboratoryId == null || it.laboratoryId == laboratoryId) &&
            (status.isNullOrBlank() || it.status == status) &&
            (search.isNullOrBlank() || "${it.responsible} ${it.reason} ${it.laboratoryName}".contains(search, ignoreCase = true))
    }.sortedWith(compareBy<Reservation> { it.date }.thenBy { it.startTime })

    @Synchronized override fun find(id: Long) = items.find { it.id == id }
    @Synchronized override fun create(request: ReservationRequest): Reservation {
        val item = Reservation(sequence.getAndIncrement(), request.laboratoryId, laboratories.find(request.laboratoryId)?.name.orEmpty(), request.responsible.trim(), request.date, request.startTime, request.endTime, request.reason.trim(), "CONFIRMED")
        items += item
        return item
    }
    @Synchronized override fun update(id: Long, request: ReservationRequest): Reservation? {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) return null
        val current = items[index]
        val item = current.copy(laboratoryId = request.laboratoryId, laboratoryName = laboratories.find(request.laboratoryId)?.name.orEmpty(), responsible = request.responsible.trim(), date = request.date, startTime = request.startTime, endTime = request.endTime, reason = request.reason.trim())
        items[index] = item
        return item
    }
    @Synchronized override fun cancel(id: Long): Boolean {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) return false
        items[index] = items[index].copy(status = "CANCELLED")
        return true
    }
    @Synchronized override fun hasOverlap(laboratoryId: Long, date: LocalDate, start: LocalTime, end: LocalTime, ignoreId: Long?): Boolean = items.any {
        it.status == "CONFIRMED" && it.laboratoryId == laboratoryId && it.date == date.toString() && (ignoreId == null || it.id != ignoreId) && LocalTime.parse(it.startTime).isBefore(end) && LocalTime.parse(it.endTime).isAfter(start)
    }
}

