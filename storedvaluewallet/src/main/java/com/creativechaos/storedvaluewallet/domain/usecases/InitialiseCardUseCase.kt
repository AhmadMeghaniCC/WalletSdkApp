package com.creativechaos.storedvaluewallet.domain.usecases

import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.FileStructure
import com.creativechaos.storedvaluewallet.domain.repo.CardRepository

class InitialiseCardUseCase(
    private val cardRepository: CardRepository,
    private val cardFileStructures: List<FileStructure>,
    private val masterAppId: ByteArray
) {
    fun execute(): Boolean = cardRepository.initialiseCard(masterAppId, cardFileStructures)
}