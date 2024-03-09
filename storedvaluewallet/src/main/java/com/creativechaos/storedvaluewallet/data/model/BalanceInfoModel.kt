package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.annotations.SerializedName

data class BalanceInfoModel(
    @SerializedName("lma") val lastModifiedAt: Long,
    @SerializedName("vno") var versionNo: Long,
    @SerializedName("sst") val syncStatus: Int,
    @SerializedName("lso") val lastSyncedOn: Long
)
