package com.exchangepro.moviles.data.repository

import com.exchangepro.moviles.domain.model.AppUser
import com.exchangepro.moviles.domain.model.CurrencyCode
import com.exchangepro.moviles.domain.model.Dispute
import com.exchangepro.moviles.domain.model.NotificationItem
import com.exchangepro.moviles.domain.model.Offer
import com.exchangepro.moviles.domain.model.OfferStatus
import com.exchangepro.moviles.domain.model.OperationType
import com.exchangepro.moviles.domain.model.PaymentData
import com.exchangepro.moviles.domain.model.Transaction
import com.exchangepro.moviles.domain.model.TransactionStatus
import com.exchangepro.moviles.domain.model.UserRole
import com.exchangepro.moviles.domain.model.Wallet
import com.exchangepro.moviles.domain.model.WalletBalance

object MockExchangeRepository {
    val currentUser = AppUser(
        id = "user_demo_001",
        role = UserRole.USER,
        fullName = "Gustavo Ramirez",
        email = "gustavo@exchangepro.pe",
        phone = "999 888 777",
        documentNumber = "74859612",
        reputation = 4.8,
        totalRatings = 32
    )

    val wallet = Wallet(
        userId = currentUser.id,
        balances = listOf(
            WalletBalance(CurrencyCode.PEN, available = 4250.50, retained = 350.00),
            WalletBalance(CurrencyCode.USD, available = 980.00, retained = 120.00)
        )
    )

    val offers = listOf(
        Offer(
            id = "offer_001",
            userId = "user_ana",
            userName = "Ana Torres",
            operationType = OperationType.VENTA,
            fromCurrency = CurrencyCode.USD,
            toCurrency = CurrencyCode.PEN,
            exchangeRate = 3.72,
            offeredAmount = 1200.00,
            minimumAmount = 100.00,
            paymentMethods = listOf("Yape", "BCP"),
            status = OfferStatus.ACTIVA
        ),
        Offer(
            id = "offer_002",
            userId = "user_luis",
            userName = "Luis Chang",
            operationType = OperationType.COMPRA,
            fromCurrency = CurrencyCode.PEN,
            toCurrency = CurrencyCode.USD,
            exchangeRate = 3.69,
            offeredAmount = 3500.00,
            minimumAmount = 300.00,
            paymentMethods = listOf("Plin", "Interbank"),
            status = OfferStatus.ACTIVA
        ),
        Offer(
            id = "offer_003",
            userId = currentUser.id,
            userName = currentUser.fullName,
            operationType = OperationType.VENTA,
            fromCurrency = CurrencyCode.USD,
            toCurrency = CurrencyCode.PEN,
            exchangeRate = 3.73,
            offeredAmount = 500.00,
            minimumAmount = 50.00,
            paymentMethods = listOf("Yape"),
            status = OfferStatus.ACTIVA
        )
    )

    val transactions = listOf(
        Transaction(
            id = "trx_001",
            code = "EX-2026-0001",
            offerId = "offer_001",
            buyerId = currentUser.id,
            buyerName = currentUser.fullName,
            sellerId = "user_ana",
            sellerName = "Ana Torres",
            paymentMethod = "Yape",
            operationAmount = 250.00,
            totalToPay = 930.00,
            currency = CurrencyCode.USD,
            status = TransactionStatus.PENDIENTE_PAGO
        ),
        Transaction(
            id = "trx_002",
            code = "EX-2026-0002",
            offerId = "offer_002",
            buyerId = "user_luis",
            buyerName = "Luis Chang",
            sellerId = currentUser.id,
            sellerName = currentUser.fullName,
            paymentMethod = "BCP",
            operationAmount = 150.00,
            totalToPay = 553.50,
            currency = CurrencyCode.USD,
            status = TransactionStatus.COMPLETADO
        )
    )

    val paymentData = listOf(
        PaymentData("pay_001", currentUser.id, "Yape", null, "999888777", "Yape personal"),
        PaymentData("pay_002", currentUser.id, "Banco", "BCP", "191-12345678-0-12", "Cuenta soles")
    )

    val disputes = listOf(
        Dispute("disp_001", "EX-2026-0005", "Comprobante no coincide con el monto", "EN_REVISION")
    )

    val notifications = listOf(
        NotificationItem("not_001", "Nueva oferta activa", "Ana publico una tasa competitiva para USD.", false),
        NotificationItem("not_002", "Pago pendiente", "Recuerda subir tu comprobante para EX-2026-0001.", false),
        NotificationItem("not_003", "Operacion completada", "Tu intercambio EX-2026-0002 fue liberado.", true)
    )
}
