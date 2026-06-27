package com.exchangepro.moviles.presentation.feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exchangepro.moviles.data.repository.FirebaseFeedbackRepository
import com.exchangepro.moviles.data.repository.UserFeedback
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.components.StatusPill
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangeNegative
import com.exchangepro.moviles.ui.theme.ExchangePositive
import kotlinx.coroutines.launch

@Composable
fun FeedbackScreen() {
    val repository = remember { FirebaseFeedbackRepository() }
    val scope = rememberCoroutineScope()
    var type by remember { mutableStateOf("RECOMENDACION") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf(emptyList<UserFeedback>()) }
    var message by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    suspend fun reload() {
        feedback = repository.getMine()
    }

    LaunchedEffect(Unit) {
        runCatching { reload() }
            .onFailure { message = it.message ?: "No se pudo cargar tu feedback." }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Feedback", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Envia una recomendacion o reporta un error", color = ExchangeMuted)
        }
        item {
            ExchangeCard {
                Text("Tipo", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "RECOMENDACION",
                        onClick = { type = "RECOMENDACION" },
                        label = { Text("Recomendacion") }
                    )
                    FilterChip(
                        selected = type == "REPORTE_ERROR",
                        onClick = { type = "REPORTE_ERROR" },
                        label = { Text("Reportar error") }
                    )
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(100) },
                    label = { Text("Titulo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(1000) },
                    label = { Text("Descripcion") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
                message?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, color = if (it.contains("enviado", true)) ExchangePositive else ExchangeNegative)
                }
                Spacer(Modifier.height(12.dp))
                PrimaryAction(
                    text = if (saving) "Enviando..." else "Enviar",
                    onClick = {
                        when {
                            title.trim().length < 4 -> message = "Escribe un titulo mas claro."
                            description.trim().length < 10 -> message = "Describe el caso con mas detalle."
                            !saving -> {
                                saving = true
                                scope.launch {
                                    try {
                                        repository.create(type, title, description)
                                        title = ""
                                        description = ""
                                        message = "Feedback enviado correctamente."
                                        reload()
                                    } catch (error: Exception) {
                                        message = error.message ?: "No se pudo enviar el feedback."
                                    } finally {
                                        saving = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item {
            Text("Mis envios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        if (feedback.isEmpty()) {
            item { ExchangeCard { Text("Todavia no enviaste feedback.", color = ExchangeMuted) } }
        } else {
            items(feedback, key = { it.id }) { item -> FeedbackRow(item) }
        }
    }
}

@Composable
private fun FeedbackRow(item: UserFeedback) {
    ExchangeCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold)
                Text(
                    if (item.type == "REPORTE_ERROR") "Reporte de error" else "Recomendacion",
                    color = ExchangeMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            StatusPill(item.status)
        }
        Spacer(Modifier.height(6.dp))
        Text(item.description)
        item.adminResponse?.takeIf { it.isNotBlank() }?.let {
            Spacer(Modifier.height(8.dp))
            Text("Respuesta del administrador", fontWeight = FontWeight.SemiBold)
            Text(it, color = ExchangeMuted)
        }
    }
}
