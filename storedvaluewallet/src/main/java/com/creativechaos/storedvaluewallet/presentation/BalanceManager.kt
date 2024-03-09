package com.creativechaos.storedvaluewallet.presentation

import android.util.Log
import com.creativechaos.storedvaluewallet.Utils
import com.creativechaos.storedvaluewallet.Utils.Companion.getAccessInfo
import com.creativechaos.storedvaluewallet.Utils.Companion.getRecordSize
import com.creativechaos.storedvaluewallet.data.model.BalanceInfoModel
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionDetail
import com.creativechaos.storedvaluewallet.data.model.TransactionType
import com.creativechaos.storedvaluewallet.data.model.TripLogType
import com.creativechaos.storedvaluewallet.data.remote.ApiService
import com.creativechaos.storedvaluewallet.data.remote.TransactionRequest
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.TripSalesMetadata
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetail
import com.creativechaos.storedvaluewallet.data.repoImpl.AccountRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.BalanceInfoRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.BalanceRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.LedgerRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.SalesLogRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.TransactionLogRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.TripInfoRepositoryImpl
import com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases.GetAccountInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceInfoUseCases.GetBalanceInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceInfoUseCases.UpdateBalanceInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases.CreditAccountUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases.DebitAccountUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceUseCases.GetBalanceUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases.CreditLedgerUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases.DebitLedgerUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.ledgerUseCases.GetMiniStatementUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.salesLogsUseCases.AddNewSaleLogUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.salesLogsUseCases.GetSaleLogsUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.transactionLogUseCases.AddNewTransactionLogUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.transactionLogUseCases.GetTransactionLogsUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.tripInfoLogUseCases.AddTripInfoLogUseCase
import com.google.gson.Gson
import com.nxp.nfclib.desfire.IDESFireEV1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BalanceManager(
    private val uid: String,
    private val desFireInterface: IDESFireEV1,
    private val wallet: WalletDetail,
) {
//    private val getBalanceUseCase = GetBalanceUseCase(balanceRepository)
//    private val creditAccountUseCase = CreditAccountUseCase(balanceRepository)
//    private val debitAccountUseCase = DebitAccountUseCase(balanceRepository)


    suspend fun getAccountBalance(): Int = withContext(Dispatchers.IO) {
        val balanceAccessInfo = getAccessInfo(wallet, "Balance") ?: return@withContext 0
        return@withContext GetBalanceUseCase(
            BalanceRepositoryImpl(
                wallet.appId.toByteArray(), desFireInterface
            )
        ).execute(
            balanceFileNumber = balanceAccessInfo.fileNumber, balanceAccessInfo.accessKey
        )
    }

    private fun generateTransactionRequest(
        uid: String,
        walletId: Int,
        txnDateTime: Long,
        customerAccountNumber: String,
        merchantAccountId: Long,
        value: Int,
        currentBalance: Int,
        cardNewBalance: Long,
        versionNo: Long,
        refId: String?,
        reversalType: TransactionType?
    ): TransactionRequest {

        return TransactionRequest(
            uid = uid,
            walletId = walletId.toLong(),
            coRelationId = "$customerAccountNumber-$txnDateTime",
            merchantAccountNumber = merchantAccountId,
            customerAccountNumber = customerAccountNumber,
            amount = value.toLong(),
            cardData = "df",
            cardCurrentBalance = currentBalance.toLong(),
            cardNewBalance = cardNewBalance,
            transactionInitiatedOn = Utils.getFormattedDateTime(txnDateTime),
            transactionMode = "card_present",
            versionNo = versionNo,
            refId = refId,
            transactionType = reversalType?.toJsonStringRepresentation()
        )
    }

    private fun getNewBalance(
        value: Int, currentBalance: Int, txnType: TransactionType
    ): Long {
        return when (txnType) {
            TransactionType.CashIn -> currentBalance + value
            TransactionType.CashOut -> currentBalance - value
            TransactionType.Adjustment -> currentBalance + value
            TransactionType.Sale -> currentBalance - value
            TransactionType.BusSales -> currentBalance - value
            TransactionType.ReversalCashIn -> currentBalance - value
            TransactionType.ReversalCashOut -> currentBalance + value
            TransactionType.ReversalAdjustment -> currentBalance - value
            /*TransactionType.Reversal -> {
                val newBalance = currentBalance - value
                if (reversalType == TransactionType.CashIn || reversalType == TransactionType.Adjustment) newBalance else currentBalance + value // Handle nullability of reversalType
            }*/
        }.toLong()
    }

    suspend fun performTransaction(
        walletId: Int,
        value: Int,
        txnType: TransactionType,
        refId: String? = null,
        reversalType: TransactionType? = null
    ): Pair<Boolean, String> {

        val balanceAccessInfo = getAccessInfo(wallet, "Balance")
        val accountAccessInfo = getAccessInfo(wallet, "Account Info")
        val balanceInfoAccessInfo = getAccessInfo(wallet, "Balance Info")
        val ledgerAccessInfo = getAccessInfo(wallet, "Ledger")
        val transactionAccessInfo = getAccessInfo(wallet, "Transaction Log")

        if (accountAccessInfo != null && balanceAccessInfo != null && balanceInfoAccessInfo != null && ledgerAccessInfo != null && transactionAccessInfo != null) {
            val txnDateTime = System.currentTimeMillis() / 1000

            val currentBalance = GetBalanceUseCase(
                BalanceRepositoryImpl(
                    wallet.appId.toByteArray(), desFireInterface
                )
            ).execute(balanceAccessInfo.fileNumber, balanceAccessInfo.accessKey)

            val accountInfo = GetAccountInfoUseCase(
                AccountRepositoryImpl(
                    wallet.appId.toByteArray(),
                    desFireInterface
                )
            ).execute(accountAccessInfo.fileNumber, accountAccessInfo.accessKey)
                ?: return Pair(
                    false, "Don't have access to Account Info"
                )

            val newBalance = getNewBalance(
                value = value,
                currentBalance = currentBalance,
                txnType = txnType
            )

            if (newBalance < 0) return Pair(
                false, "Not Enough Balance to perform this transaction"
            )

            val currentBalanceInfo = GetBalanceInfoUseCase(
                BalanceInfoRepositoryImpl(
                    wallet.appId.toByteArray(), desFireInterface
                )
            ).execute(
                balanceInfoAccessInfo.fileNumber, balanceInfoAccessInfo.accessKey
            ) ?: BalanceInfoModel(
                lastModifiedAt = txnDateTime, versionNo = 0, syncStatus = 0, lastSyncedOn = 0
            )

            currentBalanceInfo.versionNo = currentBalanceInfo.versionNo + 1
            val request = generateTransactionRequest(
                uid,
                walletId,
                txnDateTime,
                accountInfo.phoneNumber,
                wallet.merchantAccountNo.toLong(),
                value,
                currentBalance,
                cardNewBalance = newBalance,
                currentBalanceInfo.versionNo,
                refId,
                reversalType,
            )

            val apiResponse = when (txnType) {
                TransactionType.CashIn -> ApiService.create().cashIn(request)
                TransactionType.CashOut -> ApiService.create().cashOut(request)
                TransactionType.Adjustment -> ApiService.create().adjustment(request)
                TransactionType.ReversalCashOut, TransactionType.ReversalCashIn, TransactionType.ReversalAdjustment -> ApiService.create()
                    .reversal(request)

                else -> {
                    null
                }
            } ?: return Pair(false, "Could not send request to server!")

            if (apiResponse.status != 1.toLong()) {
                return Pair(false, apiResponse.message)
            }

            val ledgerOperationStatus =
                if (txnType == TransactionType.CashIn || txnType == TransactionType.Adjustment || txnType == TransactionType.ReversalCashOut) {
                    CreditAccountUseCase(
                        BalanceRepositoryImpl(
                            wallet.appId.toByteArray(), desFireInterface
                        )
                    ).execute(
                        value, balanceAccessInfo.fileNumber, balanceAccessInfo.accessKey
                    ) && creditLedger(
                        value,
                        txnDateTime,
                        currentBalanceInfo.versionNo,
                        ledgerAccessInfo.fileNumber,
                        ledgerAccessInfo.accessKey
                    )
                } else {
                    DebitAccountUseCase(
                        BalanceRepositoryImpl(
                            wallet.appId.toByteArray(), desFireInterface
                        )
                    ).execute(
                        value, balanceAccessInfo.fileNumber, balanceAccessInfo.accessKey
                    ) && debitLedger(
                        value,
                        txnDateTime,
                        currentBalanceInfo.versionNo,
                        ledgerAccessInfo.fileNumber,
                        ledgerAccessInfo.accessKey
                    )

                }

            if (!ledgerOperationStatus) {
                return Pair(false, "Error in ledger operation")
            }

            val operationStatus = updateBalanceInfo(
                balanceInfoModel = currentBalanceInfo,
                balanceInfoFileNumber = balanceInfoAccessInfo.fileNumber,
                balanceInfoAccessKey = balanceInfoAccessInfo.accessKey
            ) && addEntryInTransactionLog(
                amount = value,
                transactionLogFileNumber = transactionAccessInfo.fileNumber,
                transactionLogAccessKey = transactionAccessInfo.accessKey,
                coRelationId = "${accountInfo.phoneNumber}-$txnDateTime",
                merchantAccountId = wallet.merchantAccountNo.toLong(),
                txnDateTime = txnDateTime,
                transactionType = txnType
            )

            if (!operationStatus) {
                return Pair(false, "Error in transaction log operation")
            }

            return Pair(true, "Transaction Successful")

        } else {
            return Pair(false, "You do not have access to the files!")
        }

    }

    suspend fun getCurrentBalanceVersion(): Long = withContext(Dispatchers.IO) {
        val balanceInfoAccessInfo = getAccessInfo(wallet, "Balance Info")
        return@withContext if (balanceInfoAccessInfo != null) {
            val currentBalanceInfo = GetBalanceInfoUseCase(
                BalanceInfoRepositoryImpl(
                    wallet.appId.toByteArray(), desFireInterface
                )
            ).execute(
                balanceInfoAccessInfo.fileNumber, balanceInfoAccessInfo.accessKey
            )
            currentBalanceInfo?.versionNo ?: -1
        } else {
            -1
        }
    }


    suspend fun performSales(
        value: Int,
        txnType: TransactionType,
        refId: String,
        txnTime: Long,
        tripSalesMetadata: TripSalesMetadata? = null
    ): Pair<Boolean, String> = withContext(Dispatchers.IO){

        val balanceAccessInfo = getAccessInfo(wallet, "Balance")
        val balanceInfoAccessInfo = getAccessInfo(wallet, "Balance Info")
        val accountAccessInfo = getAccessInfo(wallet, "Account Info")
        val ledgerAccessInfo = getAccessInfo(wallet, "Ledger")
        val salesAccessInfo = getAccessInfo(wallet, "Sales Info Log")


        if (accountAccessInfo != null && balanceAccessInfo != null && balanceInfoAccessInfo != null && ledgerAccessInfo != null && salesAccessInfo != null) {

            val accountInfo = GetAccountInfoUseCase(
                AccountRepositoryImpl(
                    wallet.appId.toByteArray(),
                    desFireInterface
                )
            ).execute(accountAccessInfo.fileNumber, accountAccessInfo.accessKey)
                ?: return@withContext Pair(
                    false, "Don't have access to Account Info"
                )

            val txnDateTime = txnTime / 1000
            val currentBalance = GetBalanceUseCase(
                BalanceRepositoryImpl(
                    wallet.appId.toByteArray(),
                    desFireInterface
                )
            ).execute(
                balanceAccessInfo.fileNumber,
                balanceAccessInfo.accessKey
            )
            val newBalance = currentBalance - value
            if (newBalance < 0) return@withContext Pair(
                false,
                "Not Enough Balance to perform this transaction"
            )

            val currentBalanceInfo =
                GetBalanceInfoUseCase(
                    BalanceInfoRepositoryImpl(
                        wallet.appId.toByteArray(),
                        desFireInterface
                    )
                ).execute(
                    balanceInfoAccessInfo.fileNumber, balanceInfoAccessInfo.accessKey
                ) ?: BalanceInfoModel(
                    lastModifiedAt = txnDateTime, versionNo = 0, syncStatus = 0, lastSyncedOn = 0
                )

            currentBalanceInfo.versionNo = currentBalanceInfo.versionNo + 1

            val ledgerOperationStatus = DebitAccountUseCase(
                BalanceRepositoryImpl(
                    wallet.appId.toByteArray(),
                    desFireInterface
                )
            ).execute(
                value, balanceAccessInfo.fileNumber, balanceAccessInfo.accessKey
            ) && debitLedger(
                value,
                txnDateTime,
                currentBalanceInfo.versionNo,
                ledgerAccessInfo.fileNumber,
                ledgerAccessInfo.accessKey
            )

            if (!ledgerOperationStatus) {
                return@withContext Pair(false, "Error in ledger operation")
            }

            val operationStatus = updateBalanceInfo(
                balanceInfoModel = currentBalanceInfo,
                balanceInfoFileNumber = balanceInfoAccessInfo.fileNumber,
                balanceInfoAccessKey = balanceInfoAccessInfo.accessKey
            ) && addEntryInSalesLog(
                salesLogFileNumber = salesAccessInfo.fileNumber,
                salesLogAccessKey = salesAccessInfo.accessKey,
                amount = value.toLong(),
                coRelationId = "${accountInfo.phoneNumber}-$txnDateTime",
                transactionType = txnType,
                merchantAccountId = wallet.merchantAccountNo.toLong(),
                txnDateTime = txnDateTime,
                referenceId = refId
            )

            if (txnType == TransactionType.BusSales && tripSalesMetadata != null) {
                val operationSuccessful = addEntryInTripLog(
                    tripId = tripSalesMetadata.tripId,
                    waybillNo = tripSalesMetadata.waybillNo,
                    txnDateTime = txnDateTime,
                    amount = value,
                    stationId = tripSalesMetadata.stationId,
                    tripLogType = tripSalesMetadata.tripLogType
                )
                if (!operationSuccessful)
                    return@withContext Pair(false, "Error in trip log operation")
            }

            if (!operationStatus) {
                return@withContext Pair(false, "Error in sales log operation")
            }

            return@withContext Pair(true, "Transaction Successful")

        } else {
            return@withContext Pair(false, "You do not have access to files!")
        }

    }


    /*suspend fun creditAccount(
        uid: String,
        walletId: Int,
        value: Int,
        merchantAccountId: Long,
        customerAccountNumber: Long,
        balanceAccessInfo.fileNumber: Int,
        balanceInfoFileNumber: Int,
        balanceInfoAccessKey: Int,
        transactionLogFileNumber: Int,
        transactionLogAccessKey: Int,
        ledgerFileNumber: Int,
        ledgerAccessKey: Int
    ): Pair<Boolean, String> {

        val currentBalance = getBalanceUseCase.execute(balanceAccessInfo.fileNumber, balanceAccessKey)
        val txnDateTime = System.currentTimeMillis()

        val apiResponse = ApiService.create().cashIn(
            TransactionRequest(
                uid = uid,
                walletId = walletId.toLong(),
                coRelationId = "$txnDateTime-$customerAccountNumber",
                merchantAccountNumber = merchantAccountId,
                customerAccountNumber = customerAccountNumber,
                amount = value.toLong(),
                cardData = "df",
                cardCurrentBalance = currentBalance.toLong(),
                cardNewBalance = (currentBalance + value).toLong(),
                transactionInitiatedOn = Utils.getFormattedDateTime(txnDateTime / 1000),
                transactionMode = "card_present"
            )
        )

        if (apiResponse != null) {
            if (apiResponse.status == 1.toLong()) {
                val operationStatus = updateBalanceInfo(
                    lastModifiedAt = txnDateTime / 1000,
                    balanceInfoFileNumber = balanceInfoAccessInfo.fileNumber,
                    balanceInfoAccessKey = balanceInfoAccessInfo.accessKey
                ) && creditLedger(
                    value, ledgerFileNumber, ledgerAccessKey
                ) && addEntryInTransactionLog(
                    amount = value,
                    transactionLogFileNumber = transactionLogFileNumber,
                    transactionLogAccessKey = transactionLogAccessKey,
                    coRelationId = "$txnDateTime-$customerAccountNumber",
                    merchantAccountId = merchantAccountId,
                    txnDateTime = txnDateTime / 1000,
                    transactionType = TransactionType.CashOut
                ) && creditAccountUseCase.execute(
                    value, balanceAccessInfo.fileNumber, balanceAccessKey
                )

                Log.i(StoreValueWallet.TAG, "creditAccount: accountcredited $operationStatus")
                if (!operationStatus)
                    return Pair(false, "Something went wrong!")

            } else {
                return Pair(false, apiResponse.message)
            }
        } else {
            return Pair(false, "Could not send request to server!")
        }

        return Pair(true, "Transaction Successful")
    }*/

    /*suspend fun debitAccount(
        uid: String,
        walletId: Int,
        value: Int,
        merchantAccountId: Long,
        customerAccountNumber: Long,
        balanceAccessInfo.fileNumber: Int,
        balanceInfoFileNumber: Int,
        balanceInfoAccessKey: Int,
        transactionLogFileNumber: Int,
        transactionLogAccessKey: Int,
        ledgerFileNumber: Int,
        ledgerAccessKey: Int
    ): Pair<Boolean, String> {

        val currentBalance = getBalanceUseCase.execute(balanceAccessInfo.fileNumber, balanceAccessKey)
        val txnDateTime = System.currentTimeMillis()

        val apiResponse = ApiService.create().cashOut(
            TransactionRequest(
                uid = uid,
                walletId = walletId.toLong(),
                coRelationId = "$txnDateTime-$customerAccountNumber",
                merchantAccountNumber = merchantAccountId,
                customerAccountNumber = customerAccountNumber,
                amount = value.toLong(),
                cardData = "df",
                cardCurrentBalance = currentBalance.toLong(),
                cardNewBalance = (currentBalance - value).toLong(),
                transactionInitiatedOn = Utils.getFormattedDateTime(txnDateTime / 1000),
                transactionMode = "card_present"
            )
        )

        if (apiResponse != null) {
            if (apiResponse.status == 1.toLong()) {
                val operationStatus = updateBalanceInfo(
                    lastModifiedAt = txnDateTime / 1000,
                    balanceInfoFileNumber = balanceInfoAccessInfo.fileNumber,
                    balanceInfoAccessKey = balanceInfoAccessInfo.accessKey
                ) && debitLedger(
                    value, ledgerFileNumber, ledgerAccessKey
                ) && addEntryInTransactionLog(
                    amount = value,
                    transactionLogFileNumber = transactionLogFileNumber,
                    transactionLogAccessKey = transactionLogAccessKey,
                    coRelationId = "$txnDateTime-$customerAccountNumber",
                    merchantAccountId = merchantAccountId,
                    txnDateTime = txnDateTime / 1000,
                    transactionType = TransactionType.CashOut
                ) && debitAccountUseCase.execute(
                    abs(value), balanceAccessInfo.fileNumber, balanceAccessKey
                )

                Log.i(StoreValueWallet.TAG, "creditAccount: accountcredited $operationStatus")
                if (!operationStatus)
                    return Pair(false, "Something went wrong!")

            } else {
                return Pair(false, apiResponse.message)
            }
        } else {
            return Pair(false, "Could not send request to server!")
        }

        return Pair(true, "Transaction Successful")
    }*/

    /*fun debitAccount(
        value: Int,
        balanceAccessInfo.fileNumber: Int,
        balanceInfoFileNumber: Int,
        balanceInfoAccessKey: Int,
        transactionLogFileNumber: Int,
        transactionLogAccessKey: Int,
        ledgerFileNumber: Int,
        ledgerAccessKey: Int
    ): Boolean {
        val operationSuccessful = debitAccountUseCase.execute(
            value, balanceAccessInfo.fileNumber, balanceAccessKey
        ) && updateBalanceInfo(
            lastModifiedAt = System.currentTimeMillis() / 1000,
            balanceInfoFileNumber = balanceInfoAccessInfo.fileNumber,
            balanceInfoAccessKey = balanceInfoAccessInfo.accessKey
        ) && debitLedger(value, ledgerFileNumber, ledgerAccessKey) && addEntryInTransactionLog(
            transactionLogFileNumber = transactionLogFileNumber,
            value,
            transactionLogAccessKey,
            transactionType = TransactionType.CashOut
        )


        Log.i(StoreValueWallet.TAG, "debitAccount: accountdebited $operationSuccessful")

        return operationSuccessful
    }*/

    suspend fun getMiniStatement(
        recordSize: Int, ledgerFileNumber: Int, ledgerAccessKey: Int
    ): List<LedgerRecord> = withContext(Dispatchers.IO) {
        return@withContext GetMiniStatementUseCase(
            LedgerRepositoryImpl(
                wallet.appId.toByteArray(), desFireInterface
            )
        ).execute(
            fileNumber = ledgerFileNumber, accessKey = ledgerAccessKey, recordSize = recordSize
        )
    }

    suspend fun getTransactions(
    ): List<TransactionDetail> = withContext(Dispatchers.IO) {
        // Fetch ledger records
        val ledgerAccess =
            getAccessInfo(wallet, "Ledger") ?: return@withContext emptyList<TransactionDetail>()

        val ledgerRecords = GetMiniStatementUseCase(
            LedgerRepositoryImpl(wallet.appId.toByteArray(), desFireInterface)
        ).execute(
            fileNumber = ledgerAccess.fileNumber,
            accessKey = ledgerAccess.accessKey,
            recordSize = getRecordSize(ledgerAccess.fileNumber)
        )
        val txnAccess =
            getAccessInfo(wallet, "Transaction Log")
                ?: return@withContext emptyList<TransactionDetail>()

        // Fetch ledger records
        val salesAccess =
            getAccessInfo(wallet, "Sales Info Log")
                ?: return@withContext emptyList<TransactionDetail>()

        // Fetch transaction logs
        val txnLogs = GetTransactionLogsUseCase(
            TransactionLogRepositoryImpl(wallet.appId.toByteArray(), desFireInterface)
        ).execute(
            recordSize = getRecordSize(txnAccess.fileNumber),
            accessKey = txnAccess.accessKey,
            transactionLogFileNumber = txnAccess.fileNumber
        )

        Log.i("TAG", "getTransactions txn: ${Gson().toJson(txnLogs)}")

        // Fetch transaction logs
        val salesLogs = GetSaleLogsUseCase(
            SalesLogRepositoryImpl(wallet.appId.toByteArray(), desFireInterface)
        ).execute(
            recordSize = getRecordSize(salesAccess.fileNumber),
            accessKey = salesAccess.accessKey,
            salesLogFileNumber = salesAccess.fileNumber
        )

        Log.i("TAG", "getTransactions sale: ${Gson().toJson(salesLogs)}")

        val txnDetailList = ledgerRecords.map { ledger ->
            val matchedTxnLog = txnLogs.find { txnLog ->
                txnLog.amount == ledger.amount && txnLog.txnDateTime == ledger.dateTime
            }
            val matchedSalesLog = salesLogs.find { saleLog ->
                saleLog.amount == ledger.amount && saleLog.txnDateTime == ledger.dateTime
            }

            Log.i("TAG", "getTransactions: matchedTxnLog: ${Gson().toJson(matchedTxnLog)}")
            Log.i("TAG", "getTransactions: matchedSalesLog: ${Gson().toJson(matchedSalesLog)}")
            TransactionDetail(ledger, matchedTxnLog, matchedSalesLog)
        }
        return@withContext txnDetailList
    }


    private fun updateBalanceInfo(
        balanceInfoModel: BalanceInfoModel, balanceInfoFileNumber: Int, balanceInfoAccessKey: Int
    ): Boolean {

        return UpdateBalanceInfoUseCase(
            BalanceInfoRepositoryImpl(
                wallet.appId.toByteArray(),
                desFireInterface
            )
        ).execute(
            updatedBalanceInfo = balanceInfoModel,
            balanceInfoFileNumber = balanceInfoFileNumber,
            accessKey = balanceInfoAccessKey
        )
    }

    private fun creditLedger(
        amount: Int, dateTime: Long, versionNo: Long, fileNumber: Int, ledgerAccessKey: Int
    ): Boolean {
        return CreditLedgerUseCase(
            LedgerRepositoryImpl(
                wallet.appId.toByteArray(), desFireInterface
            )
        ).execute(amount = amount, dateTime, versionNo, fileNumber, ledgerAccessKey)
    }

    private fun debitLedger(
        amount: Int, dateTime: Long, versionNo: Long, fileNumber: Int, ledgerAccessKey: Int
    ): Boolean {
        return DebitLedgerUseCase(
            LedgerRepositoryImpl(
                wallet.appId.toByteArray(), desFireInterface
            )
        ).execute(amount = amount, dateTime, versionNo, fileNumber, ledgerAccessKey)
    }

    private fun addEntryInTransactionLog(
        transactionLogFileNumber: Int,
        amount: Int,
        transactionLogAccessKey: Int,
        coRelationId: String,
        txnDateTime: Long,
        merchantAccountId: Long,
        transactionType: TransactionType
    ): Boolean {
        return AddNewTransactionLogUseCase(
            TransactionLogRepositoryImpl(
                wallet.appId.toByteArray(), desFireInterface
            )
        ).execute(
            transactionLogFileNumber = transactionLogFileNumber,
            accessKey = transactionLogAccessKey,
            coRelationId = coRelationId,
            amount = amount.toLong(),
            transactionType = transactionType,
            merchantAccountId = merchantAccountId,
            txnDateTime = txnDateTime
        )
    }

    private fun addEntryInSalesLog(
        salesLogFileNumber: Int,
        salesLogAccessKey: Int,
        amount: Long,
        coRelationId: String,
        transactionType: TransactionType,
        merchantAccountId: Long,
        txnDateTime: Long,
        referenceId: String
    ): Boolean {
        return AddNewSaleLogUseCase(
            SalesLogRepositoryImpl(
                wallet.appId.toByteArray(),
                desFireInterface
            )
        ).execute(
            salesLogFileNumber = salesLogFileNumber,
            amount = amount,
            accessKey = salesLogAccessKey,
            coRelationId = coRelationId,
            transactionType = transactionType,
            merchantAccountId = merchantAccountId,
            txnDateTime = txnDateTime,
            referenceId = referenceId
        )
    }

    private fun addEntryInTripLog(
        tripId: String,
        waybillNo: Long,
        txnDateTime: Long,
        amount: Int,
        stationId: Long,
        tripLogType: TripLogType
    ): Boolean {
        val tripAccessInfo = getAccessInfo(wallet, "Trip Info Log") ?: return false
        return AddTripInfoLogUseCase(
            TripInfoRepositoryImpl(
                wallet.appId.toByteArray(),
                desFireInterface
            )
        ).execute(
            tripInfoFileNumber = tripAccessInfo.fileNumber,
            tripId = tripId,
            waybillNo = waybillNo,
            txnDateTime = txnDateTime,
            stationId = stationId,
            amount = amount,
            merchantAccountId = wallet.merchantAccountNo.toLong(),
            tripLogType = tripLogType,
            accessKey = tripAccessInfo.accessKey
        )
    }


}