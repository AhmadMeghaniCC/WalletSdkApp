package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionDetailResponse(
    @SerialName("status") val status: Int,
    @SerialName("message") val message: String,
    @SerialName("data") val data: TransactionDetailData
)

@Serializable
data class TransactionDetailData(
    @SerialName("transaction_id") val transactionId: Int,
    @SerialName("transaction_no") val transactionNo: String,
    @SerialName("correlation_id") val correlationId: String,
    @SerialName("merchant_name") val merchantName: String,
    @SerialName("customer_name") val customerName: String,
    @SerialName("transaction_initiated_on") val transactionInitiatedOn: String,
    @SerialName("transaction_mode") val transactionMode: String,
    @SerialName("transaction_type") val transactionType: String,
    @SerialName("uid") val uid: String,
    @SerialName("currency") val currency: String?,
    @SerialName("amount") val amount: String,
    @SerialName("status") val status: String?,
    @SerialName("created") val created: String?
)

