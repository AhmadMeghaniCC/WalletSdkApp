package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionData(
    @SerialName("transaction_no") val transactionNo: String,
    @SerialName("correlation_id") val coRelationId: String,
    val amount: Long,
    val uid: String
)
