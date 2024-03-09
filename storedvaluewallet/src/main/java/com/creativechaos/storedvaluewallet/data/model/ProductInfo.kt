package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.annotations.SerializedName

data class ProductInfo(
    @SerializedName("pid") val productId: Int,
    @SerializedName("ppx") val productExpiry: Long,
    @SerializedName("ppo") val productPurchasedOn: Long
)
