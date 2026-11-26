package com.aidenmontero.reservas

import com.aidenmontero.reservas.config.configureHTTP
import com.aidenmontero.reservas.config.configureMonitoring
import com.aidenmontero.reservas.config.configureSerialization
import com.aidenmontero.reservas.persistence.RepositoryFactory
import com.aidenmontero.reservas.routes.configureApiRoutes
import com.aidenmontero.reservas.routes.configureHealthRoutes
import com.aidenmontero.reservas.routes.configureStaticRoutes
import com.aidenmontero.reservas.service.LaboratoryService
import com.aidenmontero.reservas.service.ReservationService
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureHealthRoutes()
    configureStaticRoutes()
    val repositories = RepositoryFactory.create()
    val laboratoryService = LaboratoryService(repositories.laboratories)
    val reservationService = ReservationService(repositories.reservations, repositories.laboratories)
    routing { configureApiRoutes(laboratoryService, reservationService) }
}

