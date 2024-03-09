package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.LedgerRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.nxp.nfclib.desfire.IDESFireEV1

class LedgerRepositoryImpl(private val appID: ByteArray, desFireEV1: IDESFireEV1) :
    LedgerRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)

    override fun getMiniStatement(
        noOfRecords: Int,
        fileNumber: Int,
        accessKey: Int,
        recordSize: Int
    ): MutableList<String> {
        return try {
            performInitialOperations()
            dataFileManager.readCyclicRecordFile(noOfRecords, fileNumber, recordSize, accessKey)
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
            // Handle error
            mutableListOf()
        }
    }

    override fun addLedgerEntry(
        recordContent: ByteArray,
        fileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations()
            dataFileManager.writeCyclicRecordToFile(recordContent, fileNumber, accessKey)
            Log.i(TAG, "Success creating Linear Record File and writing record")
            true
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
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