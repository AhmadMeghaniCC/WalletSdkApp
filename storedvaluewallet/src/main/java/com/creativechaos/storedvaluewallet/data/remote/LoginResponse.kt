package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val status: Int,
    val message: String,
    val data: LoginData?
)

@Serializable
data class LoginData(
    @SerialName("merchant_info") val merchantAccountInfo: MerchantInfo,
    val token: String
)

@Serializable
data class MerchantInfo(
//    @SerialName("account_no") val merchantAccountNo: String,
    @SerialName("title") val merchantTitle: String
)