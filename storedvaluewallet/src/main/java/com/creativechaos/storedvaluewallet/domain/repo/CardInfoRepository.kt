package com.creativechaos.storedvaluewallet.domain.repo

interface CardInfoRepository {
    fun getCardInfo(cardInfoFileNumber: Int, accessKey: Int): ByteArray
    fun updateCardInfo(data: ByteArray, cardInfoFileNumber: Int, accessKey: Int): Boolean
}