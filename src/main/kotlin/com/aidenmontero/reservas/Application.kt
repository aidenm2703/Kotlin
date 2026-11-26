package com.aidenmontero.reservas

import com.aidenmontero.reservas.config.configureHTTP
import com.aidenmontero.reservas.config.configureMonitoring
import com.aidenmontero.reservas.config.configureSerialization
import com.aidenmontero.reservas.routes.configureHealthRoutes
import com.aidenmontero.reservas.routes.configureStaticRoutes
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureHealthRoutes()
    configureStaticRoutes()
}

