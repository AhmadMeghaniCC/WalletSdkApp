package com.creativechaos.storedvaluewallet.domain.repo

interface LedgerRepository {
    fun getMiniStatement(noOfRecords: Int, fileNumber: Int,  accessKey: Int, recordSize: Int): MutableList<String>

    //    fun creditLedger(amount: Int): Boolean
//    fun debitLedger(amount: Int): Boolean
    fun addLedgerEntry(recordContent: ByteArray, fileNumber: Int, accessKey: Int): Boolean

}