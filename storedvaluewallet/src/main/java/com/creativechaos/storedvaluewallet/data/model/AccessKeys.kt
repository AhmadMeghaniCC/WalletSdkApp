package com.creativechaos.storedvaluewallet.data.model

data class AccessKeys(
    val readAccessKey: Int = 0,
    val writeAccessKey: Int = 0,
    val readWriteAccessKey: Int = 0,
    val changeAccessKey: Int = 0
)