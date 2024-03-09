package com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases

import android.util.Log
import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.creativechaos.storedvaluewallet.domain.repo.AccountRepository
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.google.gson.Gson

class GetAccountInfoUseCase(private val accountRepository: AccountRepository) {

    fun execute(accountFileNumber: Int, accessKey: Int): UserAccount? {
        // Implement the logic to create an account using the accountRepository
        val accountInfoString =
            String(accountRepository.getAccountInfo(accountFileNumber, accessKey)).replace(
                "\u0000",
                ""
            )
        Log.i(TAG, "GetAccountInfoUseCase: $accountInfoString")
        val userAccount: UserAccount = try {
            Gson().fromJson(accountInfoString, UserAccount::class.java)
        } catch (e: Exception) {
            Log.i(TAG, e.toString())
//            getDefaultUserAccount() // Get a default UserAccount instance
            return null
        }
        return userAccount
    }

    // Define a function to create a default UserAccount instance with your preferred default values
    fun getDefaultUserAccount(): UserAccount {
        return UserAccount.UserAccountBuilder()
            .accountNo(0L)
            .accountTitle("Default Title")
            .accountStatus(0)
            .phoneNumber("Default Phone Number")
            .accountCurrency("ZAR")
            .accountPrecision(0)
            .build()
    }

}