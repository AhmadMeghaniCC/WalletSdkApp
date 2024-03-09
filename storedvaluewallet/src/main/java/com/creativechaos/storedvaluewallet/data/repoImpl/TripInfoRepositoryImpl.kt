package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.TripInfoRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.nxp.nfclib.desfire.IDESFireEV1

class TripInfoRepositoryImpl(private val appID: ByteArray, desFireEV1: IDESFireEV1) :
    TripInfoRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)
    override fun getTripInfoLog(
        noOfRecords: Int,
        recordSize: Int,
        tripInfoFileNumber: Int,
        accessKey: Int
    ): MutableList<String> {
        return try {
            performInitialOperations()
            dataFileManager.readCyclicRecordFile(noOfRecords, tripInfoFileNumber, recordSize,accessKey)
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            mutableListOf()
        }
    }

    override fun addNewTripInfo(
        recordContent: ByteArray,
        tripInfoFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations()
            dataFileManager.writeCyclicRecordToFile(recordContent, tripInfoFileNumber, accessKey)
            Log.i(StoreValueWallet.TAG, "Success creating Linear Record File and writing record")
            true
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            false
        }
    }

    private fun performInitialOperations() {
        cardManager.performInitialChecks()

        if (!cardManager.checkApplicationExists(appID))
            cardManager.createApplication(appID)

        cardManager.selectApplicationByAID(appID)
        cardManager.authenticateToApplication()
    }

}