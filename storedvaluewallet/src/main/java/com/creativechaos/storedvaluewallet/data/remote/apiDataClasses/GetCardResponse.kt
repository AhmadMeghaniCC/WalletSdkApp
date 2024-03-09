package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetCardResponse(
    val status: Int,
    val message: String,
    @SerialName("data") val cardData: GetCardData? = null
)