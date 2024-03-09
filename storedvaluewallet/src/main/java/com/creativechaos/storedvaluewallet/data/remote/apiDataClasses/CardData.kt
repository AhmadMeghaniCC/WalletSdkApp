package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CardData(
    @SerialName("account_info") var accountInfo: String = "",
    @SerialName("balance") var balance: String = "",
    @SerialName("ledger") var ledger: String = "",
    @SerialName("transaction_log") var transactionLog: String = "",
    @SerialName("card_info") var cardInfo: String = "",
    @SerialName("balance_info") var balanceInfo: String = "",
    @SerialName("trip_info_log") var tripInfoLog: String = "",
    @SerialName("sales_info_log") var salesInfoLog: String = "",
    @SerialName("product_info") var productInfo: String = ""
)
