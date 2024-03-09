package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.domain.repo.BalanceRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.nxp.nfclib.desfire.IDESFireEV1
import kotlin.math.abs

class BalanceRepositoryImpl(private val AID: ByteArray, private val desFireEV1: IDESFireEV1) :
    BalanceRepository {
    private var cardManager: CardManager = CardManager(desFireEV1)
    private var dataFileManager: DataFileManager = DataFileManager(desFireEV1, cardManager)

    override fun getBalance(balanceFileNumber: Int, accessKey: Int): Int {
        return try {
            dataFileManager.readValueFile(balanceFileNumber, accessKey)
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
            0
        }
    }

    override fun creditAccount(balanceFileNumber: Int, amount: Int, accessKey: Int): Boolean {
        return try {
            performInitialOperations(AID)
            dataFileManager.creditValue(amount, balanceFileNumber, accessKey)

            Log.i(TAG, "Success creating Value File and crediting value")
            true
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
            // Handle error
            false
        }
    }

    override fun debitAccount(balanceFileNumber: Int, amount: Int, accessKey: Int): Boolean {
        return try {
            performInitialOperations(AID)
            dataFileManager.debitValue(abs(amount), balanceFileNumber, accessKey)

            Log.i(TAG, "Success creating Value File and debiting value")
            true
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
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