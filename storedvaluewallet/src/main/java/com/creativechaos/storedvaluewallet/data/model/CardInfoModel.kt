package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.annotations.SerializedName

data class CardInfoModel(
    @SerializedName("uid") val UID: String,
    @SerializedName("isd") val issueDate: Long,
    @SerializedName("cst") val cardStatus: Int,
    @SerializedName("wid") val walletId: Int,
    @SerializedName("wcd") val walletCode: String,
    @SerializedName("vld") val validity: Int,
    @SerializedName("wvn") val walletStructureVersionNumber: Int
    //version of files, more files or less.
    //more to come
)