package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: Int? = null,
    val uid: String? = null,
    @SerialName("serial_no")
    val serialNo: String? = null,
    val checksum: String? = null,
    @SerialName("issuer_balance")
    val issuerBalance: String? = null,
    @SerialName("issuer_id")
    val issuerId: Int? = null,
    @SerialName("network_id")
    val networkId: Int? = null,
    @SerialName("market_id")
    val marketId: String? = null,
    val status: Int? = null,
    @SerialName("parent_id")
    val parentId: Int? = null,
    @SerialName("last_sync")
    val lastSync: String? = null,
    @SerialName("product_id")
    val productId: Int? = null,
    @SerialName("product_activation_date")
    val productActivationDate: String? = null,
    @SerialName("product_expiry_date")
    val productExpiryDate: String? = null,
    @SerialName("product_balance")
    val productBalance: String? = null,
    @SerialName("transit_balance")
    val transitBalance: String? = null,
    @SerialName("program_id")
    val programId: Int? = null,
    @SerialName("program_balance")
    val programBalance: String? = null,
    @SerialName("ticket_data")
    val ticketData: String? = null,
    @SerialName("createdon")
    val createdOn: String? = null,
    @SerialName("updatedon")
    val updatedOn: String? = null
)
