package com.creativechaos.storedvaluewallet.domain.repo

interface ProductInfoRepository {
    fun getProductInfo(accessKey: Int): ByteArray
    fun updateProductInfo(data: ByteArray, accessKey: Int): Boolean
}