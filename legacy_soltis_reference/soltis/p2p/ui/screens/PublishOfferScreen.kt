package com.soltis.p2p.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.*
import com.soltis.p2p.ui.theme.*

data class CurrencyInfo(val code: String, val flag: String, val name: String, val symbol: String)

val availableCurrencies = listOf(
    CurrencyInfo("USD", "🇺🇸", "Dólar estadounidense", "$"),
    CurrencyInfo("PEN", "🇵🇪", "Sol peruano", "S/"),
    CurrencyInfo("EUR", "🇪🇺", "Euro", "€")
)

@Composable
fun PublishOfferScreen(
    onBack: () -> Unit,
    onPublished: () -> Unit
) {
    var isBuyMode          by remember { mutableStateOf(true) }
    var fromCurrency       by remember { mutableStateOf(availableCurrencies[0]) } // USD
    var toCurrency         by remember { mutableStateOf(availableCurrencies[1]) } // PEN
    
    var showFromSelector   by remember { mutableStateOf(false) }
    var showToSelector     by remember { mutableStateOf(false) }

    var monto              by remember { mutableStateOf("") }
    var tipoCambio         by remember { mutableStateOf("") }
    var limiteMin          by remember { mutableStateOf("") }
    var limiteMax          by remember { mutableStateOf("") }
    var terminos           by remember { mutableStateOf("") }
    var errorMonto         by remember { mutableStateOf("") }
    var errorTipo          by remember { mutableStateOf("") }
    val selectedMethods    = remember { mutableStateListOf("Yape") }

    val paymentMethods = listOf("Yape", "Plin", "Transferencia bancaria", "Wallet interno")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 52.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
            }
            Text(
                text = "Publicar oferta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        // ── Logo ──────────────────────────────────────────────────────────────
        P2PTopLogo(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(top = 4.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // ── Compra / Venta toggle ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                .padding(4.dp)
        ) {
            listOf(true to "Compra", false to "Venta").forEach { (isBuy, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isBuyMode == isBuy) YellowPrimary else Color.Transparent,
                            RoundedCornerShape(18.dp)
                        )
                        .clickable { isBuyMode = isBuy }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (isBuyMode == isBuy) FontWeight.Bold else FontWeight.Normal,
                        color = if (isBuyMode == isBuy) Color.White else Color(0xFF888888)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Par de divisas ────────────────────────────────────────────────────
        Text("Par de divisas", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // From
            CurrencySelector(
                flag = fromCurrency.flag,
                code = fromCurrency.code,
                name = fromCurrency.name,
                modifier = Modifier.weight(1f),
                onClick = { showFromSelector = true }
            )

            Icon(Icons.Default.SwapHoriz, contentDescription = null,
                tint = YellowPrimary, modifier = Modifier.size(28.dp).clickable {
                    val temp = fromCurrency
                    fromCurrency = toCurrency
                    toCurrency = temp
                })

            // To
            CurrencySelector(
                flag = toCurrency.flag,
                code = toCurrency.code,
                name = toCurrency.name,
                modifier = Modifier.weight(1f),
                onClick = { showToSelector = true }
            )
        }

        if (showFromSelector) {
            CurrencyPickerDialog(
                currencies = availableCurrencies.filter { it.code != toCurrency.code },
                onDismiss = { showFromSelector = false },
                onSelect = { fromCurrency = it; showFromSelector = false }
            )
        }

        if (showToSelector) {
            CurrencyPickerDialog(
                currencies = availableCurrencies.filter { it.code != fromCurrency.code },
                onDismiss = { showToSelector = false },
                onSelect = { toCurrency = it; showToSelector = false }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Monto ─────────────────────────────────────────────────────────────
        P2PTextField(
            label = "Monto a ofertar",
            value = monto,
            onValueChange = { monto = it; errorMonto = "" },
            hint = "Ingresa el monto",
            leadingIcon = { Text(fromCurrency.symbol, fontWeight = FontWeight.Bold, color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp)) },
            error = errorMonto,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Text(
            text = "Monto mínimo: ${fromCurrency.symbol} 10.00 · Máximo: ${fromCurrency.symbol} 90,000.00",
            fontSize = 10.sp, color = Color(0xFFAAAAAA),
            modifier = Modifier.padding(start = 20.dp, top = 3.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Tipo de cambio ────────────────────────────────────────────────────
        P2PTextField(
            label = "Tipo de cambio",
            value = tipoCambio,
            onValueChange = { tipoCambio = it; errorTipo = "" },
            hint = "Precio por 1 ${fromCurrency.code}",
            leadingIcon = { Text(toCurrency.symbol, fontWeight = FontWeight.Bold, color = TextPrimary,
                modifier = Modifier.padding(start = 4.dp)) },
            error = errorTipo,
            keyboardType = KeyboardType.Decimal,
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Límites ───────────────────────────────────────────────────────────
        Text("Límite mínimo (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = limiteMin,
                onValueChange = { limiteMin = it },
                placeholder = { Text("Mínimo", color = TextHint, fontSize = 13.sp) },
                prefix = { Text("${toCurrency.symbol} ", color = TextPrimary, fontWeight = FontWeight.Bold) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YellowPrimary,
                    unfocusedBorderColor = StrokeDefault
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = limiteMax,
                onValueChange = { limiteMax = it },
                placeholder = { Text("Máximo", color = TextHint, fontSize = 13.sp) },
                prefix = { Text("${toCurrency.symbol} ", color = TextPrimary, fontWeight = FontWeight.Bold) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = YellowPrimary,
                    unfocusedBorderColor = StrokeDefault
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Métodos de pago ───────────────────────────────────────────────────
        Text("Métodos de pago", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            paymentMethods.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { method ->
                        val isSelected = selectedMethods.contains(method)
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .background(
                                    if (isSelected) Color(0xFFFFFBF2) else Color.White,
                                    RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) YellowPrimary else StrokeDefault,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    if (isSelected) selectedMethods.remove(method)
                                    else selectedMethods.add(method)
                                }
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountBalance, null,
                                tint = if (isSelected) YellowPrimary else TextHint,
                                modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(method, fontSize = 12.sp,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Términos de la oferta ─────────────────────────────────────────────
        Text("Términos de la oferta (opcional)", fontSize = 14.sp, fontWeight = FontWeight.Bold,
            color = TextPrimary, modifier = Modifier.padding(horizontal = 20.dp))

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = terminos,
            onValueChange = { if (it.length <= 250) terminos = it },
            placeholder = { Text("Agrega información adicional para los compradores",
                color = TextHint, fontSize = 13.sp) },
            minLines = 3,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YellowPrimary,
                unfocusedBorderColor = StrokeDefault
            ),
            supportingText = { 
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Máximo 250 caracteres", color = TextHint, fontSize = 11.sp)
                    Text("${terminos.length}/250", color = TextHint, fontSize = 11.sp)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Info banner ───────────────────────────────────────────────────────
        val bal = when(fromCurrency.code) {
            "PEN" -> GlobalWalletState.penBalance
            "USD" -> GlobalWalletState.usdBalance
            "EUR" -> GlobalWalletState.eurBalance
            else -> 0.0
        }
        val sym = fromCurrency.symbol
        P2PInfoBanner(
            text = "Solo si lo posees: $sym ${"%.2f".format(bal)}\n" +
                   "Ese es el saldo que podrás usar para completar la ${if (isBuyMode) "compra" else "venta"}",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Publicar ──────────────────────────────────────────────────────────
        P2PButton(
            text = "Publicar oferta",
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            var valid = true
            val montoVal = monto.toDoubleOrNull()
            if (montoVal == null || montoVal < 10) { errorMonto = "Mínimo ${fromCurrency.symbol} 10.00"; valid = false }
            if (montoVal != null && montoVal > 90000) { errorMonto = "Máximo ${fromCurrency.symbol} 90,000.00"; valid = false }
            val tipoVal = tipoCambio.toDoubleOrNull()
            if (tipoVal == null || tipoVal <= 0) { errorTipo = "Tipo de cambio inválido"; valid = false }
            if (selectedMethods.isEmpty()) { valid = false }
            
            // Check if user has enough balance to retain
            if (valid && montoVal != null) {
                val currentBal = when(fromCurrency.code) {
                    "PEN" -> GlobalWalletState.penBalance
                    "USD" -> GlobalWalletState.usdBalance
                    "EUR" -> GlobalWalletState.eurBalance
                    else -> 0.0
                }
                if (montoVal > currentBal) {
                    errorMonto = "Saldo insuficiente"
                    valid = false
                }
            }

            if (valid && montoVal != null) {
                GlobalWalletState.retainAmount(fromCurrency.code, montoVal)
                onPublished()
            }
        }
    }
}

@Composable
fun CurrencyPickerDialog(
    currencies: List<CurrencyInfo>,
    onDismiss: () -> Unit,
    onSelect: (CurrencyInfo) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = YellowPrimary) }
        },
        title = { Text("Seleccionar divisa", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                currencies.forEach { currency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(currency) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(currency.flag, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(currency.code, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                            Text(currency.name, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                }
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun CurrencySelector(
    flag: String,
    code: String,
    name: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, StrokeDefault, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(flag, fontSize = 20.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(code, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(name, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(Icons.Default.KeyboardArrowDown, null,
            modifier = Modifier.size(16.dp), tint = TextSecondary)
    }
}
