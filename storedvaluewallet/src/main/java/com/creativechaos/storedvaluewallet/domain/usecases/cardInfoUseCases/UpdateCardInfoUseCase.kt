package com.creativechaos.storedvaluewallet.domain.usecases.cardInfoUseCases

import android.util.Log
import com.creativechaos.storedvaluewallet.data.model.CardInfoModel
import com.creativechaos.storedvaluewallet.domain.repo.CardInfoRepository
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.google.gson.Gson

class UpdateCardInfoUseCase(private val cardInfoRepository: CardInfoRepository) {
    fun execute(
        uid: String,
        issueDate: Long,
        cardStatus: Int,
        walletId: Int,
        walletCode: String,
        validity: Int,
        walletStructureVersionNumber: Int,
        cardInfoFileNumber: Int,
        accessKey: Int
    ): Boolean {
        val data = Gson().toJson(
            CardInfoModel(
                UID = uid,
                issueDate = issueDate,
                cardStatus = cardStatus,
                walletId = walletId,
                walletCode = walletCode,
                validity = validity,
                walletStructureVersionNumber = walletStructureVersionNumber
            )
        )
        Log.i(TAG, "UpdateCardInfoUseCase execute: $data")
        return cardInfoRepository.updateCardInfo(
            data = data.toByteArray(),
            cardInfoFileNumber = cardInfoFileNumber,
            accessKey = accessKey
        )
    }
}