package com.soltis.p2p.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.P2PButton
import com.soltis.p2p.ui.components.P2PTextField
import com.soltis.p2p.ui.components.P2PTopLogo
import com.soltis.p2p.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var correo       by remember { mutableStateOf("") }
    var contrasena   by remember { mutableStateOf("") }
    var showPass     by remember { mutableStateOf(false) }
    var recordarme   by remember { mutableStateOf(false) }
    var errorCorreo  by remember { mutableStateOf("") }
    var errorPass    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {

        // ── Top bar: language ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Language pill
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = ButtonDefaults.outlinedButtonBorder,
                modifier = Modifier.clickable { /* TODO: idioma */ }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌐 ES", fontSize = 13.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Logo ─────────────────────────────────────────────────────────────
        P2PTopLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(20.dp))

        // ── Title ─────────────────────────────────────────────────────────────
        Text(
            text = "Iniciar sesión",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle con link en amarillo
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Accede a tu cuenta y comienza a ", fontSize = 13.sp, color = TextSecondary)
        }
        Text(
            text = "comprar y vender divisas.",
            fontSize = 13.sp,
            color = YellowPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Correo ────────────────────────────────────────────────────────────
        P2PTextField(
            label        = "Correo electrónico",
            value        = correo,
            onValueChange = {
                correo = it
                errorCorreo = ""
            },
            hint         = "Ingresa tu correo electrónico",
            leadingIcon  = { Icon(Icons.Default.Email, null, tint = TextHint) },
            error        = errorCorreo,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Contraseña ────────────────────────────────────────────────────────
        P2PTextField(
            label        = "Contraseña",
            value        = contrasena,
            onValueChange = {
                contrasena = it
                errorPass = ""
            },
            hint         = "Ingresa tu contraseña",
            leadingIcon  = { Icon(Icons.Default.Lock, null, tint = TextHint) },
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextHint
                    )
                }
            },
            error                = errorPass,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType         = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Recordarme + ¿Olvidaste? ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = recordarme,
                onCheckedChange = { recordarme = it },
                colors = CheckboxDefaults.colors(
                    checkedColor   = YellowPrimary,
                    uncheckedColor = TextHint
                )
            )
            Text("Recordarme", fontSize = 13.sp, color = TextSecondary)

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 13.sp,
                color = YellowPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    // TODO: navegar a ForgotPasswordScreen
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Botón ingresar ────────────────────────────────────────────────────
        P2PButton(text = "Ingresar") {
            var valid = true
            if (correo.isBlank() || !correo.contains("@")) {
                errorCorreo = "Ingresa un correo válido"
                valid = false
            }
            if (contrasena.isEmpty()) {
                errorPass = "Ingresa tu contraseña"
                valid = false
            }
            if (valid) onLoginSuccess()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── ¿No tienes cuenta? ────────────────────────────────────────────────
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                text = "Regístrate",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}
