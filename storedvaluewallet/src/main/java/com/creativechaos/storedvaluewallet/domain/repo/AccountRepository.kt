package com.creativechaos.storedvaluewallet.domain.repo

import com.creativechaos.storedvaluewallet.data.model.UserAccount

interface AccountRepository {
    fun writeToAccountInfoFile(account: UserAccount, accountFileNumber: Int, accessKey: Int): Boolean
    fun getAccountInfo(accountFileNumber: Int, accessKey: Int): ByteArray
}