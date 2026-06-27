package com.exchangepro.moviles.presentation.navigation

sealed class Route(val value: String) {
    data object Login : Route("login")
    data object Register : Route("register")
    data object Home : Route("home")
    data object Offers : Route("offers")
    data object CreateOffer : Route("create_offer")
    data object MyOffers : Route("my_offers")
    data object Wallet : Route("wallet")
    data object Transactions : Route("transactions")
    data object PaymentData : Route("payment_data")
    data object Disputes : Route("disputes")
    data object Profile : Route("profile")
    data object Notifications : Route("notifications")
    data object Feedback : Route("feedback")
    data object AdminDashboard : Route("admin_dashboard")
    data object AdminDisputes : Route("admin_disputes")
    data object AdminFeedback : Route("admin_feedback")
    data object AdminReports : Route("admin_reports")
    data object AdminNotifications : Route("admin_notifications")
}
