package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    @SerialName("uid") val uid: String,
    @SerialName("wallet_id") val walletId: Long,
    @SerialName("correlation_id") val coRelationId: String,
    @SerialName("merchant_account_no") val merchantAccountNumber: Long,
    @SerialName("customer_account_no") val customerAccountNumber: String,
    @SerialName("amount") val amount: Long,
    @SerialName("card_data") val cardData: String,
    @SerialName("card_current_balance") val cardCurrentBalance: Long,
    @SerialName("card_new_balance") val cardNewBalance: Long,
    @SerialName("transaction_initiated_on") val transactionInitiatedOn: String,
    @SerialName("transaction_mode") val transactionMode: String,
    @SerialName("version_no") val versionNo: Long,
    @SerialName("ref_transaction_id") val refId: String? = null,
    @SerialName("transaction_type") val transactionType: String? = null


)
