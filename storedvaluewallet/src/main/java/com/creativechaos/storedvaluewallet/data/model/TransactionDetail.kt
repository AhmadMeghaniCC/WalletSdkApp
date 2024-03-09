package com.creativechaos.storedvaluewallet.data.model

data class TransactionDetail(
    val ledgerRecord: LedgerRecord,
    val transactionLogRecord: TransactionLogRecord?,
    val salesLogRecord: SalesInfoRecord? = null
)
