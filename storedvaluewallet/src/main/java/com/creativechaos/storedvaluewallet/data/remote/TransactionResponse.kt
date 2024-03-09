package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class TransactionResponse(
    val status: Long,
    val message: String,
    val data: TransactionData?
)
