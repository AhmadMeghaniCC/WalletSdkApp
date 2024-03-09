package com.creativechaos.storedvaluewallet.data.model

data class ServerBalance(
    val accountId: String,
    val serverBalance: Double,
    val lastUpdatedOnServer: String,
    )