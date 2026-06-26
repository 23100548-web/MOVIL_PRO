package com.soltis.p2p.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.theme.*

// ── Model ─────────────────────────────────────────────────────────────────────

data class MovementDetail(
    val icon: ImageVector,
    val iconColor: Color,
    val type: String,
    val operation: String,
    val date: String,
    val amount: String,
    val currency: String,
    val isPositive: Boolean,
    val category: String   // "recarga" | "retencion" | "liberacion" | "pago" | "devolucion"
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun MovementsScreen(onBack: () -> Unit) {

    var selectedCurrency by remember { mutableStateOf("TODOS") }
    var selectedType     by remember { mutableStateOf("TODOS") }

    val allMovements = listOf(
        MovementDetail(Icons.Default.Add,          GreenPositive,  "Recarga",               "Depósito bancario · BBVA",       "20 may 2025, 10:35 a. m.", "+ S/ 500.00",  "PEN", true,  "recarga"),
        MovementDetail(Icons.Default.Lock,         OrangeRetained, "Retención",             "Operación #NX-84521",           "20 may 2025, 10:18 a. m.", "- S/ 320.00",  "PEN", false, "retencion"),
        MovementDetail(Icons.Default.LockOpen,     GreenPositive,  "Liberación",            "Operación #NX-84521",           "20 may 2025, 10:42 a. m.", "+ S/ 320.00",  "PEN", true,  "liberacion"),
        MovementDetail(Icons.Default.SwapHoriz,    RedNegative,    "Pago con Nexus Pay",    "Operación #NX-95322",           "19 may 2025, 6:30 p. m.", "- USD 45.00",  "USD", false, "pago"),
        MovementDetail(Icons.Default.ArrowDownward,GreenPositive,  "Devolución",            "Operación #NX-34523",           "18 may 2025, 1:20 p. m.", "+ EUR 60.00",  "EUR", true,  "devolucion"),
        MovementDetail(Icons.Default.Lock,         OrangeRetained, "Retención",             "Operación #NX-84824",           "18 may 2025, 11:06 a. m.", "- S/ 150.00", "PEN", false, "retencion"),
        MovementDetail(Icons.Default.LockOpen,     GreenPositive,  "Liberación",            "Operación #NX-56923",           "17 may 2025, 2:50 p. m.", "+ S/ 280.00",  "PEN", true,  "liberacion")
    )

    val filtered = allMovements.filter { mov ->
        val matchCurrency = selectedCurrency == "TODOS" || mov.currency == selectedCurrency
        val matchType     = selectedType == "TODOS"     || mov.category == selectedType
        matchCurrency && matchType
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F7F7))) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 52.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(48.dp))
                    Text(
                        text = "Movimientos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = TextPrimary)
                    }
                }

                // ── Currency tabs ─────────────────────────────────────────────
                val currencyTabs = listOf("TODOS" to "Todos", "PEN" to "PEN", "USD" to "USD", "EUR" to "EUR")
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 10.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    currencyTabs.forEach { (code, label) ->
                        val isSelected = selectedCurrency == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) YellowPrimary else Color.Transparent,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { selectedCurrency = code }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF888888)
                            )
                        }
                    }
                }

                // ── Type chips ────────────────────────────────────────────────
                val typeChips = listOf(
                    "TODOS"      to "Todos",
                    "recarga"    to "Recargas",
                    "retencion"  to "Retenciones",
                    "liberacion" to "Liberaciones",
                    "pago"       to "Pagos"
                )
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    typeChips.forEach { (code, label) ->
                        val isSelected = selectedType == code
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) YellowPrimary else Color.White,
                                    RoundedCornerShape(16.dp)
                                )
                                .run {
                                    if (!isSelected) modifierBorder(
                                        width = 1.dp,
                                        color = Color(0xFFDDDDDD),
                                        shape = RoundedCornerShape(16.dp)
                                    ) else this
                                }
                                .clickable { selectedType = code }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF888888)
                            )
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        // ── List ──────────────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filtered) { mov ->
                MovementDetailRow(mov)
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(start = 70.dp))
            }
        }
    }
}

@Composable
fun MovementDetailRow(mov: MovementDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFFFF3E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(mov.icon, contentDescription = null,
                tint = mov.iconColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(mov.type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(mov.operation, fontSize = 11.sp, color = Color(0xFFAAAAAA))
            Text(mov.date, fontSize = 11.sp, color = Color(0xFFAAAAAA))
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = mov.amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (mov.isPositive) GreenPositive else RedNegative
            )
            Text(
                text = mov.currency,
                fontSize = 11.sp,
                color = Color(0xFFAAAAAA)
            )
        }
    }
}

// Extension for border on Modifier (renamed to avoid conflict with official border)
fun Modifier.modifierBorder(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape) =
    this.then(Modifier.background(Color.Transparent, shape))
        .padding(width)
        .background(Color.Transparent)
