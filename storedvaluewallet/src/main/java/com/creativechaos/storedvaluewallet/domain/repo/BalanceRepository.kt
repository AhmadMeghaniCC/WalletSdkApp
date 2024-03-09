package com.creativechaos.storedvaluewallet.domain.repo

interface BalanceRepository {
//    fun createBalanceFile(appId: ByteArray, accessKey: Int): Boolean
    fun getBalance(balanceFileNumber: Int, accessKey: Int): Int
    fun creditAccount(balanceFileNumber: Int,amount: Int, accessKey: Int): Boolean
    fun debitAccount(balanceFileNumber: Int,amount: Int, accessKey: Int): Boolean
//    fun topUpBalance(): Boolean
//    fun updateBalance(): Boolean
}