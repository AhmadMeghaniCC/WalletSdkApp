package com.creativechaos.storedvaluewallet.domain.repo

interface BalanceInfoRepository {
    fun updateBalanceInfo(data: ByteArray, balanceINfoFileNumber: Int, accessKey: Int): Boolean
    fun getBalanceInfo(balanceInfoFileNumber: Int, accessKey: Int): ByteArray
}