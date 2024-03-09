package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDetailResponse(
    val status: Int,
    val message: String,
    val data: List<WalletDetail>
)

@Serializable
data class WalletDetail(
    val walletId: Int,
    val walletCode: String,
    val walletName: String,
    @SerialName("account_no")
    val merchantAccountNo: String,
    val appId: String,
    @SerialName("appAccessKey")
    val accessKey: String,
    @SerialName("Role")
    val role: String,
    @SerialName("Rights")
    val rights: List<AccessRights>,
    @SerialName("FileStructure")
    val fileStructures: List<FileStructure>
)

@Serializable
data class AccessRights(
    @SerialName("FileNo")
    val fileNumber: Int,
    val accessKey: Int,
    @SerialName("Rights")
    val rights: String
)





