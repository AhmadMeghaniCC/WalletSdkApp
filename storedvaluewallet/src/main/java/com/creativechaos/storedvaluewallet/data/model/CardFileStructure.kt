package com.creativechaos.storedvaluewallet.data.model

data class CardFileStructure(
    val fileName: String,
    val fileNo: Int,
    val fileType: String,
    val fileSize: Int,
    val readKey: Int,
    val writeKey: Int,
    val readWriteKey: Int,
    val changeKey: Int,
    val noOfRecords: Int?,
    val recordSize: Int?,
    val maxValue: Int?,
    val minValue: Int?,
    val initialValue: Int?
)