package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Customer(
    val id: Int? = null,
    val name: String? = null,
    val surname: String? = null,
    @SerialName("cell_number") val cellNumber: String? = null,
    val email: String? = null,
    val dob: String? = null,
    val city: String? = null,
    val address: String? = null,
    val gender: Int? = null,
    @SerialName("identification_number") val identificationNumber: String? = null,
    val status: Int? = null,
    val created: String? = null,
    val updated: String? = null,
    @SerialName("channel_id")
    val channelId: Int? = null
)
