package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.creativechaos.storedvaluewallet.domain.repo.AccountRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.google.gson.Gson
import com.nxp.nfclib.desfire.IDESFireEV1

class AccountRepositoryImpl(private val appAID: ByteArray, private val desFireEV1: IDESFireEV1) :
    AccountRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)

    override fun writeToAccountInfoFile(
        account: UserAccount,
        accountFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations(appAID)

            dataFileManager.writeDataToStandardFile(
                accountFileNumber,
                Gson().toJson(account).toByteArray(),
                accessKey
            )

            Log.i(
                TAG,
                "Success creating and writing to Standard Data File. Data =  ${Gson().toJson(account)} and byteArray is ${
                    Gson().toJson(account).toByteArray()
                }"
            )
            true
        } catch (e: Exception) {
            Log.i(
                TAG, "writeToAccountInfoFile: ${Gson().toJson(account).toByteArray()} ${
                    e.message.toString()
                }"
            )
            // Handle error
            false
        }
    }

    override fun getAccountInfo(accountFileNumber: Int, accessKey: Int): ByteArray {
        return try {
            dataFileManager.readStandardFile(accountFileNumber, accessKey)
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    private fun performInitialOperations(appAID: ByteArray) {
        cardManager.performInitialChecks()

        if (!cardManager.checkApplicationExists(appAID))
            cardManager.createApplication(appAID)

        cardManager.selectApplicationByAID(appAID)
        cardManager.authenticateToApplication()
    }
}