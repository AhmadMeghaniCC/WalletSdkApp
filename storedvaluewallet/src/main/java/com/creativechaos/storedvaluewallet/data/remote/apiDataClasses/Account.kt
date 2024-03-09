package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Int,
    @SerialName("account_no") val accountNo: String,
    val title: String,
    @SerialName("customer_id") val customerId: Int,
    @SerialName("wallet_id") val walletId: Int,
    @SerialName("wallet_code") val walletCode: String,
    @SerialName("account_category_id") val accountCategoryId: Int,
    @SerialName("account_type_id") val accountTypeId: Int,
    val status: Int,
    val created: String,
    val updated: String
)