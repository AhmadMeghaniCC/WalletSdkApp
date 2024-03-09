package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.TransactionLogRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet
import com.nxp.nfclib.desfire.IDESFireEV1

class TransactionLogRepositoryImpl(private val appID: ByteArray, desFireEV1: IDESFireEV1) :
    TransactionLogRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)


    private fun performInitialOperations() {
        cardManager.performInitialChecks()

        if (!cardManager.checkApplicationExists(appID))
            cardManager.createApplication(appID)

        cardManager.selectApplicationByAID(appID)
        cardManager.authenticateToApplication()
    }

    override fun newTransactionLog(
        recordContent: ByteArray,
        transactionLogFileNumber: Int,
        accessKey: Int
    ): Boolean {
        return try {
            performInitialOperations()
            dataFileManager.writeCyclicRecordToFile(
                recordContent,
                transactionLogFileNumber,
                accessKey
            )
            Log.i(StoreValueWallet.TAG, "Success creating Linear Record File and writing record")
            true
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            false
        }
    }

    override fun getTransactionLogs(
        noOfRecords: Int,
        transactionLogFileNumber: Int,
        accessKey: Int,
        recordSize: Int
    ): MutableList<String> {
        return try {
            performInitialOperations()
            dataFileManager.readCyclicRecordFile(
                noOfRecords,
                transactionLogFileNumber,
                accessKey = accessKey, recordSize = recordSize
            )
        } catch (e: Exception) {
            Log.i(StoreValueWallet.TAG, e.message!!)
            // Handle error
            mutableListOf()
        }
    }
}