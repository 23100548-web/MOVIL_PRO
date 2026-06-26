package com.soltis.p2p.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.ui.components.P2PButton
import com.soltis.p2p.ui.components.P2PTextField
import com.soltis.p2p.ui.components.P2PTopLogo
import com.soltis.p2p.ui.theme.*

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var nombreCompleto   by remember { mutableStateOf("") }
    var correo           by remember { mutableStateOf("") }
    var usuario          by remember { mutableStateOf("") }
    var contrasena       by remember { mutableStateOf("") }
    var confirmar        by remember { mutableStateOf("") }
    var aceptaTerminos   by remember { mutableStateOf(false) }
    var showPass         by remember { mutableStateOf(false) }
    var showConfirm      by remember { mutableStateOf(false) }

    var errorNombre      by remember { mutableStateOf("") }
    var errorCorreo      by remember { mutableStateOf("") }
    var errorUsuario     by remember { mutableStateOf("") }
    var errorPass        by remember { mutableStateOf("") }
    var errorConfirm     by remember { mutableStateOf("") }
    var errorTerminos    by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // ── Logo ─────────────────────────────────────────────────────────────
        Spacer(modifier = Modifier.height(32.dp))
        P2PTopLogo(modifier = Modifier.align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(16.dp))

        // ── Title ────────────────────────────────────────────────────────────
        Text(
            text = "Crear cuenta",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Únete a Nexus Pay y accede\na la mejor experiencia de compra y venta de divisas.",
            fontSize = 13.sp,
            color = TextSecondary,
            lineHeight = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Nombre completo ───────────────────────────────────────────────────
        P2PTextField(
            label       = "Nombre completo",
            value       = nombreCompleto,
            onValueChange = {
                nombreCompleto = it
                errorNombre = ""
            },
            hint        = "Ingresa tu nombre completo",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = TextHint) },
            error       = errorNombre,
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Correo ────────────────────────────────────────────────────────────
        P2PTextField(
            label       = "Correo electrónico",
            value       = correo,
            onValueChange = {
                correo = it
                errorCorreo = ""
            },
            hint        = "Ingresa tu correo electrónico",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = TextHint) },
            error       = errorCorreo,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Usuario ───────────────────────────────────────────────────────────
        P2PTextField(
            label       = "Nombre de usuario",
            value       = usuario,
            onValueChange = {
                usuario = it
                errorUsuario = ""
            },
            hint        = "Crea un nombre de usuario",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = TextHint) },
            error       = errorUsuario,
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Contraseña ────────────────────────────────────────────────────────
        P2PTextField(
            label       = "Contraseña",
            value       = contrasena,
            onValueChange = {
                contrasena = it
                errorPass = ""
            },
            hint           = "Crea una contraseña segura",
            leadingIcon    = { Icon(Icons.Default.Lock, null, tint = TextHint) },
            trailingIcon   = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        if (showPass) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextHint
                    )
                }
            },
            error          = errorPass,
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType   = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ── Confirmar contraseña ──────────────────────────────────────────────
        P2PTextField(
            label       = "Confirmar contraseña",
            value       = confirmar,
            onValueChange = {
                confirmar = it
                errorConfirm = ""
            },
            hint           = "Confirma tu contraseña",
            leadingIcon    = { Icon(Icons.Default.Lock, null, tint = TextHint) },
            trailingIcon   = {
                IconButton(onClick = { showConfirm = !showConfirm }) {
                    Icon(
                        if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = TextHint
                    )
                }
            },
            error          = errorConfirm,
            visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardType   = KeyboardType.Password
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Términos y condiciones ────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = {
                    aceptaTerminos = it
                    errorTerminos = false
                },
                colors = CheckboxDefaults.colors(
                    checkedColor   = YellowPrimary,
                    uncheckedColor = if (errorTerminos) RedNegative else TextHint
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = buildAnnotatedString {
                    append("Acepto los ")
                    withStyle(SpanStyle(color = YellowPrimary, fontWeight = FontWeight.Bold)) {
                        append("Términos y Condiciones")
                    }
                    append(" y la ")
                    withStyle(SpanStyle(color = YellowPrimary, fontWeight = FontWeight.Bold)) {
                        append("Política de Privacidad")
                    }
                },
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        if (errorTerminos) {
            Text("Debes aceptar los términos", color = RedNegative, fontSize = 11.sp,
                modifier = Modifier.padding(start = 16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botón registrar ───────────────────────────────────────────────────
        P2PButton(text = "Registrarme") {
            // Validaciones
            var valid = true
            if (nombreCompleto.isBlank()) { errorNombre = "Ingresa tu nombre completo"; valid = false }
            if (!correo.contains("@") || correo.isBlank()) { errorCorreo = "Correo inválido"; valid = false }
            if (usuario.isBlank()) { errorUsuario = "Ingresa un nombre de usuario"; valid = false }
            if (contrasena.length < 6) { errorPass = "Mínimo 6 caracteres"; valid = false }
            if (contrasena != confirmar) { errorConfirm = "Las contraseñas no coinciden"; valid = false }
            if (!aceptaTerminos) { errorTerminos = true; valid = false }
            if (valid) onRegisterSuccess()
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── ¿Ya tienes cuenta? ────────────────────────────────────────────────
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                text = "Inicia sesión",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = YellowPrimary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
