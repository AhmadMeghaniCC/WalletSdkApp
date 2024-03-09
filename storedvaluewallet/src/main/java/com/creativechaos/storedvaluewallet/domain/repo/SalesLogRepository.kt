package com.creativechaos.storedvaluewallet.domain.repo

interface SalesLogRepository {
    //    fun getLastSaleInfo()
    fun getSalesLog(
        noOfRecords: Int,
        salesLogFileNumber: Int,
        recordSize: Int,
        accessKey: Int
    ): MutableList<String>

    fun newSale(recordContent: ByteArray, salesLogFileNumber: Int, accessKey: Int): Boolean
}