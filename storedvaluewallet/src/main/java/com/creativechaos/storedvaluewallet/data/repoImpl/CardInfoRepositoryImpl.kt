package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.CardInfoRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.nxp.nfclib.desfire.IDESFireEV1

class CardInfoRepositoryImpl(private val appID: ByteArray, private val desFireEV1: IDESFireEV1) :
    CardInfoRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)
    override fun getCardInfo(cardInfoFileNumber: Int, accessKey: Int): ByteArray {
        return try {
            dataFileManager.readStandardFile(cardInfoFileNumber, accessKey)
        } catch (e: Exception) {
            e.printStackTrace()
            ByteArray(0)
        }
    }

    override fun updateCardInfo(
        data: ByteArray,
        cardInfoFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            Log.i(StoreValueWallet.TAG, "updateCardInfo: ${data.size} $cardInfoFileNumber $accessKey")
            performInitialOperations(appID)
            dataFileManager.writeDataToStandardFile(
                standardFileNo = cardInfoFileNumber,
                content = data,
                accessKey = accessKey
            )

            Log.i(StoreValueWallet.TAG, "Success creating and writing to Standard Data File")
            true
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, "updateCardInfo: ${data.size} ${e.message.toString()}")
            // Handle error
            false
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