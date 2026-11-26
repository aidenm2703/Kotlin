package com.aidenmontero.reservas.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Laboratory(
    val id: Long,
    val name: String,
    val capacity: Int,
    val location: String,
    val equipment: String,
    val color: String
)

@Serializable
data class LaboratoryRequest(
    val name: String,
    val capacity: Int,
    val location: String,
    val equipment: String,
    val color: String
)

@Serializable
data class Reservation(
    val id: Long,
    val laboratoryId: Long,
    val laboratoryName: String,
    val responsible: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String,
    val status: String
)

@Serializable
data class ReservationRequest(
    val laboratoryId: Long,
    val responsible: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val reason: String
)

@Serializable
data class ApiError(val message: String)

@Serializable
data class HealthResponse(val status: String, val service: String)

@Serializable
data class DashboardResponse(
    val laboratories: Int,
    val reservationsToday: Int,
    val availableToday: Int,
    val occupancyPercent: Int,
    val weeklyReservations: List<Reservation>
)

