package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardRequest(
    @SerialName("serial_no") val serialNo: String,
    val uid: String,
    @SerialName("status") val cardStatus: Int? = null,
    @SerialName("account_no") val accountNo: String? = null,
    @SerialName("wallet_id") val walletId: Int? = null
)
