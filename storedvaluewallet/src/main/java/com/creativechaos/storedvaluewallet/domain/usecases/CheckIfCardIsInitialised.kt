package com.creativechaos.storedvaluewallet.domain.usecases

import com.creativechaos.storedvaluewallet.domain.repo.CardRepository

class CheckIfCardIsInitialised(
    private val cardRepository: CardRepository,
    private val masterAppId: ByteArray,
    private val filesToCheck: List<Int>
) {
    fun execute(): Boolean = cardRepository.isCardInitialised(masterAppId,filesToCheck)

}