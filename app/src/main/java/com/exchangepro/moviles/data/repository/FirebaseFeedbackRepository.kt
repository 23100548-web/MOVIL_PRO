package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.data.firebase.FirebaseCollections
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class UserFeedback(
    val id: String,
    val type: String,
    val title: String,
    val description: String,
    val status: String,
    val adminResponse: String?
)

class FirebaseFeedbackRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val dbProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
) {
    private fun currentUserId(): String =
        authProvider().currentUser?.uid ?: error("No hay una sesion activa.")

    suspend fun create(type: String, title: String, description: String) {
        val uid = currentUserId()
        dbProvider().collection(FirebaseCollections.FEEDBACK).document().set(
            mapOf(
                "userId" to uid,
                "type" to type,
                "title" to title.trim(),
                "description" to description.trim(),
                "status" to "PENDIENTE",
                "adminResponse" to null,
                "createdAt" to FieldValue.serverTimestamp()
            )
        ).awaitFeedback()
    }

    suspend fun getMine(): List<UserFeedback> {
        val snapshot = dbProvider().collection(FirebaseCollections.FEEDBACK)
            .whereEqualTo("userId", currentUserId())
            .get()
            .awaitFeedback()
        return snapshot.documents.map {
            UserFeedback(
                id = it.id,
                type = it.getString("type").orEmpty(),
                title = it.getString("title").orEmpty(),
                description = it.getString("description").orEmpty(),
                status = it.getString("status").orEmpty(),
                adminResponse = it.getString("adminResponse")
            )
        }
    }
}

private suspend fun <T> Task<T>.awaitFeedback(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { continuation.resume(it) }
    addOnFailureListener { continuation.resumeWithException(it) }
    addOnCanceledListener { continuation.cancel() }
}
