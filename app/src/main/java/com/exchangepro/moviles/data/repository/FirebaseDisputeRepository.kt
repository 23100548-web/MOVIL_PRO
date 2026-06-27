package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.data.firebase.FirebaseCollections
import com.exchangepro.moviles.domain.model.Dispute
import com.exchangepro.moviles.domain.model.TransactionStatus
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseDisputeRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val dbProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
) {
    private fun currentUserId(): String =
        authProvider().currentUser?.uid ?: error("No hay una sesion activa.")

    suspend fun create(transactionId: String, reason: String, description: String): String {
        val db = dbProvider()
        val uid = currentUserId()
        val transactionRef = db.collection(FirebaseCollections.TRANSACTIONS).document(transactionId)
        val disputeRef = db.collection(FirebaseCollections.DISPUTES).document()
        val notificationRef = db.collection(FirebaseCollections.NOTIFICATIONS).document()

        db.runTransaction { transaction ->
            val record = transaction.get(transactionRef)
            require(record.exists()) { "La transaccion ya no existe." }
            require(uid == record.getString("buyerId") || uid == record.getString("sellerId")) {
                "No perteneces a esta transaccion."
            }
            val status = record.getString("status")
            require(status == TransactionStatus.PENDIENTE_PAGO.name || status == TransactionStatus.PAGADO.name) {
                "La transaccion ya no admite disputas."
            }
            transaction.set(
                disputeRef,
                mapOf(
                    "transactionId" to transactionId,
                    "transactionCode" to record.getString("code").orEmpty(),
                    "reporterId" to uid,
                    "buyerId" to record.getString("buyerId").orEmpty(),
                    "sellerId" to record.getString("sellerId").orEmpty(),
                    "reason" to reason.trim(),
                    "description" to description.trim(),
                    "status" to "PENDIENTE",
                    "evidenceAttachmentIds" to emptyList<String>(),
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            transaction.update(
                transactionRef,
                mapOf(
                    "status" to TransactionStatus.EN_DISPUTA.name,
                    "disputeId" to disputeRef.id,
                    "disputedAt" to FieldValue.serverTimestamp()
                )
            )
            val counterpartId = if (uid == record.getString("buyerId")) {
                record.getString("sellerId").orEmpty()
            } else {
                record.getString("buyerId").orEmpty()
            }
            transaction.set(
                notificationRef,
                mapOf(
                    "userId" to counterpartId,
                    "title" to "Nueva disputa",
                    "message" to "Se abrio una disputa en ${record.getString("code").orEmpty()}.",
                    "read" to false,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            null
        }.awaitDispute()
        return disputeRef.id
    }

    suspend fun getMine(): List<Dispute> {
        val snapshot = dbProvider().collection(FirebaseCollections.DISPUTES)
            .whereEqualTo("reporterId", currentUserId())
            .get()
            .awaitDispute()
        return snapshot.documents
            .sortedByDescending { (it.get("createdAt") as? Timestamp)?.toDate()?.time ?: 0L }
            .mapNotNull { it.toDispute() }
    }

    private fun DocumentSnapshot.toDispute(): Dispute? = Dispute(
        id = id,
        transactionCode = getString("transactionCode").orEmpty(),
        reason = getString("reason").orEmpty(),
        status = getString("status").orEmpty(),
        description = getString("description").orEmpty(),
        transactionId = getString("transactionId").orEmpty(),
        evidenceAttachmentIds = (get("evidenceAttachmentIds") as? List<*>)
            ?.mapNotNull { it as? String }
            .orEmpty()
    )
}

private suspend fun <T> Task<T>.awaitDispute(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { continuation.resume(it) }
    addOnFailureListener { continuation.resumeWithException(it) }
    addOnCanceledListener { continuation.cancel() }
}
