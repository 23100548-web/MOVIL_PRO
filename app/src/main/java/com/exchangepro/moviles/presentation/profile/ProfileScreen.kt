package com.exchangepro.moviles.presentation.profile

import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.exchangepro.moviles.data.image.ImageCompressor
import com.exchangepro.moviles.data.repository.FirebaseAttachmentRepository
import com.exchangepro.moviles.data.repository.FirebaseUserProfile
import com.exchangepro.moviles.data.repository.FirebaseUserRepository
import com.exchangepro.moviles.ui.components.ExchangeCard
import com.exchangepro.moviles.ui.components.PrimaryAction
import com.exchangepro.moviles.ui.theme.ExchangeElevated
import com.exchangepro.moviles.ui.theme.ExchangeMuted
import com.exchangepro.moviles.ui.theme.ExchangePositive
import com.exchangepro.moviles.ui.theme.ExchangePrimary
import com.exchangepro.moviles.ui.theme.ExchangePrimaryLight
import com.exchangepro.moviles.ui.theme.ExchangeWarning
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen() {
    val repository = remember { FirebaseUserRepository() }
    val attachmentRepository = remember { FirebaseAttachmentRepository() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<FirebaseUserProfile?>(null) }
    var names by remember { mutableStateOf("") }
    var lastNames by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var photoBytes by remember { mutableStateOf<ByteArray?>(null) }
    var uploadingPhoto by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }

    fun applyProfile(value: FirebaseUserProfile) {
        profile = value
        names = value.names
        lastNames = value.lastNames
        phone = value.user.phone.filter(Char::isDigit).take(9)
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null && !uploadingPhoto) {
            scope.launch {
                uploadingPhoto = true
                try {
                    val compressed = ImageCompressor.compress(context, uri)
                    val attachmentId = attachmentRepository.uploadProfilePhoto(compressed)
                    photoBytes = attachmentRepository.getImage(attachmentId)
                    applyProfile(repository.getCurrentProfile())
                    message = "Foto de perfil actualizada."
                } catch (error: Exception) {
                    message = error.message ?: "No se pudo guardar la foto."
                } finally {
                    uploadingPhoto = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        try {
            val loaded = repository.getCurrentProfile()
            applyProfile(loaded)
            loaded.user.photoAttachmentId?.let { attachmentId ->
                photoBytes = runCatching { attachmentRepository.getImage(attachmentId) }.getOrNull()
            }
        } catch (error: Exception) {
            message = "No se pudo cargar el perfil: ${error.message.orEmpty()}"
        } finally {
            loading = false
        }
    }

    val profileBitmap = remember(photoBytes) {
        photoBytes?.let { bytes ->
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
        }
    }

    LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Text("Mi Perfil", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Administra tu informacion personal", color = ExchangeMuted)
        }

        if (loading) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = ExchangePrimary)
                }
            }
        } else {
            val currentProfile = profile
            if (currentProfile == null) {
                item {
                    ExchangeCard {
                        Text(message ?: "No se encontro el perfil.", color = ExchangeMuted)
                    }
                }
            } else {
                val user = currentProfile.user

                item {
                    ExchangeCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(82.dp)
                                    .clip(CircleShape)
                                    .background(ExchangePrimary.copy(alpha = 0.20f))
                                    .clickable {
                                        if (!uploadingPhoto) photoPicker.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (profileBitmap != null) {
                                    Image(
                                        bitmap = profileBitmap,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(38.dp))
                                }
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
                            onValueChange = { names = it.take(100) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nombres") },
                            singleLine = true
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = lastNames,
                            onValueChange = { lastNames = it.take(100) },
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
                                enabled = !saving && !uploadingPhoto,
                                onClick = {
                                    names = currentProfile.names
                                    lastNames = currentProfile.lastNames
                                    phone = user.phone.filter(Char::isDigit).take(9)
                                    message = null
                                }
                            ) {
                                Text("Cancelar")
                            }
                            Spacer(Modifier.width(10.dp))
                            PrimaryAction(if (saving) "Guardando..." else "Guardar Cambios", {
                                if (!saving) {
                                    saving = true
                                    scope.launch {
                                        try {
                                            repository.updateProfile(names, lastNames, phone)
                                            applyProfile(repository.getCurrentProfile())
                                            message = "Perfil actualizado exitosamente."
                                        } catch (error: Exception) {
                                            message = error.message ?: "No se pudo actualizar el perfil."
                                        } finally {
                                            saving = false
                                        }
                                    }
                                }
                            })
                        }
                    }
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
