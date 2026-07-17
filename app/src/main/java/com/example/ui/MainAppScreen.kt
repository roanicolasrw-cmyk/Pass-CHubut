package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.PriceConfig
import com.example.data.Reserve
import com.example.data.Ticket
import com.example.data.ValidationLog
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: ChubutViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val reserves by viewModel.reserves.collectAsStateWithLifecycle()
    val prices by viewModel.prices.collectAsStateWithLifecycle()
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()
    val validationLogs by viewModel.validationLogs.collectAsStateWithLifecycle()
    val stats by viewModel.statsFlow.collectAsStateWithLifecycle()

    var showExportToast by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Chubut ANP",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Chubut Pass",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Áreas Naturales Protegidas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.validateTicketScan("") // Triggers background update or test validation
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sync,
                            contentDescription = "Sincronizar base de datos centralizada",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.shadow(2.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(8.dp)
            ) {
                NavigationBarItem(
                    selected = viewModel.currentTab is AppTab.Visitor,
                    onClick = { viewModel.currentTab = AppTab.Visitor },
                    icon = { Icon(Icons.Default.Explore, contentDescription = "Portal de Visitante") },
                    label = { Text("Visitante") },
                    modifier = Modifier.testTag("nav_visitor")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab is AppTab.Ranger,
                    onClick = { viewModel.currentTab = AppTab.Ranger },
                    icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "Guardaparques Terreno") },
                    label = { Text("Guardaparque") },
                    modifier = Modifier.testTag("nav_ranger")
                )
                NavigationBarItem(
                    selected = viewModel.currentTab is AppTab.Admin,
                    onClick = { viewModel.currentTab = AppTab.Admin },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Panel de Gestión") },
                    label = { Text("Admin") },
                    modifier = Modifier.testTag("nav_admin")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (viewModel.currentTab) {
                is AppTab.Visitor -> VisitorScreen(viewModel, reserves, prices, tickets)
                is AppTab.Ranger -> RangerScreen(viewModel, tickets, validationLogs)
                is AppTab.Admin -> AdminScreen(
                    viewModel = viewModel,
                    prices = prices,
                    stats = stats,
                    validationLogs = validationLogs,
                    onExportClick = { showExportToast = true }
                )
            }

            if (showExportToast) {
                AlertDialog(
                    onDismissRequest = { showExportToast = false },
                    confirmButton = {
                        Button(onClick = { showExportToast = false }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("Reporte Estadístico Exportado") },
                    text = { Text("Se ha generado el archivo PDF/CSV con las estadísticas de acceso del día de hoy y sincronizado correctamente con la base de datos central de la provincia del Chubut.") },
                    icon = { Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "Success", tint = MaterialTheme.colorScheme.tertiary) }
                )
            }
        }
    }
}

// =============================================================================
// SCREEN: VISITOR PORTAL
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitorScreen(
    viewModel: ChubutViewModel,
    reserves: List<Reserve>,
    prices: List<PriceConfig>,
    tickets: List<Ticket>
) {
    var activeSubTab by remember { mutableStateOf(0) } // 0: Reserves, 1: My Tickets

    Column(modifier = Modifier.fillMaxSize()) {
        // Segmented Tabs
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = activeSubTab == 0,
                onClick = { activeSubTab = 0 },
                text = { Text("Áreas Protegidas", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                modifier = Modifier.testTag("subtab_reserves")
            )
            Tab(
                selected = activeSubTab == 1,
                onClick = { activeSubTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Mis Pases / Tickets", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (tickets.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = tickets.size.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.testTag("subtab_tickets")
            )
        }

        if (activeSubTab == 0) {
            // Reserve browsing and buying
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hero Header Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Local JPG Image loaded as background
                            Image(
                                painter = painterResource(id = R.drawable.img_chubut_reserves_hero),
                                contentDescription = "Chubut Reserves",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            // Dark gradient overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                                            startY = 100f
                                        )
                                    )
                            )

                            // Title & Description Overlay
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Ecoturismo Responsable",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Reserva de Ingresos Online",
                                    color = Color.White,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "Evite demoras en los puntos de control de guardaparques.",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Seleccione un Área Natural Protegida",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(reserves) { reserve ->
                    ReserveCard(
                        reserve = reserve,
                        onBuyClick = {
                            viewModel.selectedReserveId = reserve.id
                            viewModel.isPaymentSheetOpen = true
                        },
                        onAccreditationClick = {
                            viewModel.selectedReserveId = reserve.id
                            viewModel.isAccreditationSheetOpen = true
                        }
                    )
                }
            }
        } else {
            // Display purchased tickets
            if (tickets.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.ConfirmationNumber,
                    title = "Sin tickets emitidos",
                    subtitle = "Compre una entrada o solicite una acreditación libre de residente en la pestaña de Áreas Protegidas."
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Historial de pases digitales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(tickets) { ticket ->
                        DigitalTicketCard(ticket = ticket)
                    }
                }
            }
        }
    }

    // Modal Form: Purchase Entry Ticket
    if (viewModel.isPaymentSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isPaymentSheetOpen = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val reserveName = reserves.find { it.id == viewModel.selectedReserveId }?.name ?: ""
            val calculatedPrice = viewModel.calculatePrice(viewModel.selectedResidency, viewModel.selectedCategory, prices)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Comprar Entrada Digital",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Destino: $reserveName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Input fields
                OutlinedTextField(
                    value = viewModel.visitorName,
                    onValueChange = { viewModel.visitorName = it },
                    label = { Text("Nombre y Apellido del Visitante") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("input_visitor_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.visitorDoc,
                    onValueChange = { viewModel.visitorDoc = it },
                    label = { Text("Documento / DNI / Pasaporte") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("input_visitor_doc"),
                    singleLine = true
                )

                // Residency Category
                Text(
                    text = "Residencia",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Chubut", "Nacional", "Extranjero").forEach { residency ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.selectedResidency = residency }
                        ) {
                            RadioButton(
                                selected = viewModel.selectedResidency == residency,
                                onClick = { viewModel.selectedResidency = residency },
                                modifier = Modifier.testTag("radio_residency_$residency")
                            )
                            Text(text = residency, fontSize = 12.sp)
                        }
                    }
                }

                // Category Selection
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val catList = if (viewModel.selectedResidency == "Extranjero") listOf("Mayor", "Menor") else listOf("Mayor", "Menor", "Jubilado")
                    catList.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.selectedCategory = category }
                        ) {
                            RadioButton(
                                selected = viewModel.selectedCategory == category,
                                onClick = { viewModel.selectedCategory = category },
                                modifier = Modifier.testTag("radio_category_$category")
                            )
                            Text(text = category, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tarifa Aplicada",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Categoría: ${viewModel.selectedResidency} - ${viewModel.selectedCategory}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$${DecimalFormat("#,###").format(calculatedPrice)} ARS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Payment Gateway Simulation Panel
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pasarela de Pago Segura",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Simulación integrada para transacciones con tarjeta.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Interactive Credit Card visual
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Chubut Pago",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = viewModel.cardNumber.ifBlank { "•••• •••• •••• ••••" },
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = viewModel.cardName.ifBlank { "NOMBRE DEL TITULAR" }.uppercase(),
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = viewModel.cardExpiry.ifBlank { "MM/YY" },
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Card Input Fields
                        OutlinedTextField(
                            value = viewModel.cardNumber,
                            onValueChange = { if (it.length <= 16) viewModel.cardNumber = it },
                            label = { Text("Número de Tarjeta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("card_number"),
                            singleLine = true
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = viewModel.cardExpiry,
                                onValueChange = { if (it.length <= 5) viewModel.cardExpiry = it },
                                label = { Text("Exp. (MM/YY)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("card_expiry"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = viewModel.cardCvv,
                                onValueChange = { if (it.length <= 4) viewModel.cardCvv = it },
                                label = { Text("CVV") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("card_cvv"),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = viewModel.cardName,
                            onValueChange = { viewModel.cardName = it },
                            label = { Text("Nombre como figura en la Tarjeta") },
                            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .testTag("card_name"),
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = {
                        viewModel.processPayment {
                            activeSubTab = 1
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("pay_and_generate_button"),
                    enabled = viewModel.visitorName.isNotBlank() && viewModel.visitorDoc.isNotBlank() && viewModel.cardNumber.isNotBlank() && !viewModel.paymentProcessing,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (viewModel.paymentProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Procesando pago con Banco Chubut...")
                    } else {
                        Icon(Icons.Default.Verified, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pagar e Imprimir Ticket Digital")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal Form: Free Digital Accreditation Request
    if (viewModel.isAccreditationSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.isAccreditationSheetOpen = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            val reserveName = reserves.find { it.id == viewModel.selectedReserveId }?.name ?: "Todas"

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pase de Acceso Libre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Acreditación Digital para $reserveName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Input fields
                OutlinedTextField(
                    value = viewModel.accName,
                    onValueChange = { viewModel.accName = it },
                    label = { Text("Nombre y Apellido") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("acc_name"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.accDoc,
                    onValueChange = { viewModel.accDoc = it },
                    label = { Text("DNI / Credencial Acreditadora") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .testTag("acc_doc"),
                    singleLine = true
                )

                // Accreditation Type Selection
                Text(
                    text = "Tipo de Acreditación Autorizada",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 4.dp)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    listOf("Habitante de la Región (Gratuito)", "Personal Autorizado / Científico", "Evento Especial").forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.accType = type }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = viewModel.accType == type,
                                onClick = { viewModel.accType = type },
                                modifier = Modifier.testTag("radio_acc_$type")
                            )
                            Text(text = type, fontSize = 13.sp)
                        }
                    }
                }

                // Event name extra input
                if (viewModel.accType == "Evento Especial") {
                    OutlinedTextField(
                        value = viewModel.accEventName,
                        onValueChange = { viewModel.accEventName = it },
                        label = { Text("Nombre del Evento (ej. Carrera Península)") },
                        leadingIcon = { Icon(Icons.Default.LocalActivity, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .testTag("acc_event_name"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Acceso libre amparado bajo la normativa de acreditación local de Chubut. Sujeto a validación por guardaparque en terreno.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.processAccreditation {
                            activeSubTab = 1
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_acc_button"),
                    enabled = viewModel.accName.isNotBlank() && viewModel.accDoc.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generar Pase Digital QR")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun ReserveCard(
    reserve: Reserve,
    onBuyClick: () -> Unit,
    onAccreditationClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Reserve header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = reserve.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Navigation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = reserve.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Open Status
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (reserve.isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (reserve.isOpen) "Abierto" else "Cerrado",
                            color = if (reserve.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = reserve.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Weather Alert Section
                if (reserve.weatherAlert != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta Clima",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reserve.weatherAlert,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Visitor statistics badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${DecimalFormat("#,###").format(reserve.visitorCount)} visitas",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onAccreditationClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("acc_btn_${reserve.id}")
                        ) {
                            Text("Acceso Libre", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
                        }

                        Button(
                            onClick = onBuyClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("buy_btn_${reserve.id}")
                        ) {
                            Text("Pagar Ingreso", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DigitalTicketCard(ticket: Ticket) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (ticket.isAccreditation) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (ticket.isAccreditation) "ACREDITACIÓN DIGITAL" else "TICKET DE INGRESO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (ticket.isAccreditation) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = ticket.reserveName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Validation badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (ticket.isValidated) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (ticket.isValidated) Icons.Default.CheckCircle else Icons.Default.QrCode,
                                contentDescription = null,
                                tint = if (ticket.isValidated) Color(0xFF2E7D32) else Color(0xFF1976D2),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (ticket.isValidated) "VALIDADO / USADO" else "VÁLIDO / PENDIENTE",
                                color = if (ticket.isValidated) Color(0xFF2E7D32) else Color(0xFF1976D2),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Coupon Cut layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "  CORTAR AQUÍ PARA CONTROL  ",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold
                )
                Divider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info block
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "Código de Entrada:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = ticket.id,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "Visitante:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = ticket.visitorName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "DNI / Documento:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = ticket.visitorDoc,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = "Categoría:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "${ticket.residency} • ${ticket.category}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (ticket.isValidated && ticket.validatedTimestamp != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Control por: ${ticket.validatedBy}\nFecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ticket.validatedTimestamp))}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // High fidelity QR rendering with laser line (unless validated)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QRCodeCanvas(
                        data = ticket.id,
                        size = 110.dp,
                        showLaser = !ticket.isValidated,
                        qrColor = if (ticket.isValidated) Color.Gray else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Presentar en control",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

// =============================================================================
// SCREEN: RANGER (GUARDAPARQUE GROUND PANEL)
// =============================================================================

@Composable
fun RangerScreen(
    viewModel: ChubutViewModel,
    tickets: List<Ticket>,
    validationLogs: List<ValidationLog>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Ranger Profile Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_guardaparque_badge),
                    contentDescription = "Guardaparques Chubut",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.secondary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = viewModel.activeRangerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Guardaparque en Servicio • Terreno",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = "Punto Control: Entrada Principal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Real-time Scanning Simulation Box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Lector QR de Control Integrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Escanee códigos de pases para habilitar accesos offline/online.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // High fidelity Camera Viewfinder Mock
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Moving scan line simulation
                    QRCodeCanvas(
                        data = viewModel.selectedTicketIdToScan.ifBlank { "SCAN_ID_0000" },
                        size = 180.dp,
                        showLaser = true,
                        qrColor = Color.Black
                    )

                    // Viewfinder frame corner overlays
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Top-Left corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .size(24.dp)
                                .border(
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(topStart = 8.dp)
                                )
                        )
                        // Top-Right corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .border(
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(topEnd = 8.dp)
                                )
                        )
                        // Bottom-Left corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .size(24.dp)
                                .border(
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(bottomStart = 8.dp)
                                )
                        )
                        // Bottom-Right corner
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .border(
                                    BorderStroke(3.dp, MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(bottomEnd = 8.dp)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector for testing / scanning simulation
                Text(
                    text = "Simular Escaneo (Seleccione un Ticket de la lista):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                if (tickets.none { !it.isValidated }) {
                    Text(
                        text = "No hay tickets activos pendientes para simular escaneo.",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tickets.filter { !it.isValidated }) { pendingTicket ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                                    .clickable {
                                        viewModel.selectedTicketIdToScan = pendingTicket.id
                                    }
                                    .border(
                                        width = if (viewModel.selectedTicketIdToScan == pendingTicket.id) 2.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = pendingTicket.id,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = pendingTicket.visitorName,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = viewModel.manualCodeInput,
                        onValueChange = { viewModel.manualCodeInput = it },
                        label = { Text("Código Manual") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_code_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    Button(
                        onClick = {
                            val code = viewModel.manualCodeInput.ifBlank { viewModel.selectedTicketIdToScan }
                            viewModel.validateTicketScan(code)
                            viewModel.manualCodeInput = ""
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("trigger_scan_button"),
                        enabled = viewModel.selectedTicketIdToScan.isNotBlank() || viewModel.manualCodeInput.isNotBlank()
                    ) {
                        Text("Escanear")
                    }
                }
            }
        }

        // Validation Logs History
        Text(
            text = "Registro de Escaneos Recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (validationLogs.isEmpty()) {
            Text(
                text = "Sin escaneos registrados aún.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    validationLogs.take(10).forEachIndexed { index, log ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            when (log.status) {
                                                "SUCCESS" -> Color(0xFFE8F5E9)
                                                "DUPLICATE" -> Color(0xFFFFF3E0)
                                                else -> Color(0xFFFFEBEE)
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (log.status) {
                                            "SUCCESS" -> Icons.Default.Verified
                                            "DUPLICATE" -> Icons.Default.Warning
                                            else -> Icons.Default.Close
                                        },
                                        contentDescription = null,
                                        tint = when (log.status) {
                                            "SUCCESS" -> Color(0xFF2E7D32)
                                            "DUPLICATE" -> Color(0xFFEF6C00)
                                            else -> Color(0xFFC62828)
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = log.visitorName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${log.ticketId} • ${log.reserveName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = when (log.status) {
                                        "SUCCESS" -> "PERMITIDO"
                                        "DUPLICATE" -> "DUPLICADO"
                                        else -> "INVÁLIDO"
                                    },
                                    color = when (log.status) {
                                        "SUCCESS" -> Color(0xFF2E7D32)
                                        "DUPLICATE" -> Color(0xFFEF6C00)
                                        else -> Color(0xFFC62828)
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                )
                            }
                        }
                        if (index < validationLogs.size - 1 && index < 9) {
                            Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        }
                    }
                }
            }
        }
    }

    // Dynamic result popups for scan feedback
    if (viewModel.showScanResultDialog) {
        val result = viewModel.scanValidationResult
        AlertDialog(
            onDismissRequest = { viewModel.showScanResultDialog = false },
            confirmButton = {
                Button(onClick = { viewModel.showScanResultDialog = false }) {
                    Text("Cerrar Control")
                }
            },
            icon = {
                Icon(
                    imageVector = when (result) {
                        is ValidationResult.Success -> Icons.Default.Verified
                        is ValidationResult.Duplicate -> Icons.Default.Warning
                        else -> Icons.Default.Close
                    },
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = when (result) {
                        is ValidationResult.Success -> Color(0xFF2E7D32)
                        is ValidationResult.Duplicate -> Color(0xFFEF6C00)
                        else -> Color(0xFFC62828)
                    }
                )
            },
            title = {
                Text(
                    text = when (result) {
                        is ValidationResult.Success -> "¡ACCESO PERMITIDO!"
                        is ValidationResult.Duplicate -> "¡ACCESO DENEGADO!"
                        else -> "¡TICKET INVÁLIDO!"
                    },
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    color = when (result) {
                        is ValidationResult.Success -> Color(0xFF2E7D32)
                        is ValidationResult.Duplicate -> Color(0xFFEF6C00)
                        else -> Color(0xFFC62828)
                    }
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (result) {
                        is ValidationResult.Success -> {
                            Text(
                                text = "El ticket ha sido verificado con éxito en la base de datos central.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Visitante: ${result.ticket.visitorName}", fontWeight = FontWeight.Bold)
                                    Text("Documento: ${result.ticket.visitorDoc}")
                                    Text("Destino: ${result.ticket.reserveName}")
                                    Text("Residencia: ${result.ticket.residency}")
                                    Text("Tipo: ${result.ticket.category}")
                                }
                            }
                        }
                        is ValidationResult.Duplicate -> {
                            Text(
                                text = "¡Alerta de Fraude! Este ticket YA ha sido validado y utilizado.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Código: ${result.ticket.id}", fontWeight = FontWeight.Bold)
                                    Text("Titular: ${result.ticket.visitorName}")
                                    Text("Primer escaneo:")
                                    Text("Por: ${result.ticket.validatedBy}")
                                    Text("Fecha: ${SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(result.ticket.validatedTimestamp ?: 0))}")
                                }
                            }
                        }
                        else -> {
                            Text(
                                text = "El código QR o ID ingresado no coincide con ningún registro de pago o acreditación libre del Chubut Pass.",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        )
    }
}

// =============================================================================
// SCREEN: ADMIN (MANAGEMENT & STATISTICS AUTOMATED REPORTS)
// =============================================================================

@Composable
fun AdminScreen(
    viewModel: ChubutViewModel,
    prices: List<PriceConfig>,
    stats: ChubutStats,
    validationLogs: List<ValidationLog>,
    onExportClick: () -> Unit
) {
    var editingPriceId by remember { mutableStateOf("") }
    var priceInputString by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Statistical Reports Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reportes Estadísticos Automatizados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onExportClick) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = "Exportar reporte",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid of Stats
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                title = "Recaudación Hoy",
                value = "$${DecimalFormat("#,###").format(stats.dailyRevenue)}",
                icon = Icons.Default.LocalOffer,
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                textColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            StatItem(
                title = "Validaciones",
                value = stats.totalValidations.toString(),
                icon = Icons.Default.Verified,
                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                textColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatItem(
                title = "Pases Gratuitos",
                value = stats.accreditationsValidated.toString(),
                icon = Icons.Default.CardMembership,
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                textColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
            StatItem(
                title = "Pendientes Scan",
                value = stats.totalPendingTickets.toString(),
                icon = Icons.Default.LocalActivity,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart: Visitor residency split
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Distribución por Residencia (Visitantes Regular)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Percentage bar representation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    val wChubut = stats.chubutResidentPct.coerceAtLeast(0.0)
                    val wNat = stats.nationalPct.coerceAtLeast(0.0)
                    val wExt = stats.foreignerPct.coerceAtLeast(0.0)
                    val sum = wChubut + wNat + wExt

                    if (sum == 0.0) {
                        // Empty state chart
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray)
                        )
                    } else {
                        if (wChubut > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(wChubut.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                if (wChubut > 15) Text("${wChubut.toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (wNat > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(wNat.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                if (wNat > 15) Text("${wNat.toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        if (wExt > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(wExt.toFloat())
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.tertiary),
                                contentAlignment = Alignment.Center
                            ) {
                                if (wExt > 15) Text("${wExt.toInt()}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Legends
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem("Residentes", MaterialTheme.colorScheme.primary, "${stats.chubutResidentPct.toInt()}%")
                    LegendItem("Nacionales", MaterialTheme.colorScheme.secondary, "${stats.nationalPct.toInt()}%")
                    LegendItem("Extranjeros", MaterialTheme.colorScheme.tertiary, "${stats.foreignerPct.toInt()}%")
                }
            }
        }

        // Price Configuration Panel
        Text(
            text = "Gestión Tarifaria de Ingreso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Modifique los precios por categorías. Se aplicarán de forma inmediata.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column {
                prices.forEachIndexed { index, priceConfig ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text(
                                text = "Residencia: ${priceConfig.residency}",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Categoría: ${priceConfig.category}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Price edit view
                        if (editingPriceId == priceConfig.id) {
                            Row(
                                modifier = Modifier.weight(1.5f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedTextField(
                                    value = priceInputString,
                                    onValueChange = { priceInputString = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier
                                        .width(90.dp)
                                        .testTag("price_input_${priceConfig.id}"),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = {
                                        val newPrice = priceInputString.toDoubleOrNull()
                                        if (newPrice != null) {
                                            viewModel.updatePriceConfig(priceConfig.id, newPrice)
                                        }
                                        editingPriceId = ""
                                    },
                                    modifier = Modifier.testTag("save_price_${priceConfig.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Guardar tarifa",
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.weight(1.5f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "$${DecimalFormat("#,###").format(priceConfig.price)} ARS",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                OutlinedButton(
                                    onClick = {
                                        editingPriceId = priceConfig.id
                                        priceInputString = priceConfig.price.toInt().toString()
                                    },
                                    modifier = Modifier
                                        .height(36.dp)
                                        .testTag("edit_price_btn_${priceConfig.id}")
                                ) {
                                    Text("Editar", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    if (index < prices.size - 1) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =============================================================================
// HELPER REUSABLE COMPOSABLES
// =============================================================================

@Composable
fun StatItem(
    title: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = textColor.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textColor
            )
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color, percentage: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label ($percentage)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun EmptyStateView(icon: ImageVector, title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

// Simple in-memory scroll helper since we didn't add extra imports
@Composable
fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()
