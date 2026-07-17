package com.example.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ChubutRepository
import com.example.data.PriceConfig
import com.example.data.Reserve
import com.example.data.Ticket
import com.example.data.ValidationLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

// Sealed class for application navigation routes/tabs
sealed class AppTab {
    object Visitor : AppTab()
    object Ranger : AppTab()
    object Admin : AppTab()
}

// UI State for the validation scan
sealed class ValidationResult {
    object Idle : ValidationResult()
    data class Success(val ticket: Ticket) : ValidationResult()
    data class Duplicate(val ticket: Ticket) : ValidationResult()
    object Invalid : ValidationResult()
}

class ChubutViewModel(private val repository: ChubutRepository) : ViewModel() {

    // Seeding trigger
    init {
        viewModelScope.launch {
            val currentReserves = repository.allReserves.first()
            if (currentReserves.isEmpty()) {
                repository.seedDatabase()
            }
        }
    }

    // Navigation state
    var currentTab by mutableStateOf<AppTab>(AppTab.Visitor)

    // Flow states from database
    val reserves: StateFlow<List<Reserve>> = repository.allReserves
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prices: StateFlow<List<PriceConfig>> = repository.allPrices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tickets: StateFlow<List<Ticket>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val validationLogs: StateFlow<List<ValidationLog>> = repository.allValidationLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // -------------------------------------------------------------------------
    // FORM STATE: BOOKING/PAYMENT
    // -------------------------------------------------------------------------
    var selectedReserveId by mutableStateOf("peninsula_valdes")
    var visitorName by mutableStateOf("")
    var visitorDoc by mutableStateOf("")
    var selectedResidency by mutableStateOf("Chubut") // "Chubut", "Nacional", "Extranjero"
    var selectedCategory by mutableStateOf("Mayor")  // "Mayor", "Menor", "Jubilado"
    var isPaymentSheetOpen by mutableStateOf(false)
    var isBookingSuccess by mutableStateOf(false)
    var createdTicketId by mutableStateOf("")

    // Simulated card fields
    var cardNumber by mutableStateOf("")
    var cardName by mutableStateOf("")
    var cardExpiry by mutableStateOf("")
    var cardCvv by mutableStateOf("")
    var paymentProcessing by mutableStateOf(false)

    // -------------------------------------------------------------------------
    // ACCREDITATION FORM STATE (HABITANTES / PERSONAL / EVENTOS)
    // -------------------------------------------------------------------------
    var isAccreditationSheetOpen by mutableStateOf(false)
    var accName by mutableStateOf("")
    var accDoc by mutableStateOf("")
    var accType by mutableStateOf("Habitante Región") // "Habitante Región", "Personal Autorizado", "Ranger", "Evento Especial"
    var accEventName by mutableStateOf("")
    var isAccreditationSuccess by mutableStateOf(false)

    // -------------------------------------------------------------------------
    // RANGER SCREEN: LIVE SCANNER SIMULATION
    // -------------------------------------------------------------------------
    var selectedTicketIdToScan by mutableStateOf("")
    var manualCodeInput by mutableStateOf("")
    var scanValidationResult by mutableStateOf<ValidationResult>(ValidationResult.Idle)
    var showScanResultDialog by mutableStateOf(false)
    var activeRangerName by mutableStateOf("Guardaparque Sergio")

    // -------------------------------------------------------------------------
    // CALCULATED PRICE BASED ON CURRENT SELECTIONS
    // -------------------------------------------------------------------------
    fun calculatePrice(residency: String, category: String, priceList: List<PriceConfig>): Double {
        // Find matching price config or fallback
        val lookupKey = "${residency.uppercase()}_${category.uppercase()}"
        return priceList.find { it.id == lookupKey }?.price ?: when (residency) {
            "Chubut" -> if (category == "Mayor") 10000.0 else 8000.0
            "Nacional" -> when (category) {
                "Mayor" -> 18000.0
                "Jubilado" -> 10000.0
                else -> 1000.0
            }
            else -> if (category == "Mayor") 50000.0 else 35000.0
        }
    }

    // -------------------------------------------------------------------------
    // ACTIONS: PURCHASE TICKET
    // -------------------------------------------------------------------------
    fun processPayment(onSuccess: () -> Unit) {
        if (visitorName.isBlank() || visitorDoc.isBlank()) return
        paymentProcessing = true
        viewModelScope.launch {
            // Simulate payment delay
            kotlinx.coroutines.delay(1800)
            
            val currentPrices = prices.value
            val price = calculatePrice(selectedResidency, selectedCategory, currentPrices)
            val ticketId = "CHUBUT-ANP-${Random.nextInt(10000, 99999)}"
            
            val activeReserves = reserves.value
            val reserveName = activeReserves.find { it.id == selectedReserveId }?.name ?: "Área Protegida"

            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val ticket = Ticket(
                id = ticketId,
                reserveId = selectedReserveId,
                reserveName = reserveName,
                visitorName = visitorName,
                visitorDoc = visitorDoc,
                residency = selectedResidency,
                category = selectedCategory,
                pricePaid = price,
                purchaseTimestamp = System.currentTimeMillis(),
                entryDate = dateStr,
                isValidated = false,
                isAccreditation = false
            )

            repository.createTicket(ticket)
            
            createdTicketId = ticketId
            paymentProcessing = false
            isPaymentSheetOpen = false
            isBookingSuccess = true
            
            // Clear form
            visitorName = ""
            visitorDoc = ""
            cardNumber = ""
            cardName = ""
            cardExpiry = ""
            cardCvv = ""
            
            onSuccess()
        }
    }

    // -------------------------------------------------------------------------
    // ACTIONS: REQUEST FREE DIGITAL ACCREDITATION
    // -------------------------------------------------------------------------
    fun processAccreditation(onSuccess: () -> Unit) {
        if (accName.isBlank() || accDoc.isBlank()) return
        viewModelScope.launch {
            val ticketId = "ACRED-FREE-${Random.nextInt(10000, 99999)}"
            val activeReserves = reserves.value
            val reserveName = activeReserves.find { it.id == selectedReserveId }?.name ?: "Todas las Áreas"

            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val ticket = Ticket(
                id = ticketId,
                reserveId = selectedReserveId,
                reserveName = reserveName,
                visitorName = accName,
                visitorDoc = accDoc,
                residency = "Acreditación",
                category = accType,
                pricePaid = 0.0,
                purchaseTimestamp = System.currentTimeMillis(),
                entryDate = dateStr,
                isValidated = false,
                isAccreditation = true,
                accreditationType = accType,
                eventName = if (accType == "Evento Especial") accEventName else null
            )

            repository.createTicket(ticket)
            
            createdTicketId = ticketId
            isAccreditationSheetOpen = false
            isAccreditationSuccess = true
            
            // Clear form
            accName = ""
            accDoc = ""
            accEventName = ""
            
            onSuccess()
        }
    }

    // -------------------------------------------------------------------------
    // ACTIONS: VALIDATE QR OR CODE (GUARDAPARQUE)
    // -------------------------------------------------------------------------
    fun validateTicketScan(ticketId: String) {
        if (ticketId.isBlank()) return
        viewModelScope.launch {
            val result = repository.validateTicket(ticketId, activeRangerName, selectedReserveId)
            scanValidationResult = when (result.first) {
                "SUCCESS" -> ValidationResult.Success(result.second!!)
                "DUPLICATE" -> ValidationResult.Duplicate(result.second!!)
                else -> ValidationResult.Invalid
            }
            showScanResultDialog = true
        }
    }

    // -------------------------------------------------------------------------
    // ACTIONS: EDIT PRICE CONFIGS (ADMIN)
    // -------------------------------------------------------------------------
    fun updatePriceConfig(id: String, newPrice: Double) {
        viewModelScope.launch {
            val existing = prices.value.find { it.id == id }
            if (existing != null) {
                repository.updatePrice(existing.copy(price = newPrice))
            } else {
                // If somehow missing, insert it
                val parts = id.split("_")
                val res = parts.getOrNull(0)?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Chubut"
                val cat = parts.getOrNull(1)?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Mayor"
                repository.updatePrice(PriceConfig(id, res, cat, newPrice))
            }
        }
    }

    // -------------------------------------------------------------------------
    // STATS AGGREGATIONS (ADMIN REPORT PORTAL)
    // -------------------------------------------------------------------------
    val statsFlow: StateFlow<ChubutStats> = combine(tickets, reserves) { ticketList, reserveList ->
        val validated = ticketList.filter { it.isValidated }
        val totalRevenue = validated.sumOf { it.pricePaid }
        val totalValidations = validated.size
        
        // Daily revenue calculation
        val todayStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val dailyRevenue = validated.filter { it.entryDate == todayStr }.sumOf { it.pricePaid }
        
        // Percentages by Residency
        val validatedTickets = validated.filter { !it.isAccreditation }
        val residencyGroups = validatedTickets.groupBy { it.residency }
        val totalRegVisitor = validatedTickets.size.toDouble().coerceAtLeast(1.0)
        
        val pctChubut = ((residencyGroups["Chubut"]?.size ?: 0) / totalRegVisitor) * 100.0
        val pctNational = ((residencyGroups["Nacional"]?.size ?: 0) / totalRegVisitor) * 100.0
        val pctForeigner = ((residencyGroups["Extranjero"]?.size ?: 0) / totalRegVisitor) * 100.0

        // Accreditation counts
        val accCount = validated.count { it.isAccreditation }

        // Visitor counts per reserve from Room db
        val reserveVisitorStats = reserveList.map { res ->
            ReserveStat(res.name, res.visitorCount, res.visitorCount * 12000.0) // Mocking estimation of revenue share or calculated directly
        }

        ChubutStats(
            totalRevenue = totalRevenue,
            dailyRevenue = dailyRevenue,
            totalValidations = totalValidations,
            accreditationsValidated = accCount,
            chubutResidentPct = pctChubut,
            nationalPct = pctNational,
            foreignerPct = pctForeigner,
            reserveStats = reserveVisitorStats,
            totalPendingTickets = ticketList.count { !it.isValidated }
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ChubutStats()
    )
}

// Data structures for stats
data class ChubutStats(
    val totalRevenue: Double = 0.0,
    val dailyRevenue: Double = 0.0,
    val totalValidations: Int = 0,
    val accreditationsValidated: Int = 0,
    val chubutResidentPct: Double = 0.0,
    val nationalPct: Double = 0.0,
    val foreignerPct: Double = 0.0,
    val reserveStats: List<ReserveStat> = emptyList(),
    val totalPendingTickets: Int = 0
)

data class ReserveStat(
    val name: String,
    val visitorCount: Int,
    val estimatedRevenue: Double
)

// Factory for ViewModel
class ChubutViewModelFactory(private val repository: ChubutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChubutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChubutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
