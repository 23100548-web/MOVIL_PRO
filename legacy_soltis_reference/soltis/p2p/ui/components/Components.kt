package com.soltis.p2p.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.soltis.p2p.R
import com.soltis.p2p.ui.theme.*

// ── Reusable TextField ────────────────────────────────────────────────────────

@Composable
fun P2PTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    error: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(hint, color = TextHint, fontSize = 14.sp) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            isError = error.isNotEmpty(),
            visualTransformation = visualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = YellowPrimary,
                unfocusedBorderColor = StrokeDefault,
                errorBorderColor     = RedNegative
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = RedNegative,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}

// ── Primary yellow button ─────────────────────────────────────────────────────

@Composable
fun P2PButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = YellowPrimary,
            contentColor   = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

// ── P2P Logo header ───────────────────────────────────────────────────────────

@Composable
fun P2PTopLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "Nexus Pay Logo",
            modifier = Modifier
                .size(44.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Nexus", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text("Pay", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = TextPrimary, letterSpacing = 1.5.sp)
        }
    }
}

// ── Info banner (yellow tinted) ───────────────────────────────────────────────

@Composable
fun P2PInfoBanner(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFBF0), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFFFE0A0), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("ℹ", color = YellowPrimary, fontSize = 14.sp,
            modifier = Modifier.padding(end = 8.dp, top = 1.dp))
        Text(text, fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
    }
}

// ── Section card (white rounded) ──────────────────────────────────────────────

@Composable
fun P2PSectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        color = Color.White,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// ── Payment / currency badge chip ─────────────────────────────────────────────

@Composable
fun BadgeChip(text: String, color: Color, textColor: Color = Color.White) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 10.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}
