package com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases

import com.creativechaos.storedvaluewallet.domain.repo.BalanceRepository

class DebitAccountUseCase(private val balanceRepository: BalanceRepository) {

    fun execute(amount: Int, balanceFileNumber: Int, accessKey: Int): Boolean {
        // Implement the logic to debit the user's account with the specified amount
        return balanceRepository.debitAccount(balanceFileNumber, amount, accessKey)
    }
}