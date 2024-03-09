package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.BalanceInfoRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.google.gson.Gson
import com.nxp.nfclib.desfire.IDESFireEV1

class BalanceInfoRepositoryImpl(
    private val appAID: ByteArray,
    private val desFireEV1: IDESFireEV1
) :
    BalanceInfoRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)
    override fun updateBalanceInfo(
        data: ByteArray,
        balanceINfoFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations(appAID)
            dataFileManager.writeDataToStandardFile(
                balanceINfoFileNumber,
                data,
                accessKey
            )

            Log.i(
                StoreValueWallet.TAG,
                "Success creating and writing to Standard Data File. Data =  ${Gson().toJson(data)} and byteArray is ${
                    Gson().toJson(data).toByteArray()
                }"
            )
            true
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message.toString())
            // Handle error
            false
        }
    }

    override fun getBalanceInfo(balanceInfoFileNumber: Int, accessKey: Int): ByteArray {
        return try {
            dataFileManager.readStandardFile(balanceInfoFileNumber, accessKey)
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