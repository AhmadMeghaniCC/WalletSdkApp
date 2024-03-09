package com.creativechaos.storedvaluewallet.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ResetCardResponse(
    val status: Int,
    val message: String
)
