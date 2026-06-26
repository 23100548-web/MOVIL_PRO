package com.exchangepro.moviles.presentation.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.exchangepro.moviles.data.repository.MockExchangeRepository
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.theme.ExchangeElevated
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangePositive
import com.exchangepro.moviles.ui.theme.ExchangePrimary
import com.exchangepro.moviles.ui.theme.ExchangePrimaryLight
import com.exchangepro.moviles.ui.theme.ExchangeWarning

@Composable
fun ProfileScreen() {
    val user = MockExchangeRepository.currentUser
    val nameParts = remember { user.fullName.split(" ", limit = 2) }
    var names by remember { mutableStateOf(nameParts.getOrNull(0).orEmpty()) }
    var lastNames by remember { mutableStateOf(nameParts.getOrNull(1).orEmpty()) }
    var phone by remember { mutableStateOf(user.phone.filter { it.isDigit() }.take(9)) }
    var message by remember { mutableStateOf<String?>(null) }
    var photoChanged by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Administra tu informacion personal", color = ExchangeMuted)
        }

        item {
            ExchangeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .background(ExchangePrimary.copy(alpha = 0.20f))
                            .clickable {
                                photoChanged = true
                                message = "Foto preparada en modo demo. Luego se subira a Firebase Storage."
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ExchangePrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.width(18.dp))
                    Column {
                        Text("$names $lastNames".trim(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < user.reputation.toInt()) ExchangeWarning else ExchangeMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(Modifier.width(6.dp))
                            Text("%.1f".format(user.reputation), color = ExchangeWarning, fontWeight = FontWeight.SemiBold)
                        }
                        Text("${user.totalRatings} calificaciones", color = ExchangeMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        item {
            ExchangeCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PersonOutline, contentDescription = null, tint = ExchangePrimaryLight)
                    Spacer(Modifier.width(8.dp))
                    Text("Informacion Personal", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = names,
                    onValueChange = { names = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombres") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = lastNames,
                    onValueChange = { lastNames = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Apellidos") },
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                ReadOnlyProfileField("Correo Electronico", user.email, Icons.Default.Email)
                Spacer(Modifier.height(12.dp))
                ReadOnlyProfileField("Documento de Identidad", user.documentNumber, Icons.Default.Badge)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter(Char::isDigit).take(9) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Telefono") },
                    placeholder = { Text("999888777") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                message?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = if (it.contains("actualizado", true)) ExchangePositive else ExchangeMuted, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(
                        onClick = {
                            names = nameParts.getOrNull(0).orEmpty()
                            lastNames = nameParts.getOrNull(1).orEmpty()
                            phone = user.phone.filter { it.isDigit() }.take(9)
                            photoChanged = false
                            message = null
                        }
                    ) {
                        Text("Cancelar")
                    }
                    Spacer(Modifier.width(10.dp))
                    PrimaryAction("Guardar Cambios", {
                        message = if (names.isBlank() || lastNames.isBlank()) {
                            "Nombres y apellidos son requeridos."
                        } else {
                            "Perfil actualizado exitosamente."
                        }
                    })
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyProfileField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier
            .fillMaxWidth()
            .background(ExchangeElevated.copy(alpha = 0.18f)),
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = ExchangeMuted) },
        readOnly = true,
        singleLine = true
    )
}
