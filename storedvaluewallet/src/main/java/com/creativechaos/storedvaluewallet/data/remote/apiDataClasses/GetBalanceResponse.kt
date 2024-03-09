package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetBalanceResponse(
    val status: Int,
    val message: String,
    val data: GetBalanceData?
)

@Serializable
data class GetBalanceData(
    @SerialName("account_id") val accountId: Int,
    val balance: Int,
    @SerialName("version_no") val versionNo: Int,
    @SerialName("last_modified") val lastModified: String
)
