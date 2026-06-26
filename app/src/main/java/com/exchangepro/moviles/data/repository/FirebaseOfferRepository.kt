package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.data.firebase.FirebaseCollections
import com.exchangepro.moviles.domain.model.CreateOfferRequest
import com.exchangepro.moviles.domain.model.OperationType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseOfferRepository(
    private val authProvider: () -> FirebaseAuth = { FirebaseAuth.getInstance() },
    private val dbProvider: () -> FirebaseFirestore = { FirebaseFirestore.getInstance() }
) {
    private fun userId(): String = authProvider().currentUser?.uid ?: MockExchangeRepository.currentUser.id

    suspend fun createOffer(request: CreateOfferRequest) {
        val db = dbProvider()
        val uid = userId()
        val holdCurrency = if (request.operationType == OperationType.COMPRA) {
            request.toCurrency
        } else {
            request.fromCurrency
        }
        val requiredAmount = if (request.operationType == OperationType.COMPRA) {
            request.offeredAmount * request.exchangeRate
        } else {
            request.offeredAmount
        }

        val walletRef = db.collection(FirebaseCollections.WALLETS).document(uid)
        val balanceRef = walletRef.collection(FirebaseCollections.BALANCES).document(holdCurrency.name)
        val offerRef = db.collection(FirebaseCollections.OFFERS).document()

        db.runTransaction { transaction ->
            val balance = transaction.get(balanceRef)
            val available = balance.getDouble("available") ?: 0.0
            val retained = balance.getDouble("retained") ?: 0.0

            require(available >= requiredAmount) {
                "Fondos insuficientes en ${holdCurrency.name}. Necesitas %.2f y tienes %.2f.".format(requiredAmount, available)
            }

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
                    "currency" to holdCurrency.name,
                    "available" to available - requiredAmount,
                    "retained" to retained + requiredAmount,
                    "updatedAt" to FieldValue.serverTimestamp()
                ),
                SetOptions.merge()
            )
            transaction.set(
                offerRef,
                mapOf(
                    "userId" to uid,
                    "userName" to MockExchangeRepository.currentUser.fullName,
                    "operationType" to request.operationType.name,
                    "fromCurrency" to request.fromCurrency.name,
                    "toCurrency" to request.toCurrency.name,
                    "exchangeRate" to request.exchangeRate,
                    "offeredAmount" to request.offeredAmount,
                    "minimumAmount" to request.minimumAmount,
                    "status" to "ACTIVA",
                    "heldCurrency" to holdCurrency.name,
                    "heldAmount" to requiredAmount,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            null
        }.await()
    }
}

private suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { continuation ->
    addOnSuccessListener { result -> continuation.resume(result) }
    addOnFailureListener { error -> continuation.resumeWithException(error) }
    addOnCanceledListener { continuation.cancel() }
}
