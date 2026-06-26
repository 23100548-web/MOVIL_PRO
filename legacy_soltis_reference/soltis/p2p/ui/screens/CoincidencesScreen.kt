package com.soltis.p2p.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.BadgeChip
import com.soltis.p2p.ui.components.P2PInfoBanner
import com.soltis.p2p.ui.components.P2PSectionCard
import com.soltis.p2p.ui.theme.*

// ── Model ─────────────────────────────────────────────────────────────────────

data class CompatibleOfferUi(
    val initials: String,
    val avatarColor: Color,
    val userName: String,
    val stats: String,
    val compatibility: Int,
    val tipoCambio: String,
    val limites: String,
    val metodos: List<String>
)

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun CoincidencesScreen(onBack: () -> Unit) {

    val offers = listOf(
        CompatibleOfferUi("JM", Color(0xFF3498DB), "juan.martinez",
            "126 operaciones • 100% positivas", 85,
            "S/ 3.78 por USD", "S/ 500.00 – S/ 2,500.00", listOf("BCP", "BBVA", "Yape")),
        CompatibleOfferUi("LG", Color(0xFF27AE60), "luisa.garcia",
            "98 operaciones • 98% positivas", 92,
            "S/ 3.78 por USD", "S/ 500.00 – S/ 2,000.00", listOf("BCP", "BBVA", "Plin")),
        CompatibleOfferUi("AR", Color(0xFFE67E22), "alex.ramos",
            "55 operaciones • 98% positivas", 78,
            "S/ 3.77 por USD", "S/ 700.00 – S/ 2,500.00", listOf("Interbank", "Plin"))
    )

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF7F7F7))) {

        // ── Top bar ───────────────────────────────────────────────────────────
        Surface(color = Color.White, shadowElevation = 2.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = TextPrimary)
                }
                Text(
                    text = "Coincidencias",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Subtitle ──────────────────────────────────────────────────────
            item {
                Text(
                    text = "Hemos encontrado ofertas compatibles con tu publicación.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }

            // ── Tu oferta card ────────────────────────────────────────────────
            item {
                P2PSectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tu oferta", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            color = TextPrimary, modifier = Modifier.weight(1f))

                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFF3E0), RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Publicada", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                color = YellowPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFF3E0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SwapHoriz, null,
                                tint = YellowPrimary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("USD → PEN", fontSize = 13.sp, color = TextSecondary)
                            Text("$ 2,000.00", fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    OfertaInfoRow("Tipo de cambio",   "S/ 3.80 por USD")
                    Spacer(modifier = Modifier.height(8.dp))
                    OfertaInfoRow("Límites de la oferta", "S/ 500.00 – S/ 3,000.00")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Métodos de pago", fontSize = 12.sp, color = TextSecondary,
                            modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            BadgeChip("BCP",          Color(0xFF2980B9))
                            BadgeChip("Wallet interno", Color(0xFF7B2D8B))
                            BadgeChip("+1",           Color(0xFFEEEEEE), TextSecondary)
                        }
                    }
                }
            }

            // ── Ofertas compatibles header ─────────────────────────────────────
            item {
                Text("Ofertas compatibles ①", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary)
            }

            // ── Each compatible offer ─────────────────────────────────────────
            items(offers) { offer ->
                CompatibleOfferCard(offer)
            }

            // ── Disclaimer ────────────────────────────────────────────────────
            item {
                P2PInfoBanner(
                    text = "Las coincidencias en Nexus Pay se generan automáticamente y están sujetas a la confirmación de la operación."
                )
            }
        }
    }
}

// ── Sub-composables ───────────────────────────────────────────────────────────

@Composable
fun OfertaInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.weight(1f))
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
fun CompatibleOfferCard(offer: CompatibleOfferUi) {
    val compatColor = when {
        offer.compatibility >= 90 -> Color(0xFF27AE60)
        offer.compatibility >= 75 -> YellowPrimary
        else                      -> RedNegative
    }
    val compatBg = when {
        offer.compatibility >= 90 -> Color(0xFFE8F8F0)
        offer.compatibility >= 75 -> Color(0xFFFFF8E1)
        else                      -> Color(0xFFFDECEA)
    }

    P2PSectionCard {
        // Row 1: avatar + name + badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(offer.avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(offer.initials, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = Color.White)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(offer.userName, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary)
                Text(offer.stats, fontSize = 11.sp, color = Color(0xFFAAAAAA))
            }

            // Compatibility badge
            Box(
                modifier = Modifier
                    .background(compatBg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("${offer.compatibility}% compatible", fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, color = compatColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: tipo de cambio + límites
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Tipo de cambio", fontSize = 11.sp, color = Color(0xFFAAAAAA))
                Text(offer.tipoCambio, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Límites", fontSize = 11.sp, color = Color(0xFFAAAAAA))
                Text(offer.limites, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    color = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 3: payment methods + button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                offer.metodos.take(3).forEach { method ->
                    BadgeChip(method, Color(0xFF2980B9))
                }
            }

            Button(
                onClick = { },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFF3E0),
                    contentColor   = YellowPrimary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("Ver detalle", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
