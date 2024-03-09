package com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases

import com.creativechaos.storedvaluewallet.domain.repo.BalanceRepository

class CreditAccountUseCase(private val balanceRepository: BalanceRepository) {

    fun execute(balanceFileNumber: Int, amount: Int, accessKey: Int): Boolean {
        // Implement the logic to debit the user's account with the specified amount
        return balanceRepository.creditAccount(amount, balanceFileNumber, accessKey)
    }
}