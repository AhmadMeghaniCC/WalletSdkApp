package com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases

import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.creativechaos.storedvaluewallet.domain.repo.AccountRepository
import com.google.gson.Gson

class UpdateAccountStatusUseCase(private val accountRepository: AccountRepository) {

    fun execute(status: Int, accountFileNumber: Int, accessKey: Int): Boolean {
        val accountInfoString =
            String(accountRepository.getAccountInfo(accountFileNumber, accessKey)).replace(
                "\u0000",
                ""
            )
        val account = Gson().fromJson(accountInfoString, UserAccount::class.java)
        account.accountStatus = status

        return accountRepository.writeToAccountInfoFile(account, accountFileNumber, accessKey)
    }

}