package com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases

import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.data.model.LedgerType
import com.creativechaos.storedvaluewallet.domain.repo.LedgerRepository

class CreditLedgerUseCase(private val ledgerRepository: LedgerRepository) {
    fun execute(
        amount: Int,
        dateTime: Long,
        versionNo: Long,
        fileNumber: Int,
        accessKey: Int
    ): Boolean {
        val data = Utils.getLedgerGsonObject().toJson(
            LedgerRecord(
                amount.toLong(), dateTime, LedgerType.Credit, versionNo
            )
        )
        return ledgerRepository.addLedgerEntry(data.toByteArray(), fileNumber, accessKey)
    }

}