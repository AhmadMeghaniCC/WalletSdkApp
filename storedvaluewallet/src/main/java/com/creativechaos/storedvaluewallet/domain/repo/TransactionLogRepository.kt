package com.creativechaos.storedvaluewallet.domain.repo

interface TransactionLogRepository {
    fun newTransactionLog(
        recordContent: ByteArray,
        transactionLogFileNumber: Int,
        accessKey: Int
    ): Boolean

    fun getTransactionLogs(
        noOfRecords: Int = 0,
        transactionLogFileNumber: Int,
        accessKey: Int,
        recordSize: Int
    ): MutableList<String>
}