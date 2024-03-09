package com.creativechaos.storedvaluewallet.domain.usecases.cardInfoUseCases

import com.creativechaos.storedvaluewallet.data.model.CardInfoModel
import com.creativechaos.storedvaluewallet.domain.repo.CardInfoRepository
import com.google.gson.Gson

class GetCardInfoUseCase(private val cardInfoRepository: CardInfoRepository) {
    fun execute(cardInfoFileNumber: Int, accessKey: Int): CardInfoModel? {
        val rawData = StringBuilder().apply {
            append(
                String(
                    cardInfoRepository.getCardInfo(
                        cardInfoFileNumber,
                        accessKey
                    )
                ).replace("\u0000", "")
            )
        }.toString()

        return if (rawData.isNotBlank()) {
            Gson().fromJson(rawData, CardInfoModel::class.java)
        } else {
            null
        }
    }

}