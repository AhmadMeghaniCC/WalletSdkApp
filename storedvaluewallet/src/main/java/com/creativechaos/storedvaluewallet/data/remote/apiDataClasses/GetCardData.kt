package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.Serializable

@Serializable
data class GetCardData(
    val card: Card? = null,
    val customer: Customer? = null,
    val account: Account? = null
)
