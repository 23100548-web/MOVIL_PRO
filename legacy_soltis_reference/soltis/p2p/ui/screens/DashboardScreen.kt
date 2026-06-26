package com.soltis.p2p.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.P2PSectionCard
import com.soltis.p2p.ui.components.BadgeChip
import com.soltis.p2p.ui.theme.*

// ── Data ──────────────────────────────────────────────────────────────────────

data class CurrencyBalance(
    val flag: String,
    val code: String,
    val name: String,
    val amount: String,
    val retained: String,
    val buyRate: String,
    val sellRate: String
)

data class RecentMovement(
    val icon: ImageVector,
    val iconColor: Color,
    val type: String,
    val detail: String,
    val date: String,
    val amount: String,
    val isPositive: Boolean
)

// ── Screen ────────────────────────────────────────────────────────────────────

// ── State ─────────────────────────────────────────────────────────────────────

object GlobalWalletState {
    var penBalance by mutableStateOf(8240.50)
    var penRetained by mutableStateOf(390.00)
    
    var usdBalance by mutableStateOf(2150.00)
    var usdRetained by mutableStateOf(50.00)
    
    var eurBalance by mutableStateOf(66.28)
    var eurRetained by mutableStateOf(6.00)

    fun addDeposit(currency: String, amount: Double) {
        when(currency) {
            "PEN" -> penBalance += amount
            "USD" -> usdBalance += amount
            "EUR" -> eurBalance += amount
        }
    }

    fun retainAmount(currency: String, amount: Double) {
        when(currency) {
            "PEN" -> {
                penBalance -= amount
                penRetained += amount
            }
            "USD" -> {
                usdBalance -= amount
                usdRetained += amount
            }
            "EUR" -> {
                eurBalance -= amount
                eurRetained += amount
            }
        }
    }
}

@Composable
fun DashboardScreen(
    onNavigateToDeposit: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToMovements: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(3) } // 3 = Wallet por defecto
    var showCoincidences by remember { mutableStateOf(false) }

    // Reset flow state when leaving the "Ofertas" tab to ensure fresh start on return
    LaunchedEffect(selectedTab) {
        if (selectedTab != 2) {
            showCoincidences = false
        }
    }

    val tabs = listOf("Inicio", "Mercado", "Ofertas", "Wallet", "Perfil")
    val tabIcons = listOf(
        Icons.Default.Home,
        Icons.Default.BarChart,
        Icons.Default.LocalOffer,
        Icons.Default.AccountBalanceWallet,
        Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp
            ) {
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        icon = {
                            Icon(tabIcons[index], contentDescription = label)
                        },
                        label = { Text(label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = YellowPrimary,
                            selectedTextColor   = YellowPrimary,
                            unselectedIconColor = Color(0xFFBDBDBD),
                            unselectedTextColor = Color(0xFFBDBDBD),
                            indicatorColor      = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFFF8F1))
        ) {
            when (selectedTab) {
                0 -> InicioTabContent(
                    onNavigateToDeposit = onNavigateToDeposit,
                    onNavigateToOfertas = { selectedTab = 2 }
                )
                1 -> MercadoTabContent()
                2 -> {
                    if (showCoincidences) {
                        CoincidencesScreen(onBack = { showCoincidences = false })
                    } else {
                        PublishOfferScreen(
                            onBack = { selectedTab = 0 },
                            onPublished = { showCoincidences = true }
                        )
                    }
                }
                3 -> WalletTabContent(
                    onNavigateToDeposit = onNavigateToDeposit,
                    onNavigateToWithdraw = onNavigateToWithdraw,
                    onNavigateToTransfer = onNavigateToTransfer,
                    onNavigateToMovements = onNavigateToMovements
                )
                4 -> PerfilTabContent(onLogout)
            }
        }
    }
}

@Composable
fun InicioTabContent(
    onNavigateToDeposit: () -> Unit,
    onNavigateToOfertas: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Cabecera de bienvenida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Column {
                Text("¡Hola, Juan!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Bienvenido de nuevo a Nexus Pay", color = Color(0xFFAAAAAA), fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
                Surface(
                    color = DarkSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Saldo total disponible (est.)", color = Color(0xFFAAAAAA), fontSize = 12.sp)
                        val totalEstimatedPen = GlobalWalletState.penBalance + (GlobalWalletState.usdBalance * 3.75) + (GlobalWalletState.eurBalance * 4.05)
                        Text("S/ ${"%,.2f".format(totalEstimatedPen)}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Acciones rápidas", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionItem("Comprar", Icons.Default.AddChart, YellowPrimary, Modifier.weight(1f)) {
                onNavigateToOfertas()
            }
            QuickActionItem("Vender", Icons.Default.HistoryEdu, OrangeRetained, Modifier.weight(1f)) {
                onNavigateToOfertas()
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        Text("Mercado en tiempo real", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        CurrenciesSection()
    }
}

@Composable
fun QuickActionItem(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.height(80.dp).clickable { onClick() },
        shadowElevation = 1.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}

@Composable
fun MercadoTabContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBg, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(horizontal = 20.dp, vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tipos de Cambio", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("En vivo desde Nexus Pay", color = Color(0xFFAAAAAA), fontSize = 14.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Pares de divisas en Nexus Pay", modifier = Modifier.padding(horizontal = 20.dp), fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        CurrenciesSection()
    }
}

@Composable
fun PerfilTabContent(onLogout: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Box(modifier = Modifier.size(100.dp).background(YellowPrimary, CircleShape), contentAlignment = Alignment.Center) {
            Text("JM", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Juan Martínez", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("juan.martinez@email.com", fontSize = 14.sp, color = TextSecondary)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            ProfileOptionItem("Mi Cuenta", Icons.Default.Person)
            ProfileOptionItem("Seguridad", Icons.Default.Security)
            ProfileOptionItem("Notificaciones", Icons.Default.Notifications)
            ProfileOptionItem("Ayuda y Soporte", Icons.Default.Help)
            Spacer(modifier = Modifier.height(20.dp))
            ProfileOptionItem("Cerrar Sesión", Icons.Default.ExitToApp, isDestructive = true, onClick = onLogout)
        }
    }
}

@Composable
fun ProfileOptionItem(label: String, icon: ImageVector, isDestructive: Boolean = false, onClick: () -> Unit = {}) {
    val color = if (isDestructive) RedNegative else TextPrimary
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, fontSize = 16.sp, color = color, modifier = Modifier.weight(1f))
        if (!isDestructive) Icon(Icons.Default.ChevronRight, null, tint = TextHint)
    }
    HorizontalDivider(color = Color(0xFFF0F0F0))
}

@Composable
fun WalletTabContent(
    onNavigateToDeposit: () -> Unit,
    onNavigateToWithdraw: () -> Unit,
    onNavigateToTransfer: () -> Unit,
    onNavigateToMovements: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Dark header ───────────────────────────────────────────────────
        WalletHeader(
            onDeposit    = onNavigateToDeposit,
            onWithdraw   = onNavigateToWithdraw,
            onTransfer   = onNavigateToTransfer,
            onProfile    = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Currencies ────────────────────────────────────────────────────
        CurrenciesSection()

        Spacer(modifier = Modifier.height(12.dp))

        // ── Recent movements ──────────────────────────────────────────────
        RecentMovementsSection(onVerTodos = onNavigateToMovements)

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// ── Dark header composable ────────────────────────────────────────────────────

@Composable
fun WalletHeader(
    onDeposit: () -> Unit,
    onWithdraw: () -> Unit,
    onTransfer: () -> Unit,
    onProfile: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = DarkBg,
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp)
            .padding(top = 52.dp, bottom = 28.dp)
    ) {
        Column {
            // Top row: profile + title + language
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile button
                IconButton(
                    onClick = onProfile,
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkSurface, CircleShape)
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Perfil",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }

                // Title centered
                Text(
                    text = "Wallet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.width(40.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Saldo total estimado 👁",
                        fontSize = 13.sp,
                        color = Color(0xFFAAAAAA)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val totalEstimatedPen = GlobalWalletState.penBalance + (GlobalWalletState.usdBalance * 3.75) + (GlobalWalletState.eurBalance * 4.05)
                    val totalEstimatedUsd = totalEstimatedPen / 3.75
                    Text(
                        text = "S/ ${"%,.2f".format(totalEstimatedPen)}",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "≈ $ ${"%,.2f".format(totalEstimatedUsd)} USD",
                        fontSize = 13.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }

                // Coins icon placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(DarkSurface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("💰", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton("Depositar",  Icons.Default.Add,            onDeposit)
                ActionButton("Retirar",    Icons.Default.ArrowUpward,    onWithdraw)
                ActionButton("Transferir", Icons.Default.SwapHoriz,      onTransfer)
            }
        }
    }
}

@Composable
fun ActionButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DarkSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label,
                tint = Color.White, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, color = Color.White)
    }
}

// ── Currencies section ────────────────────────────────────────────────────────

@Composable
fun CurrenciesSection() {
    val currencies = listOf(
        CurrencyBalance("🇵🇪", "PEN", "Sol peruano",          "S/ ${"%,.2f".format(GlobalWalletState.penBalance)}", "S/ ${"%,.2f".format(GlobalWalletState.penRetained)}",  "3.72", "3.78"),
        CurrencyBalance("🇺🇸", "USD", "Dólar estadounidense", "$ ${"%,.2f".format(GlobalWalletState.usdBalance)}",  "$ ${"%,.2f".format(GlobalWalletState.usdRetained)}",    "1.00", "1.02"),
        CurrencyBalance("🇪🇺", "EUR", "Euro",                 "€ ${"%,.2f".format(GlobalWalletState.eurBalance)}",     "€ ${"%,.2f".format(GlobalWalletState.eurRetained)}",     "1.08", "1.10")
    )

    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            currencies.forEachIndexed { index, currency ->
                CurrencyRow(currency = currency)
                if (index < currencies.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 68.dp),
                        color = Color(0xFFF0F0F0)
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyRow(currency: CurrencyBalance) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Flag
        Text(currency.flag, fontSize = 28.sp, modifier = Modifier.size(40.dp))

        Spacer(modifier = Modifier.width(12.dp))

        // Name + rates
        Column(modifier = Modifier.weight(1f)) {
            Text(currency.code, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(currency.name, fontSize = 12.sp, color = TextSecondary)
            Text("Compra: ${currency.buyRate}", fontSize = 11.sp, color = Color(0xFFAAAAAA))
            Text("Venta: ${currency.sellRate}", fontSize = 11.sp, color = Color(0xFFAAAAAA))
        }

        // Amount
        Column(horizontalAlignment = Alignment.End) {
            Text(currency.amount, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Retenido: ${currency.retained}", fontSize = 11.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.width(4.dp))

        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = Color(0xFFCCCCCC), modifier = Modifier.size(20.dp))
    }
}

// ── Recent movements section ──────────────────────────────────────────────────

@Composable
fun RecentMovementsSection(onVerTodos: () -> Unit) {
    val movements = listOf(
        RecentMovement(Icons.Default.Add,        GreenPositive,  "Depósito",               "PEN - BCP",    "06 may 2024, 11:12", "+S/ 500.00",  true),
        RecentMovement(Icons.Default.ArrowUpward, RedNegative,   "Retiro",                 "USD - PayPal", "23 may 2024, 11:45", "-\$ 150.00",   false),
        RecentMovement(Icons.Default.SwapHoriz,  RedNegative,   "Transferencia enviada",  "PEN - Giuseppina", "22 may 2024, 12:18", "-S/ 300.00", false)
    )

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Movimientos recientes",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Ver todos",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = YellowPrimary,
                    modifier = Modifier.clickable { onVerTodos() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            movements.forEach { mov ->
                MovementRow(movement = mov)
                HorizontalDivider(color = Color(0xFFF5F5F5))
            }
        }
    }
}

@Composable
fun MovementRow(movement: RecentMovement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFFFF3E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(movement.icon, contentDescription = null,
                tint = movement.iconColor, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(movement.type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(movement.detail, fontSize = 11.sp, color = TextSecondary)
            Text(movement.date, fontSize = 11.sp, color = Color(0xFFAAAAAA))
        }

        // Amount
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = movement.amount,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (movement.isPositive) GreenPositive else RedNegative
            )
            Text(
                text = if (movement.isPositive) "Completado" else "Completado",
                fontSize = 11.sp,
                color = if (movement.isPositive) GreenPositive else RedNegative
            )
        }
    }
}
