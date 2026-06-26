package com.exchangepro.moviles.presentation.disputes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exchangepro.moviles.data.repository.MockExchangeRepository
import com.exchangepro.moviles.domain.model.Dispute
import com.exchangepro.moviles.domain.model.Transaction
import com.exchangepro.moviles.domain.model.TransactionStatus
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.components.StatusPill
import com.exchangepro.moviles.ui.theme.ExchangeElevated
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangeNegative
import com.exchangepro.moviles.ui.theme.ExchangePositive
import com.exchangepro.moviles.ui.theme.ExchangePrimary
import com.exchangepro.moviles.ui.theme.ExchangePrimaryLight
import com.exchangepro.moviles.ui.theme.ExchangeWarning

@Composable
fun DisputesScreen() {
    val disputes = remember { mutableStateListOf<Dispute>().apply { addAll(MockExchangeRepository.disputes) } }
    val transactions = remember {
        MockExchangeRepository.transactions.filter {
            it.status != TransactionStatus.CANCELADO &&
                it.status != TransactionStatus.COMPLETADO &&
                it.status != TransactionStatus.EN_DISPUTA
        }
    }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(transactions.firstOrNull()) }
    var reason by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var evidenceCount by remember { mutableStateOf(0) }
    var submitted by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Abrir Disputa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Reporta un problema con una transaccion", color = ExchangeMuted)
        }

        item {
            ExchangeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ExchangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = Color.White)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Formulario de Disputa", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("La transaccion sera congelada hasta que un administrador la revise", color = ExchangeMuted)
                    }
                }

                Spacer(Modifier.height(18.dp))

                TransactionDropdown(
                    selected = selectedTransaction,
                    transactions = transactions,
                    showRequired = submitted && selectedTransaction == null,
                    onSelect = { selectedTransaction = it }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it.take(100) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Motivo") },
                    placeholder = { Text("Ej: No recibi el pago") },
                    leadingIcon = { Icon(Icons.Default.ReportProblem, contentDescription = null) },
                    singleLine = true,
                    isError = submitted && reason.length < 5
                )
                if (submitted && reason.length < 5) {
                    Text("El motivo debe tener al menos 5 caracteres", color = ExchangeNegative, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(1000) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripcion detallada") },
                    placeholder = { Text("Describe que ocurrio con la mayor cantidad de detalles posible...") },
                    minLines = 4,
                    isError = submitted && description.length < 10
                )
                if (submitted && description.length < 10) {
                    Text("La descripcion debe tener al menos 10 caracteres", color = ExchangeNegative, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(12.dp))

                EvidenceMockSection(
                    evidenceCount = evidenceCount,
                    onAddEvidence = { evidenceCount += 1 }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(ExchangeWarning.copy(alpha = 0.10f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = ExchangeWarning)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Al abrir una disputa, la transaccion quedara EN_DISPUTA y un administrador la revisara.",
                        color = ExchangeMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                message?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = if (it.contains("abierta", true)) ExchangePositive else ExchangeNegative)
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            selectedTransaction = transactions.firstOrNull()
                            reason = ""
                            description = ""
                            evidenceCount = 0
                            submitted = false
                            message = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(8.dp))
                    PrimaryAction("Abrir Disputa", {
                        submitted = true
                        if (selectedTransaction == null) {
                            message = "Selecciona una transaccion."
                        } else if (reason.length < 5) {
                            message = "Completa el motivo de la disputa."
                        } else if (description.length < 10) {
                            message = "Agrega una descripcion mas detallada."
                        } else {
                            disputes.add(
                                0,
                                Dispute(
                                    id = "disp_${disputes.size + 1}",
                                    transactionCode = selectedTransaction!!.code,
                                    reason = reason,
                                    status = "PENDIENTE"
                                )
                            )
                            message = "Disputa abierta. Transaccion congelada."
                            reason = ""
                            description = ""
                            evidenceCount = 0
                            submitted = false
                        }
                    })
                }
            }
        }

        item {
            Text("Disputas recientes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        if (disputes.isEmpty()) {
            item {
                ExchangeCard {
                    Text("No hay disputas registradas.", color = ExchangeMuted)
                }
            }
        } else {
            items(disputes, key = { it.id }) { dispute ->
                ExchangeCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(dispute.transactionCode, fontWeight = FontWeight.Bold)
                            Text(dispute.reason, color = ExchangeMuted)
                        }
                        StatusPill(dispute.status)
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionDropdown(
    selected: Transaction?,
    transactions: List<Transaction>,
    showRequired: Boolean,
    onSelect: (Transaction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected?.let { "${it.code} - S/ ${"%.2f".format(it.totalToPay)}" }.orEmpty(),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Transaccion") },
            placeholder = { Text("Seleccionar transaccion") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
            trailingIcon = {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.clickable { expanded = true }
                )
            },
            isError = showRequired
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            transactions.forEach { trx ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(trx.code)
                            Text("${trx.status} - S/ ${"%.2f".format(trx.totalToPay)}", color = ExchangeMuted, style = MaterialTheme.typography.bodySmall)
                        }
                    },
                    onClick = {
                        onSelect(trx)
                        expanded = false
                    }
                )
            }
        }
    }
    if (showRequired) {
        Text("Selecciona una transaccion", color = ExchangeNegative, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun EvidenceMockSection(evidenceCount: Int, onAddEvidence: () -> Unit) {
    ExchangeCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AttachFile, contentDescription = null, tint = ExchangePrimaryLight)
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Evidencias opcionales", fontWeight = FontWeight.SemiBold)
                Text("JPG, PNG o PDF - maximo 10 MB por archivo", color = ExchangeMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(10.dp))
        PrimaryAction("Agregar evidencia demo", onAddEvidence, Modifier.fillMaxWidth())
        if (evidenceCount > 0) {
            Spacer(Modifier.height(10.dp))
            repeat(evidenceCount) { index ->
                Text(
                    "evidencia_${index + 1}.png",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(ExchangeElevated)
                        .padding(8.dp),
                    color = ExchangeMuted
                )
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}
