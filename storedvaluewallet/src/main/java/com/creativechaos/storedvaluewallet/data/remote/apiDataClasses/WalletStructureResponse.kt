package com.creativechaos.storedvaluewallet.data.remote.apiDataClasses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletStructureResponse(
    val status: Int, val message: String, val data: List<Wallet>
)

@Serializable
data class Wallet(
    val walletId: Int,
    val walletCode: String,
    val walletName: String,
    val appId: String,
    val appAccessKey: String,
    @SerialName("FileStructure")
    val fileStructure: List<FileStructure>
)
@Serializable
data class FileStructure(
    @SerialName("FileName")
    val fileName: String,

    @SerialName("FileNo")
    val fileNo: Int,

    @SerialName("FileType")
    val fileType: String,

    @SerialName("FileSize")
    val fileSize: Int,

    @SerialName("ReadKey")
    val readKey: Int,

    @SerialName("WriteKey")
    val writeKey: Int,

    @SerialName("ReadWriteKey")
    val readWriteKey: Int,

    @SerialName("ChangeKey")
    val changeKey: Int,

    @SerialName("NoOfRecords")
    val noOfRecords: Int?,

    @SerialName("RecordSize")
    val recordSize: Int?,
    val maxValue: Int?,
    val minValue: Int?,
    val initialValue: Int?
)

