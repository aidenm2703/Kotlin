package com.aidenmontero.reservas

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import java.time.LocalDate

class ApplicationTest {
    @Test
    fun healthEndpointReturnsOk() = testApplication {
        application {
            module()
        }

        val response = client.get("/api/health")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("\"status\": \"ok\""))
    }

    @Test
    fun overlappingReservationsAreRejected() = testApplication {
        application { module() }
        val json = """{"laboratoryId":1,"responsible":"Prueba","date":"${LocalDate.now().plusDays(2)}","startTime":"10:00","endTime":"11:00","reason":"Clase"}"""
        val first = client.post("/api/reservas") { contentType(ContentType.Application.Json); setBody(json) }
        val second = client.post("/api/reservas") { contentType(ContentType.Application.Json); setBody(json) }
        assertEquals(HttpStatusCode.Created, first.status)
        assertEquals(HttpStatusCode.BadRequest, second.status)
        assertTrue(second.bodyAsText().contains("solapa"))
    }
}

