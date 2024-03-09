package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.annotations.SerializedName

data class SalesInfoRecord(
    @SerializedName("cri") val coRelationId: String,
    @SerializedName("txt") val transactionType: TransactionType,
    @SerializedName("mai") val merchantAccountId: Long,
    @SerializedName("tdt") val txnDateTime: Long,
    @SerializedName("amt") val amount: Long,
    @SerializedName("rid") val referenceId: String
)