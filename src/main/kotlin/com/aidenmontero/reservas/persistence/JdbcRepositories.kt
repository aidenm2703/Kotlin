package com.aidenmontero.reservas.persistence

import com.aidenmontero.reservas.domain.model.Laboratory
import com.aidenmontero.reservas.domain.model.LaboratoryRequest
import com.aidenmontero.reservas.domain.model.Reservation
import com.aidenmontero.reservas.domain.model.ReservationRequest
import java.net.URI
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.sql.Connection
import java.sql.Date
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Time
import java.time.LocalDate
import java.time.LocalTime
import java.util.Properties

data class RepositoryBundle(
    val laboratories: LaboratoryRepository,
    val reservations: ReservationRepository,
    val persistent: Boolean
)

object RepositoryFactory {
    fun create(): RepositoryBundle {
        val databaseUrl = System.getenv("DATABASE_URL")
        if (databaseUrl.isNullOrBlank()) {
            val laboratories = InMemoryLaboratoryRepository()
            return RepositoryBundle(laboratories, InMemoryReservationRepository(laboratories), persistent = false)
        }
        val database = JdbcDatabase(databaseUrl)
        database.initialize()
        val laboratories = JdbcLaboratoryRepository(database)
        return RepositoryBundle(laboratories, JdbcReservationRepository(database), persistent = true)
    }
}

class JdbcDatabase(databaseUrl: String) {
    private val jdbcUrl: String
    private val properties = Properties()

    init {
        if (databaseUrl.startsWith("jdbc:")) {
            jdbcUrl = databaseUrl
        } else {
            val uri = URI(databaseUrl)
            val userInfo = uri.userInfo?.split(":", limit = 2)
            if (userInfo != null) {
                properties["user"] = decode(userInfo[0])
                if (userInfo.size > 1) properties["password"] = decode(userInfo[1])
            }
            uri.rawQuery.orEmpty().split("&").filter { it.contains("=") }.forEach {
                val parts = it.split("=", limit = 2)
                properties[decode(parts[0])] = decode(parts[1])
            }
            if (!properties.containsKey("sslmode")) properties["sslmode"] = "require"
            val port = if (uri.port > 0) ":${uri.port}" else ""
            jdbcUrl = "jdbc:postgresql://${uri.host}$port${uri.rawPath.orEmpty()}"
        }
    }

    fun connection(): Connection = DriverManager.getConnection(jdbcUrl, properties)

    fun initialize() {
        connection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS laboratories (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(120) NOT NULL,
                        capacity INTEGER NOT NULL CHECK (capacity > 0),
                        location VARCHAR(160) NOT NULL,
                        equipment TEXT NOT NULL DEFAULT '',
                        color VARCHAR(20) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                """.trimIndent())
                statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS reservations (
                        id BIGSERIAL PRIMARY KEY,
                        laboratory_id BIGINT NOT NULL REFERENCES laboratories(id),
                        responsible VARCHAR(120) NOT NULL,
                        reservation_date DATE NOT NULL,
                        start_time TIME NOT NULL,
                        end_time TIME NOT NULL,
                        reason TEXT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT reservation_time_order CHECK (end_time > start_time)
                    )
                """.trimIndent())
                statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_reservations_date ON reservations(reservation_date)")
                statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_reservations_lab_date ON reservations(laboratory_id, reservation_date)")
            }
            seed(connection)
        }
    }

    private fun seed(connection: Connection) {
        connection.prepareStatement("SELECT COUNT(*) FROM laboratories").use { statement ->
            statement.executeQuery().use { result ->
                result.next()
                if (result.getInt(1) > 0) return
            }
        }
        connection.prepareStatement("INSERT INTO laboratories(name, capacity, location, equipment, color) VALUES (?, ?, ?, ?, ?)").use { statement ->
            listOf(
                arrayOf("Ciencias Naturales", 30, "Edificio A · 101", "Microscopios, kits de química", "#6C63FF"),
                arrayOf("Robótica", 24, "Edificio B · 204", "Kits Arduino, impresora 3D", "#00BFA6")
            ).forEach { values ->
                statement.setString(1, values[0] as String)
                statement.setInt(2, values[1] as Int)
                statement.setString(3, values[2] as String)
                statement.setString(4, values[3] as String)
                statement.setString(5, values[4] as String)
                statement.addBatch()
            }
            statement.executeBatch()
        }
    }

    private fun decode(value: String) = URLDecoder.decode(value, StandardCharsets.UTF_8)
}

class JdbcLaboratoryRepository(private val database: JdbcDatabase) : LaboratoryRepository {
    override fun list(): List<Laboratory> = database.connection().use { connection ->
        connection.prepareStatement("SELECT id, name, capacity, location, equipment, color FROM laboratories ORDER BY name").use { statement ->
            statement.executeQuery().use { result -> result.map { laboratory(it) } }
        }
    }

    override fun find(id: Long): Laboratory? = database.connection().use { connection ->
        connection.prepareStatement("SELECT id, name, capacity, location, equipment, color FROM laboratories WHERE id = ?").use { statement ->
            statement.setLong(1, id)
            statement.executeQuery().use { result -> if (result.next()) laboratory(result) else null }
        }
    }

    override fun create(request: LaboratoryRequest): Laboratory = database.connection().use { connection ->
        connection.prepareStatement("INSERT INTO laboratories(name, capacity, location, equipment, color) VALUES (?, ?, ?, ?, ?)", arrayOf("id")).use { statement ->
            fill(statement, request)
            statement.executeUpdate()
            statement.generatedKeys.use { keys -> keys.next(); find(keys.getLong(1))!! }
        }
    }

    override fun update(id: Long, request: LaboratoryRequest): Laboratory? = database.connection().use { connection ->
        connection.prepareStatement("UPDATE laboratories SET name=?, capacity=?, location=?, equipment=?, color=? WHERE id=?").use { statement ->
            fill(statement, request); statement.setLong(6, id)
            if (statement.executeUpdate() == 0) null else find(id)
        }
    }

    override fun delete(id: Long): Boolean = database.connection().use { connection ->
        connection.prepareStatement("DELETE FROM laboratories WHERE id=?").use { statement -> statement.setLong(1, id); statement.executeUpdate() > 0 }
    }

    private fun fill(statement: java.sql.PreparedStatement, request: LaboratoryRequest) {
        statement.setString(1, request.name.trim()); statement.setInt(2, request.capacity); statement.setString(3, request.location.trim()); statement.setString(4, request.equipment.trim()); statement.setString(5, request.color)
    }

    private fun laboratory(result: ResultSet) = Laboratory(result.getLong("id"), result.getString("name"), result.getInt("capacity"), result.getString("location"), result.getString("equipment"), result.getString("color"))
}

class JdbcReservationRepository(private val database: JdbcDatabase) : ReservationRepository {
    override fun list(date: String?, laboratoryId: Long?, status: String?, search: String?): List<Reservation> = database.connection().use { connection ->
        val conditions = mutableListOf("1=1")
        val values = mutableListOf<Any>()
        if (!date.isNullOrBlank()) { conditions += "r.reservation_date = ?"; values += Date.valueOf(date) }
        if (laboratoryId != null) { conditions += "r.laboratory_id = ?"; values += laboratoryId }
        if (!status.isNullOrBlank()) { conditions += "r.status = ?"; values += status }
        if (!search.isNullOrBlank()) { conditions += "(LOWER(r.responsible) LIKE LOWER(?) OR LOWER(r.reason) LIKE LOWER(?) OR LOWER(l.name) LIKE LOWER(?))"; val term = "%$search%"; values += term; values += term; values += term }
        val sql = "SELECT r.id, r.laboratory_id, l.name AS laboratory_name, r.responsible, r.reservation_date, r.start_time, r.end_time, r.reason, r.status FROM reservations r JOIN laboratories l ON l.id=r.laboratory_id WHERE ${conditions.joinToString(" AND ")} ORDER BY r.reservation_date, r.start_time"
        connection.prepareStatement(sql).use { statement ->
            values.forEachIndexed { index, value -> statement.setObject(index + 1, value) }
            statement.executeQuery().use { result -> result.map { reservation(it) } }
        }
    }

    override fun find(id: Long): Reservation? = list().find { it.id == id }

    override fun create(request: ReservationRequest): Reservation = database.connection().use { connection ->
        connection.prepareStatement("INSERT INTO reservations(laboratory_id, responsible, reservation_date, start_time, end_time, reason) VALUES (?, ?, ?, ?, ?, ?)", arrayOf("id")).use { statement ->
            fill(statement, request); statement.executeUpdate(); statement.generatedKeys.use { keys -> keys.next(); find(keys.getLong(1))!! }
        }
    }

    override fun update(id: Long, request: ReservationRequest): Reservation? = database.connection().use { connection ->
        connection.prepareStatement("UPDATE reservations SET laboratory_id=?, responsible=?, reservation_date=?, start_time=?, end_time=?, reason=? WHERE id=?").use { statement ->
            fill(statement, request); statement.setLong(7, id); if (statement.executeUpdate() == 0) null else find(id)
        }
    }

    override fun cancel(id: Long): Boolean = database.connection().use { connection ->
        connection.prepareStatement("UPDATE reservations SET status='CANCELLED' WHERE id=? AND status <> 'CANCELLED'").use { statement -> statement.setLong(1, id); statement.executeUpdate() > 0 }
    }

    override fun hasOverlap(laboratoryId: Long, date: LocalDate, start: LocalTime, end: LocalTime, ignoreId: Long?): Boolean = database.connection().use { connection ->
        val sql = "SELECT 1 FROM reservations WHERE laboratory_id=? AND reservation_date=? AND status='CONFIRMED' AND start_time < ? AND end_time > ? AND (? IS NULL OR id <> ?) LIMIT 1"
        connection.prepareStatement(sql).use { statement ->
            statement.setLong(1, laboratoryId); statement.setDate(2, Date.valueOf(date)); statement.setTime(3, Time.valueOf(end)); statement.setTime(4, Time.valueOf(start)); statement.setObject(5, ignoreId); statement.setObject(6, ignoreId)
            statement.executeQuery().use { it.next() }
        }
    }

    private fun fill(statement: java.sql.PreparedStatement, request: ReservationRequest) {
        statement.setLong(1, request.laboratoryId); statement.setString(2, request.responsible.trim()); statement.setDate(3, Date.valueOf(request.date)); statement.setTime(4, Time.valueOf(request.startTime)); statement.setTime(5, Time.valueOf(request.endTime)); statement.setString(6, request.reason.trim())
    }

    private fun reservation(result: ResultSet) = Reservation(result.getLong("id"), result.getLong("laboratory_id"), result.getString("laboratory_name"), result.getString("responsible"), result.getDate("reservation_date").toLocalDate().toString(), result.getTime("start_time").toLocalTime().toString().substring(0, 5), result.getTime("end_time").toLocalTime().toString().substring(0, 5), result.getString("reason"), result.getString("status"))
}

private fun <T> ResultSet.map(transform: (ResultSet) -> T): List<T> {
    val values = mutableListOf<T>()
    while (next()) values += transform(this)
    return values
}

