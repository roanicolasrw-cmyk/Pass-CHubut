package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// =============================================================================
// ROOM ENTITIES
// =============================================================================

@Entity(tableName = "reserves")
data class Reserve(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val location: String,
    val imageUrl: String = "",
    val isOpen: Boolean = true,
    val visitorCount: Int = 0,
    val weatherAlert: String? = null
)

@Entity(tableName = "tickets")
data class Ticket(
    @PrimaryKey val id: String, // E.g., CHUBUT-ANP-98124
    val reserveId: String,
    val reserveName: String,
    val visitorName: String,
    val visitorDoc: String,
    val residency: String, // "Chubut", "Nacional", "Extranjero"
    val category: String,  // "Mayor", "Menor", "Jubilado"
    val pricePaid: Double,
    val purchaseTimestamp: Long,
    val entryDate: String,
    val isValidated: Boolean = false,
    val validatedTimestamp: Long? = null,
    val validatedBy: String? = null,
    val isAccreditation: Boolean = false, // Free access accreditation
    val accreditationType: String = "", // "Habitante", "Personal Autorizado", "Evento"
    val eventName: String? = null
)

@Entity(tableName = "price_configs")
data class PriceConfig(
    @PrimaryKey val id: String, // residency + "_" + category
    val residency: String,       // "Chubut", "Nacional", "Extranjero"
    val category: String,        // "Mayor", "Menor", "Jubilado"
    val price: Double
)

@Entity(tableName = "validation_logs")
data class ValidationLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ticketId: String,
    val reserveName: String,
    val visitorName: String,
    val timestamp: Long,
    val status: String, // "SUCCESS", "DUPLICATE", "INVALID"
    val rangerName: String
)

// =============================================================================
// ROOM DAO
// =============================================================================

@Dao
interface ChubutDao {
    // Reserves
    @Query("SELECT * FROM reserves ORDER BY name ASC")
    fun getAllReserves(): Flow<List<Reserve>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReserves(reserves: List<Reserve>)

    @Query("UPDATE reserves SET visitorCount = visitorCount + 1 WHERE id = :id")
    suspend fun incrementVisitorCount(id: String)

    // Tickets
    @Query("SELECT * FROM tickets ORDER BY purchaseTimestamp DESC")
    fun getAllTickets(): Flow<List<Ticket>>

    @Query("SELECT * FROM tickets WHERE id = :id")
    suspend fun getTicketById(id: String): Ticket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: Ticket)

    @Update
    suspend fun updateTicket(ticket: Ticket)

    // Prices
    @Query("SELECT * FROM price_configs")
    fun getAllPrices(): Flow<List<PriceConfig>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrices(prices: List<PriceConfig>)

    @Update
    suspend fun updatePrice(price: PriceConfig)

    // Validation Logs
    @Query("SELECT * FROM validation_logs ORDER BY timestamp DESC")
    fun getAllValidationLogs(): Flow<List<ValidationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertValidationLog(log: ValidationLog)
}

// =============================================================================
// ROOM DATABASE
// =============================================================================

@Database(
    entities = [Reserve::class, Ticket::class, PriceConfig::class, ValidationLog::class],
    version = 1,
    exportSchema = false
)
abstract class ChubutDatabase : RoomDatabase() {
    abstract fun chubutDao(): ChubutDao

    companion object {
        @Volatile
        private var INSTANCE: ChubutDatabase? = null

        fun getDatabase(context: Context): ChubutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChubutDatabase::class.java,
                    "chubut_pass_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// =============================================================================
// REPOSITORY PATTERN
// =============================================================================

class ChubutRepository(private val dao: ChubutDao) {
    val allReserves: Flow<List<Reserve>> = dao.getAllReserves()
    val allTickets: Flow<List<Ticket>> = dao.getAllTickets()
    val allPrices: Flow<List<PriceConfig>> = dao.getAllPrices()
    val allValidationLogs: Flow<List<ValidationLog>> = dao.getAllValidationLogs()

    suspend fun getTicketById(id: String): Ticket? = dao.getTicketById(id)

    suspend fun createTicket(ticket: Ticket) {
        dao.insertTicket(ticket)
    }

    suspend fun validateTicket(ticketId: String, rangerName: String, reserveId: String): Pair<String, Ticket?> {
        val ticket = dao.getTicketById(ticketId)
        if (ticket == null) {
            val log = ValidationLog(
                ticketId = ticketId,
                reserveName = "Control General",
                visitorName = "Desconocido",
                timestamp = System.currentTimeMillis(),
                status = "INVALID",
                rangerName = rangerName
            )
            dao.insertValidationLog(log)
            return Pair("INVALID", null)
        }

        if (ticket.isValidated) {
            val log = ValidationLog(
                ticketId = ticketId,
                reserveName = ticket.reserveName,
                visitorName = ticket.visitorName,
                timestamp = System.currentTimeMillis(),
                status = "DUPLICATE",
                rangerName = rangerName
            )
            dao.insertValidationLog(log)
            return Pair("DUPLICATE", ticket)
        }

        // Mark as validated
        val updatedTicket = ticket.copy(
            isValidated = true,
            validatedTimestamp = System.currentTimeMillis(),
            validatedBy = rangerName
        )
        dao.updateTicket(updatedTicket)
        dao.incrementVisitorCount(ticket.reserveId)

        // Insert success log
        val log = ValidationLog(
            ticketId = ticketId,
            reserveName = ticket.reserveName,
            visitorName = ticket.visitorName,
            timestamp = System.currentTimeMillis(),
            status = "SUCCESS",
            rangerName = rangerName
        )
        dao.insertValidationLog(log)

        return Pair("SUCCESS", updatedTicket)
    }

    suspend fun updatePrice(price: PriceConfig) {
        dao.updatePrice(price)
    }

    suspend fun seedDatabase() {
        // We seed if reserves or prices are empty
        val defaultReserves = listOf(
            Reserve(
                id = "peninsula_valdes",
                name = "Península Valdés",
                description = "Patrimonio de la Humanidad. Avistaje de Ballena Franca Austral, elefantes y lobos marinos, orcas y aves playeras.",
                location = "Viedma, Chubut (Acceso por Pto. Madryn)",
                isOpen = true,
                visitorCount = 1450,
                weatherAlert = "Vientos fuertes del sector oeste. Precaución en rutas de ripio."
            ),
            Reserve(
                id = "punta_tombo",
                name = "Área Protegida Punta Tombo",
                description = "La mayor pingüinera continental del mundo de Pingüinos de Magallanes. Sendas elevadas para caminar entre nidos.",
                location = "Gaiman, Chubut (Cerca de Rawson/Trelew)",
                isOpen = true,
                visitorCount = 890
            ),
            Reserve(
                id = "bosque_sarmiento",
                name = "Bosque Petrificado Sarmiento",
                description = "Un bosque de araucariáceas de la era mesozoica convertido en piedra, con formaciones geológicas de colores asombrosos.",
                location = "Sarmiento, Chubut",
                isOpen = true,
                visitorCount = 310,
                weatherAlert = "Alerta por altas temperaturas. Se recomienda hidratación constante."
            ),
            Reserve(
                id = "cabo_dos_bahias",
                name = "Cabo Dos Bahías",
                description = "Reserva que protege una importante colonia de pingüinos de Magallanes, lobos marinos e imponentes manadas de guanacos salvajes.",
                location = "Camarones, Chubut",
                isOpen = true,
                visitorCount = 180
            )
        )

        val defaultPrices = listOf(
            // Residentes de Chubut
            PriceConfig("CHUBUT_MAYOR", "Chubut", "Mayor", 10000.0),
            PriceConfig("CHUBUT_MENOR", "Chubut", "Menor", 8000.0), // Menor de 6 y Jubilados
            PriceConfig("CHUBUT_JUBILADO", "Chubut", "Jubilado", 8000.0),

            // Nacionales
            PriceConfig("NACIONAL_MAYOR", "Nacional", "Mayor", 18000.0),
            PriceConfig("NACIONAL_MENOR", "Nacional", "Menor", 1000.0),
            PriceConfig("NACIONAL_JUBILADO", "Nacional", "Jubilado", 10000.0),

            // Extranjeros
            PriceConfig("EXTRANJERO_MAYOR", "Extranjero", "Mayor", 50000.0),
            PriceConfig("EXTRANJERO_MENOR", "Extranjero", "Menor", 35000.0),
            PriceConfig("EXTRANJERO_JUBILADO", "Extranjero", "Jubilado", 35000.0)
        )

        dao.insertReserves(defaultReserves)
        dao.insertPrices(defaultPrices)
    }
}
