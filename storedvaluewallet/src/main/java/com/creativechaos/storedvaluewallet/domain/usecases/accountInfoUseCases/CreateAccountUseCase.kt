package com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases

import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.creativechaos.storedvaluewallet.domain.repo.AccountRepository

class CreateAccountUseCase(private val accountRepository: AccountRepository) {

    fun execute(account: UserAccount, accountFileNumber: Int, accessKey: Int): Boolean =
        // Implement the logic to create an account using the accountRepository
        accountRepository.writeToAccountInfoFile(account, accountFileNumber, accessKey)

}