package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.data.firebase.FirebaseCollections
import com.exchangepro.moviles.domain.model.CurrencyCode
import com.exchangepro.moviles.domain.model.TopUpRequest
import com.exchangepro.moviles.domain.model.Wallet
import com.exchangepro.moviles.domain.model.WalletBalance
import com.exchangepro.moviles.domain.model.WalletMovement
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseWalletRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val dbProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
) {
    private fun userId(): String = authProvider().currentUser?.uid ?: DEMO_USER_ID

    suspend fun getWallet(): Wallet {
        val db = dbProvider()
        val uid = userId()
        val balancesSnapshot = db.collection(FirebaseCollections.WALLETS)
            .document(uid)
            .collection(FirebaseCollections.BALANCES)
            .get()
            .await()

        val balances = balancesSnapshot.documents.mapNotNull { it.toWalletBalance() }

        return if (balances.isNotEmpty()) {
            Wallet(userId = uid, balances = balances.sortedBy { it.currency.ordinal })
        } else {
            fallbackWallet(uid)
        }
    }

    suspend fun getMovements(limit: Long = 20): List<WalletMovement> {
        val db = dbProvider()
        val snapshot = db.collection(FirebaseCollections.WALLET_MOVEMENTS)
            .whereEqualTo("userId", userId())
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toWalletMovement() }
    }

    suspend fun topUp(request: TopUpRequest) {
        val db = dbProvider()
        val uid = userId()
        val walletRef = db.collection(FirebaseCollections.WALLETS).document(uid)
        val balanceRef = walletRef.collection(FirebaseCollections.BALANCES).document(request.currency.name)
        val topUpRef = db.collection(FirebaseCollections.TOP_UPS).document()
        val movementRef = db.collection(FirebaseCollections.WALLET_MOVEMENTS).document()

        db.runTransaction { transaction ->
            val balanceSnapshot = transaction.get(balanceRef)
            val current = balanceSnapshot.getDouble("available") ?: 0.0
            val retained = balanceSnapshot.getDouble("retained") ?: 0.0

            transaction.set(
                walletRef,
                mapOf(
                    "userId" to uid,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            transaction.set(
                balanceRef,
                mapOf(
                    "currency" to request.currency.name,
                    "available" to current + request.amount,
                    "retained" to retained,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            transaction.set(
                topUpRef,
                mapOf(
                    "userId" to uid,
                    "currency" to request.currency.name,
                    "amount" to request.amount,
                    "paymentMethod" to request.paymentMethod,
                    "referenceNumber" to request.referenceNumber,
                    "status" to "COMPLETADA",
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            transaction.set(
                movementRef,
                mapOf(
                    "userId" to uid,
                    "currency" to request.currency.name,
                    "amount" to request.amount,
                    "operationType" to "RECARGA",
                    "result" to "EXITOSO",
                    "referenceType" to request.paymentMethod,
                    "referenceId" to topUpRef.id,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            null
        }.await()
    }

    private fun DocumentSnapshot.toWalletBalance(): WalletBalance? {
        val currency = runCatching { CurrencyCode.valueOf(id) }.getOrNull()
            ?: runCatching { CurrencyCode.valueOf(getString("currency").orEmpty()) }.getOrNull()
            ?: return null

        return WalletBalance(
            currency = currency,
            available = getDouble("available") ?: 0.0,
            retained = getDouble("retained") ?: 0.0
        )
    }

    private fun DocumentSnapshot.toWalletMovement(): WalletMovement? {
        val currency = runCatching { CurrencyCode.valueOf(getString("currency").orEmpty()) }.getOrNull()
            ?: return null
        val timestamp = getTimestamp("createdAt") ?: get("createdAt") as? Timestamp

        return WalletMovement(
            id = id,
            currency = currency,
            amount = getDouble("amount") ?: 0.0,
            operationType = getString("operationType").orEmpty(),
            result = getString("result").orEmpty(),
            referenceType = getString("referenceType"),
            referenceId = getString("referenceId"),
            createdAtMillis = timestamp?.toDate()?.time
        )
    }

    private fun fallbackWallet(uid: String): Wallet = Wallet(
        userId = uid,
        balances = listOf(
            WalletBalance(CurrencyCode.PEN, available = 4_250.50, retained = 0.0),
            WalletBalance(CurrencyCode.USD, available = 980.00, retained = 0.0)
        )
    )

    private companion object {
        const val DEMO_USER_ID = "demo-user"
    }
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}
