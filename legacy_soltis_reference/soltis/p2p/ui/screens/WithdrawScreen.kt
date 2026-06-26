package com.soltis.p2p.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.P2PButton
import com.soltis.p2p.ui.components.P2PInfoBanner
import com.soltis.p2p.ui.components.P2PTopLogo
import com.soltis.p2p.ui.theme.*

@Composable
fun WithdrawScreen(onBack: () -> Unit) {

    var selectedCurrency by remember { mutableStateOf("PEN") }
    var monto            by remember { mutableStateOf("") }
    var errorMonto       by remember { mutableStateOf("") }

    val currencies = listOf(
        Triple("PEN", "Soles",   "🇵🇪"),
        Triple("USD", "Dólares", "🇺🇸"),
        Triple("EUR", "Euros",   "🇪🇺")
    )

    val symbols = mapOf("PEN" to "S/", "USD" to "$", "EUR" to "€")
    val symbol      = symbols[selectedCurrency] ?: "S/"
    val balance     = when(selectedCurrency) {
        "PEN" -> GlobalWalletState.penBalance
        "USD" -> GlobalWalletState.usdBalance
        "EUR" -> GlobalWalletState.eurBalance
        else -> 0.0
    }
    val montoDouble = monto.toDoubleOrNull() ?: 0.0
    val saldoFinal  = balance - montoDouble

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(52.dp))

        // ── Logo ──────────────────────────────────────────────────────────────
        P2PTopLogo(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Retirar saldo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Envía tus fondos a una cuenta externa",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Moneda ────────────────────────────────────────────────────────────
        Text(
            text = "Moneda",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencies.forEach { (code, name, flag) ->
                val isSelected = selectedCurrency == code
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .background(
                            if (isSelected) Color(0xFFFFFBF2) else Color.White,
                            RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) YellowPrimary else StrokeDefault,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedCurrency = code }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(code, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(name, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Monto ─────────────────────────────────────────────────────────────
        Text(
            text = "Monto a retirar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = monto,
            onValueChange = {
                monto = it
                errorMonto = ""
            },
            placeholder = { Text("Ingresa el monto", color = TextHint) },
            prefix = { Text("$symbol ", fontWeight = FontWeight.Bold, color = TextPrimary) },
            suffix = {
                Text(
                    text = "Disponible: $symbol ${"%.2f".format(balance)}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { monto = "%.2f".format(balance) }
                )
            },
            isError = errorMonto.isNotEmpty(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = YellowPrimary,
                unfocusedBorderColor = StrokeDefault,
                errorBorderColor     = RedNegative
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        if (errorMonto.isNotEmpty()) {
            Text(
                text = errorMonto,
                color = RedNegative,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 24.dp, top = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Info banner ───────────────────────────────────────────────────────
        P2PInfoBanner(
            text = "El retiro se procesará en un plazo de 2 a 24 horas hábiles a tu cuenta vinculada.",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Resumen ───────────────────────────────────────────────────────────
        Text(
            text = "Resumen de retiro",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFAFAFA),
            border = ButtonDefaults.outlinedButtonBorder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                ResumenRow(label = "Moneda", value = "$selectedCurrency (${currencies.first { it.first == selectedCurrency }.second})")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                ResumenRow(label = "Monto a retirar", value = "$symbol ${"%.2f".format(montoDouble)}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                ResumenRow(label = "Saldo restante", value = "$symbol ${"%.2f".format(saldoFinal)}")
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Confirmar ─────────────────────────────────────────────────────────
        P2PButton(
            text = "Confirmar retiro",
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            val amount = monto.toDoubleOrNull()
            when {
                amount == null || amount <= 0 -> errorMonto = "Ingresa un monto válido"
                amount > balance             -> errorMonto = "Saldo insuficiente"
                else -> {
                    // Logic to withdraw
                    GlobalWalletState.addDeposit(selectedCurrency, -amount)
                    onBack()
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Cancelar",
            fontSize = 14.sp,
            color = TextSecondary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onBack() }
        )
    }
}
