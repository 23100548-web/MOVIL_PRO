package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.data.firebase.FirebaseCollections
import com.exchangepro.moviles.domain.model.AppUser
import com.exchangepro.moviles.domain.model.UserRole
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class FirebaseUserProfile(
    val user: AppUser,
    val names: String,
    val lastNames: String
)

class FirebaseUserRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val dbProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
) {
    fun currentUserId(): String =
        authProvider().currentUser?.uid ?: error("No hay una sesion activa.")

    suspend fun getCurrentUser(): AppUser = getCurrentProfile().user

    suspend fun getCurrentProfile(): FirebaseUserProfile {
        val uid = currentUserId()
        val document = dbProvider()
            .collection(FirebaseCollections.USERS)
            .document(uid)
            .get()
            .awaitUser()

        require(document.exists()) { "No se encontro el perfil del usuario." }

        val role = runCatching {
            UserRole.valueOf(document.getString("role").orEmpty().uppercase())
        }.getOrElse {
            throw IllegalStateException("El perfil no tiene un rol valido.")
        }

        val names = document.getString("names").orEmpty()
        val lastNames = document.getString("lastNames").orEmpty()
        return FirebaseUserProfile(
            user = AppUser(
                id = uid,
                role = role,
                fullName = document.getString("fullName").orEmpty(),
                email = document.getString("email").orEmpty(),
                phone = document.getString("phone").orEmpty(),
                documentNumber = document.getString("documentNumber").orEmpty(),
                reputation = document.getDouble("reputation") ?: 5.0,
                totalRatings = document.getLong("totalRatings")?.toInt() ?: 0,
                photoUrl = document.getString("photoUrl")
            ),
            names = names,
            lastNames = lastNames
        )
    }

    suspend fun updateProfile(names: String, lastNames: String, phone: String) {
        val cleanNames = names.trim()
        val cleanLastNames = lastNames.trim()
        require(cleanNames.isNotBlank() && cleanLastNames.isNotBlank()) {
            "Nombres y apellidos son requeridos."
        }
        require(phone.length == 9 && phone.all(Char::isDigit)) {
            "El telefono debe tener 9 digitos."
        }

        dbProvider()
            .collection(FirebaseCollections.USERS)
            .document(currentUserId())
            .set(
                mapOf(
                    "names" to cleanNames,
                    "lastNames" to cleanLastNames,
                    "fullName" to "$cleanNames $cleanLastNames",
                    "phone" to phone
                ),
                SetOptions.merge()
            )
            .awaitUser()
    }
}

private suspend fun <T> Task<T>.awaitUser(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}
