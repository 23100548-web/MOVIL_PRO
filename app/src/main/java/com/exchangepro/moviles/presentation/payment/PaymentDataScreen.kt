package com.exchangepro.moviles.presentation.payment

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.theme.ExchangeAccent
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangeNegative
import com.exchangepro.moviles.ui.theme.ExchangePositive
import com.exchangepro.moviles.ui.theme.ExchangePrimary
import com.exchangepro.moviles.ui.theme.ExchangePrimaryLight

private data class BankOption(val id: Int, val name: String, val accountDigits: Int = 14, val cciDigits: Int = 20)

@Composable
fun PaymentDataScreen() {
    val banks = remember {
        listOf(
            BankOption(1, "BCP"),
            BankOption(2, "Interbank"),
            BankOption(3, "BBVA"),
            BankOption(4, "Scotiabank")
        )
    }
    var yape by remember { mutableStateOf("999888777") }
    var plin by remember { mutableStateOf("") }
    var selectedBank by remember { mutableStateOf<BankOption?>(banks.first()) }
    var accountNumber by remember { mutableStateOf("19112345678012") }
    var cci by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Datos de Pago", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Configura como recibiras los pagos de tus ventas", color = ExchangeMuted)
        }

        item {
            PaymentMethodCard(
                title = "Yape",
                subtitle = "Recibe pagos al instante",
                iconTint = ExchangePrimaryLight,
                icon = {
                    Text("Ya", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            ) {
                PaymentInput(
                    label = "Numero de Yape",
                    value = yape,
                    onValueChange = { yape = digitsOnly(it).take(9) },
                    placeholder = "987654321",
                    isError = submitted && yape.isNotBlank() && yape.length != 9,
                    error = "Debe tener 9 digitos"
                )
            }
        }

        item {
            PaymentMethodCard(
                title = "Plin",
                subtitle = "Transferencias rapidas",
                iconTint = ExchangeAccent,
                icon = {
                    Text("Pl", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                }
            ) {
                PaymentInput(
                    label = "Numero de Plin",
                    value = plin,
                    onValueChange = { plin = digitsOnly(it).take(9) },
                    placeholder = "987654321",
                    isError = submitted && plin.isNotBlank() && plin.length != 9,
                    error = "Debe tener 9 digitos"
                )
            }
        }

        item {
            PaymentMethodCard(
                title = "Transferencia Bancaria",
                subtitle = "Deposito en cuenta bancaria",
                iconTint = ExchangePrimary,
                icon = {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.White)
                }
            ) {
                BankDropdown(
                    selected = selectedBank,
                    banks = banks,
                    showRequired = submitted && accountNumber.isNotBlank() && selectedBank == null,
                    onSelect = { selectedBank = it }
                )
                Spacer(Modifier.height(12.dp))
                PaymentInput(
                    label = "Numero de Cuenta",
                    value = accountNumber,
                    onValueChange = { accountNumber = digitsOnly(it).take(selectedBank?.accountDigits ?: 14) },
                    placeholder = "00000000000000",
                    isError = submitted && accountNumber.isNotBlank() && accountNumber.length != (selectedBank?.accountDigits ?: 14),
                    error = "Debe tener ${selectedBank?.accountDigits ?: 14} digitos"
                )
                Spacer(Modifier.height(12.dp))
                PaymentInput(
                    label = "CCI",
                    value = cci,
                    onValueChange = { cci = digitsOnly(it).take(selectedBank?.cciDigits ?: 20) },
                    placeholder = "00000000000000000000",
                    isError = submitted && cci.isNotBlank() && cci.length != (selectedBank?.cciDigits ?: 20),
                    error = "Debe tener ${selectedBank?.cciDigits ?: 20} digitos"
                )
            }
        }

        message?.let {
            item {
                Text(
                    it,
                    color = if (it.contains("guardados", ignoreCase = true)) ExchangePositive else ExchangeNegative,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = {
                        yape = ""
                        plin = ""
                        selectedBank = null
                        accountNumber = ""
                        cci = ""
                        submitted = false
                        message = null
                    }
                ) {
                    Text("Cancelar")
                }
                Spacer(Modifier.width(10.dp))
                PrimaryAction("Guardar Datos de Pago", {
                    submitted = true
                    message = validatePaymentData(yape, plin, selectedBank, accountNumber, cci)
                        ?: "Datos de pago guardados exitosamente."
                })
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    iconTint: Color,
    icon: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    ExchangeCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, color = ExchangeMuted, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun PaymentInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    error: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError
    )
    if (isError) {
        Text(error, color = ExchangeNegative, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
    }
}

@Composable
private fun BankDropdown(
    selected: BankOption?,
    banks: List<BankOption>,
    showRequired: Boolean,
    onSelect: (BankOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected?.name.orEmpty(),
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            label = { Text("Banco") },
            placeholder = { Text("Seleccionar") },
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
            banks.forEach { bank ->
                DropdownMenuItem(
                    text = { Text(bank.name) },
                    onClick = {
                        onSelect(bank)
                        expanded = false
                    }
                )
            }
        }
    }
    if (showRequired) {
        Text("Selecciona un banco", color = ExchangeNegative, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
    }
}

private fun validatePaymentData(
    yape: String,
    plin: String,
    bank: BankOption?,
    account: String,
    cci: String
): String? {
    val hasYape = yape.isNotBlank()
    val hasPlin = plin.isNotBlank()
    val hasBank = bank != null && account.isNotBlank()

    if (!hasYape && !hasPlin && !hasBank) {
        return "Debes proporcionar al menos Yape, Plin o datos bancarios."
    }
    if (hasYape && yape.length != 9) return "Yape debe tener 9 digitos."
    if (hasPlin && plin.length != 9) return "Plin debe tener 9 digitos."
    if (account.isNotBlank() && bank == null) return "Selecciona un banco."
    if (hasBank && account.length != bank.accountDigits) return "La cuenta de ${bank.name} debe tener ${bank.accountDigits} digitos."
    if (cci.isNotBlank() && cci.length != (bank?.cciDigits ?: 20)) return "El CCI debe tener ${bank?.cciDigits ?: 20} digitos."

    return null
}

private fun digitsOnly(value: String): String = value.filter { it.isDigit() }
