package com.exchangepro.moviles.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.exchangepro.moviles.data.repository.AdminDashboardData
import com.exchangepro.moviles.data.repository.AdminDisputeRecord
import com.exchangepro.moviles.data.repository.AdminFeedbackRecord
import com.exchangepro.moviles.data.repository.AdminReportRow
import com.exchangepro.moviles.data.repository.FirebaseAdminRepository
import com.exchangepro.moviles.presentation.navigation.Route
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.components.SecondaryAction
import com.exchangepro.moviles.ui.components.StatusPill
import com.exchangepro.moviles.ui.theme.ExchangeAccent
import com.exchangepro.moviles.ui.theme.ExchangeElevated
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangeNegative
import com.exchangepro.moviles.ui.theme.ExchangePositive
import com.exchangepro.moviles.ui.theme.ExchangePrimary
import com.exchangepro.moviles.ui.theme.ExchangeSurface
import com.exchangepro.moviles.ui.theme.ExchangeWarning
import kotlinx.coroutines.launch

private data class AdminStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val repository = remember { FirebaseAdminRepository() }
    var data by remember { mutableStateOf(AdminDashboardData()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching { repository.dashboard() }
            .onSuccess { data = it }
            .onFailure { error = it.message ?: "No se pudo cargar el dashboard." }
        loading = false
    }
    val stats = listOf(
        AdminStat("Usuarios registrados", data.users.toString(), Icons.Default.Groups, ExchangePrimary),
        AdminStat("Ofertas activas", data.activeOffers.toString(), Icons.Default.Sell, ExchangeAccent),
        AdminStat("Transacciones completadas", data.completedTransactions.toString(), Icons.Default.SwapHoriz, ExchangePositive),
        AdminStat("Disputas pendientes", data.pendingDisputes.toString(), Icons.Default.Gavel, ExchangeNegative)
    )

    AdminPage("Dashboard", "Panel de control administrativo") {
        if (loading) item { ExchangeCard { Text("Cargando datos reales...", color = ExchangeMuted) } }
        error?.let { item { ErrorCard(it) } }
        items(stats) { stat -> AdminStatCard(stat) }
        item {
            ExchangeCard {
                Text("Acciones rapidas", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrimaryAction("Revisar disputas", { navController.navigate(Route.AdminDisputes.value) }, Modifier.weight(1f))
                    SecondaryAction("Reportes", { navController.navigate(Route.AdminReports.value) }, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AdminDisputesScreen() {
    val repository = remember { FirebaseAdminRepository() }
    val scope = rememberCoroutineScope()
    var disputes by remember { mutableStateOf(emptyList<AdminDisputeRecord>()) }
    var filter by remember { mutableStateOf("Pendientes") }
    var selected by remember { mutableStateOf<AdminDisputeRecord?>(null) }
    var message by remember { mutableStateOf<String?>(null) }

    suspend fun reload() { disputes = repository.disputes() }
    LaunchedEffect(Unit) {
        runCatching { reload() }.onFailure { message = it.message ?: "No se pudieron cargar las disputas." }
    }
    selected?.let { dispute ->
        ResolveDisputeDialog(
            dispute = dispute,
            onDismiss = { selected = null },
            onResolve = { release, note ->
                scope.launch {
                    runCatching { repository.resolveDispute(dispute.id, release, note); reload() }
                        .onSuccess { message = "Disputa resuelta y fondos actualizados."; selected = null }
                        .onFailure { message = it.message ?: "No se pudo resolver la disputa." }
                }
            }
        )
    }
    val visible = disputes.filter {
        filter == "Todos" || (filter == "Pendientes" && it.status == "PENDIENTE") ||
            (filter == "Resueltas" && it.status == "RESUELTA")
    }
    AdminPage("Gestion de Disputas", "Revisa evidencias y resuelve fondos retenidos") {
        message?.let { item { ExchangeCard { Text(it, color = ExchangeMuted) } } }
        item { AdminFilterRow(listOf("Todos", "Pendientes", "Resueltas"), filter) { filter = it } }
        if (visible.isEmpty()) item { EmptyAdminState(Icons.Default.Gavel, "No hay disputas en este filtro") }
        items(visible, key = { it.id }) { dispute ->
            ExchangeCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        StatusPill(if (dispute.status == "PENDIENTE") "Pendiente" else "Resuelta")
                        Text(dispute.reason, fontWeight = FontWeight.Bold)
                        Text(dispute.transactionCode, color = ExchangeMuted)
                    }
                    if (dispute.status == "PENDIENTE") SecondaryAction("Resolver", { selected = dispute })
                }
                Spacer(Modifier.height(8.dp))
                Text(dispute.description, color = ExchangeMuted)
                Text("Monto retenido: %.2f %s".format(dispute.amount, dispute.currency))
                Text("Evidencias: ${dispute.evidenceCount}", color = ExchangeMuted)
                if (dispute.resolution.isNotBlank()) Text("Fallo: ${dispute.resolution}", color = ExchangePositive)
            }
        }
    }
}

@Composable
fun AdminFeedbackScreen() {
    val repository = remember { FirebaseAdminRepository() }
    val scope = rememberCoroutineScope()
    var feedback by remember { mutableStateOf(emptyList<AdminFeedbackRecord>()) }
    var filter by remember { mutableStateOf("Todos") }
    var selected by remember { mutableStateOf<AdminFeedbackRecord?>(null) }
    var message by remember { mutableStateOf<String?>(null) }
    suspend fun reload() { feedback = repository.feedback() }
    LaunchedEffect(Unit) {
        runCatching { reload() }.onFailure { message = it.message ?: "No se pudo cargar el feedback." }
    }
    selected?.let { item ->
        ResponseDialog(item, { selected = null }) { response ->
            scope.launch {
                runCatching { repository.respondFeedback(item.id, response); reload() }
                    .onSuccess { message = "Respuesta enviada al usuario."; selected = null }
                    .onFailure { message = it.message ?: "No se pudo responder." }
            }
        }
    }
    val visible = feedback.filter { filter == "Todos" || it.type == filter }
    AdminPage("Buzon de Feedback", "Sugerencias y reportes reales de usuarios") {
        message?.let { item { ExchangeCard { Text(it, color = ExchangeMuted) } } }
        item { AdminFilterRow(listOf("Todos", "RECOMENDACION", "REPORTE_ERROR"), filter) { filter = it } }
        if (visible.isEmpty()) item { EmptyAdminState(Icons.Default.Feedback, "No hay feedback en este filtro") }
        items(visible, key = { it.id }) { item ->
            ExchangeCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(item.title, fontWeight = FontWeight.Bold)
                        Text(item.type.replace("_", " "), color = ExchangeMuted)
                    }
                    StatusPill(item.status)
                }
                Spacer(Modifier.height(6.dp))
                Text(item.description)
                if (item.adminResponse.isNotBlank()) Text("Respuesta: ${item.adminResponse}", color = ExchangePositive)
                else {
                    Spacer(Modifier.height(8.dp))
                    SecondaryAction("Responder", { selected = item })
                }
            }
        }
    }
}

@Composable
fun AdminReportsScreen() {
    val repository = remember { FirebaseAdminRepository() }
    val scope = rememberCoroutineScope()
    var type by remember { mutableStateOf("Transacciones") }
    var currency by remember { mutableStateOf("Todas") }
    var status by remember { mutableStateOf("Todos") }
    var rows by remember { mutableStateOf<List<AdminReportRow>?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    AdminPage("Reportes", "Consulta información actual de Firestore") {
        item {
            ExchangeCard {
                Text("Tipo de reporte", fontWeight = FontWeight.Bold)
                AdminFilterRow(listOf("Usuarios", "Ofertas", "Transacciones", "Recargas", "Disputas"), type) { type = it }
                Text("Moneda", fontWeight = FontWeight.Bold)
                AdminFilterRow(listOf("Todas", "PEN", "USD", "EUR"), currency) { currency = it }
                Text("Estado", fontWeight = FontWeight.Bold)
                AdminFilterRow(listOf("Todos", "ACTIVA", "PENDIENTE", "COMPLETADO"), status) { status = it }
                PrimaryAction(
                    "Generar reporte",
                    {
                        scope.launch {
                            runCatching { repository.report(type, currency, status) }
                                .onSuccess { rows = it; error = null }
                                .onFailure { error = it.message ?: "No se pudo generar el reporte." }
                        }
                    },
                    Modifier.fillMaxWidth()
                )
            }
        }
        error?.let { item { ErrorCard(it) } }
        rows?.let { result ->
            item {
                ExchangeCard {
                    Text("Resultados (${result.size})", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    ReportRow("ID", "Usuario", "Monto", "Estado", true)
                    result.forEach { ReportRow(it.id, it.owner, it.amount, it.status) }
                }
            }
        } ?: item { EmptyAdminState(Icons.Default.BarChart, "Selecciona filtros y genera un reporte") }
    }
}

@Composable
fun AdminNotificationsScreen() {
    val repository = remember { FirebaseAdminRepository() }
    var disputes by remember { mutableStateOf(emptyList<AdminDisputeRecord>()) }
    var feedback by remember { mutableStateOf(emptyList<AdminFeedbackRecord>()) }
    var error by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        runCatching {
            disputes = repository.disputes().filter { it.status == "PENDIENTE" }
            feedback = repository.feedback().filter { it.status == "PENDIENTE" }
        }.onFailure { error = it.message ?: "No se pudieron cargar las alertas." }
    }
    AdminPage("Notificaciones", "Alertas administrativas del sistema") {
        error?.let { item { ErrorCard(it) } }
        items(disputes, key = { "d-${it.id}" }) {
            AlertCard("Nueva disputa pendiente", "${it.transactionCode}: ${it.reason}", ExchangeNegative)
        }
        items(feedback, key = { "f-${it.id}" }) {
            AlertCard("Feedback pendiente", it.title, ExchangeWarning)
        }
        if (disputes.isEmpty() && feedback.isEmpty() && error == null) {
            item { EmptyAdminState(Icons.Default.Notifications, "No hay alertas administrativas pendientes") }
        }
    }
}

@Composable
private fun ResolveDisputeDialog(
    dispute: AdminDisputeRecord,
    onDismiss: () -> Unit,
    onResolve: (Boolean, String) -> Unit
) {
    var release by remember { mutableStateOf(true) }
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Resolver ${dispute.transactionCode}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(dispute.reason)
                FilterChip(release, { release = true }, { Text("Liberar a la contraparte") })
                FilterChip(!release, { release = false }, { Text("Devolver al propietario") })
                OutlinedTextField(note, { note = it.take(500) }, label = { Text("Fundamento del fallo") })
            }
        },
        confirmButton = { TextButton({ if (note.isNotBlank()) onResolve(release, note) }) { Text("Resolver") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun ResponseDialog(item: AdminFeedbackRecord, onDismiss: () -> Unit, onSend: (String) -> Unit) {
    var response by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Responder feedback") },
        text = {
            Column {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text(item.description, color = ExchangeMuted)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(response, { response = it.take(500) }, label = { Text("Respuesta") })
            }
        },
        confirmButton = { TextButton({ if (response.isNotBlank()) onSend(response) }) { Text("Enviar") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun AdminPage(
    title: String,
    subtitle: String,
    content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(subtitle, color = ExchangeMuted) }
        content()
        item { Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun AdminStatCard(stat: AdminStat) {
    ExchangeCard {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Column { Text(stat.value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = stat.color); Text(stat.label) }
            IconBadge(stat.icon, stat.color)
        }
    }
}

@Composable
private fun AdminFilterRow(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        options.chunked(2).forEach { optionsRow ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                optionsRow.forEach {
                    FilterChip(selected == it, { onSelected(it) }, { Text(it, maxLines = 1) }, modifier = Modifier.weight(1f))
                }
                if (optionsRow.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun IconBadge(icon: ImageVector, color: Color) {
    Box(Modifier.size(46.dp).clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = .16f)), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = color)
    }
}

@Composable
private fun AlertCard(title: String, body: String, color: Color) {
    ExchangeCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(Icons.Default.Notifications, color); Spacer(Modifier.width(10.dp))
            Column { Text(title, fontWeight = FontWeight.Bold); Text(body, color = ExchangeMuted) }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    ExchangeCard { Text(message, color = ExchangeNegative) }
}

@Composable
private fun EmptyAdminState(icon: ImageVector, text: String) {
    ExchangeCard {
        Column(Modifier.fillMaxWidth().padding(vertical = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, tint = ExchangeMuted, modifier = Modifier.size(44.dp)); Text(text, color = ExchangeMuted)
        }
    }
}

@Composable
private fun ReportRow(a: String, b: String, c: String, d: String, header: Boolean = false) {
    val weight = if (header) FontWeight.Bold else FontWeight.Normal
    Row(
        Modifier.fillMaxWidth().background(if (header) ExchangeElevated else Color.Transparent).padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(a, Modifier.weight(.8f), fontWeight = weight)
        Text(b, Modifier.weight(1.2f), fontWeight = weight, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(c, Modifier.weight(1f), fontWeight = weight)
        Text(d, Modifier.weight(1f), fontWeight = weight, maxLines = 1)
    }
}
