package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.SalesLogRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.nxp.nfclib.desfire.IDESFireEV1

class SalesLogRepositoryImpl(private val appID: ByteArray, desFireEV1: IDESFireEV1) :
    SalesLogRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)


    private fun performInitialOperations() {
        cardManager.performInitialChecks()

        if (!cardManager.checkApplicationExists(appID))
            cardManager.createApplication(appID)

        cardManager.selectApplicationByAID(appID)
        cardManager.authenticateToApplication()
    }

    override fun getSalesLog(
        noOfRecords: Int,
        salesLogFileNumber: Int,
        recordSize: Int,
        accessKey: Int
    ): MutableList<String> {
        return try {
            performInitialOperations()
            dataFileManager.readCyclicRecordFile(
                noOfRecords = noOfRecords,
                recordSize = recordSize,
                accessKey = accessKey,
                cyclicRecordFileNo = salesLogFileNumber
            )
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            mutableListOf()
        }
    }

    override fun newSale(
        recordContent: ByteArray,
        salesLogFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations()
            dataFileManager.writeCyclicRecordToFile(
                recordContent,
                salesLogFileNumber,
                accessKey
            )
            Log.i(
                StoreValueWallet.TAG,
                "newSale Success creating Linear Record File and writing record"
            )
            true
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            false
        }
    }
}