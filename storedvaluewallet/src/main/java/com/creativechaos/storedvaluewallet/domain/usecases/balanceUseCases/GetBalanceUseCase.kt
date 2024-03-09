package com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases

import com.creativechaos.storedvaluewallet.domain.repo.BalanceRepository

class GetBalanceUseCase(private val balanceRepository: BalanceRepository) {

    fun execute(balanceFileNumber: Int, accessKey: Int): Int {
        // Implement the logic to get the user's balance using the balanceRepository
        return balanceRepository.getBalance(balanceFileNumber, accessKey)
    }
}