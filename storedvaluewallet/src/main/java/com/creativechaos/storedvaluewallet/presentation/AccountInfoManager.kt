// AccountInfoManager.kt (within your library package)
package com.creativechaos.storedvaluewallet.presentation

import com.creativechaos.storedvaluewallet.Utils.Companion.getAccessInfo
import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetail
import com.creativechaos.storedvaluewallet.domain.repo.AccountRepository
import com.creativechaos.storedvaluewallet.domain.repo.CardInfoRepository
import com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases.CreateAccountUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases.GetAccountInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases.UpdateAccountInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.accountInfoUseCases.UpdateAccountStatusUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.cardInfoUseCases.GetCardInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.cardInfoUseCases.UpdateCardInfoUseCase
import com.nxp.nfclib.utils.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AccountInfoManager(
    private val uid: ByteArray,
    private val walletDetail: WalletDetail,
    accountRepository: AccountRepository,
    cardInfoRepository: CardInfoRepository,
) {
    private val createAccountUseCase = CreateAccountUseCase(accountRepository)
    private val getAccountInfoUseCase = GetAccountInfoUseCase(accountRepository)
    private val updateAccountInfoUseCase = UpdateAccountInfoUseCase(accountRepository)
    private val updateAccountStatusUseCase = UpdateAccountStatusUseCase(accountRepository)

    /*private val getBalanceUseCase = GetBalanceUseCase(balanceRepository)
    private val creditAccountUseCase = CreditAccountUseCase(balanceRepository)
    private val debitAccountUseCase = DebitAccountUseCase(balanceRepository)*/

//    private val getBalanceInfoUseCase = GetBalanceInfoUseCase(balanceInfoRepository)
//    private val updateBalanceInfoUseCase = UpdateBalanceInfoUseCase(balanceInfoRepository)

    private val getCardInfoUseCase = GetCardInfoUseCase(cardInfoRepository)
    private val updateCardInfoUseCase = UpdateCardInfoUseCase(cardInfoRepository)

    /*private val getMiniStatementUseCase = GetMiniStatementUseCase(ledgerRepository)
    private val creditLedgerUseCase = CreditLedgerUseCase(ledgerRepository)
    private val debitLedgerUseCase = DebitLedgerUseCase(ledgerRepository)

    private val getProductInfoUseCase = GetProductInfoUseCase(productInfoRepository)
    private val updateProductInfoUseCase = UpdateProductInfoUseCase(productInfoRepository)

    private val addNewSaleLogUseCase = AddNewSaleLogUseCase(salesLogRepository)
    private val getLastSaleLogUseCase = GetLastSaleLogUseCase(salesLogRepository)
    private val getSaleLogsUseCase = GetSaleLogsUseCase(salesLogRepository)

    private val addNewTransactionLogsUseCase = AddNewTransactionLogUseCase(transactionLogRepository)
    private val getTransactionLogUseCase = GetTransactionLogsUseCase(transactionLogRepository)

    private val addTripInfoUseCase = AddTripInfoLogUseCase(tripInfoRepository)
    private val getAllTripInfoLogsUseCase = GetAllTripInfoLogsUseCase(tripInfoRepository)
    private val getLastTripInfoLogsUseCase = GetLastTripInfoUseCase(tripInfoRepository)
    private val getTripInfoLogUseCase = GetTripInfoLogsUseCase(tripInfoRepository)*/

    fun createAccount(
        userAccountBuilder: UserAccount.UserAccountBuilder,
        walletId: Int,
        walletCode: String,
        cardStatus: Int,
        walletStructureVersionNumber: Int,
        cardValidityInYears: Int
    ): Boolean {
        val userAccount = userAccountBuilder.build()
        val cardAccessInfo = getAccessInfo(walletDetail, "Card Info") ?: return false
        val accountAccessInfo = getAccessInfo(walletDetail, "Account Info") ?: return false
        return updateCardInfoUseCase.execute(
            uid = Utilities.byteToHexString(uid),
            issueDate = (System.currentTimeMillis() / 1000),
            cardStatus = cardStatus,
            walletId = walletId,
            walletCode = walletCode,
            validity = cardValidityInYears,
            walletStructureVersionNumber = walletStructureVersionNumber,
            cardInfoFileNumber = cardAccessInfo.fileNumber,
            accessKey = cardAccessInfo.accessKey
        ) && createAccountUseCase.execute(
            userAccount, accountAccessInfo.fileNumber, accountAccessInfo.accessKey
        )
    }

    suspend fun getAccount(): UserAccount? = withContext(Dispatchers.IO){
        val accountInfoAccess = getAccessInfo(walletDetail, "Account Info") ?: return@withContext null
        return@withContext getAccountInfoUseCase.execute(
            accountInfoAccess.fileNumber, accountInfoAccess.accessKey
        )
    }


    fun updateAccount(account: UserAccount, accountFileNumber: Int, accessKey: Int) =
        updateAccountInfoUseCase.execute(account, accountFileNumber, accessKey)

    fun updateAccountStatus(status: Int, accountFileNumber: Int, accessKey: Int) =
        updateAccountStatusUseCase.execute(status, accountFileNumber, accessKey)

//    fun getAccountBalance(): Int = getBalanceUseCase.execute(accessKeys.readWriteAccessKey)

    /*fun updateTransactionOnCard(
        txnId: String,
        transactionType: TransactionType,
        transactionCategory: TransactionCategory,
        merchantId: Int,
        date: String,
        amount: String,
        referenceId: String,
        latitude: String,
        longitude: String,
        terminalId: String,
        txnStatus: String,
        tripId: String,
        shiftNo: String,
        busId: Int,
        startTime: String,
        endTime: String,
        syncedOn: String,
        status: String
    ): Boolean {

        val updatedBalance = getBalanceUseCase.execute(accessKeys.readWriteAccessKey)
        Log.i(TAG, "debitAccount: updatedBalance $updatedBalance")
        val serverBalance = 0
        val balanceInfoUpdated = updateBalanceInfoUseCase.execute(
            updatedBalance,
            serverBalance,
            Calendar.getInstance().time.toString(),
            accessKeys.readWriteAccessKey
        )

        Log.i(TAG, "debitAccount: balanceInfoUpdated $balanceInfoUpdated")

        val debitLedgerUpdated =
            debitLedgerUseCase.execute(updatedBalance, accessKeys.readWriteAccessKey)

        Log.i(TAG, "debitAccount: debitLedgerUpdated $debitLedgerUpdated")

        val transactionLogUpdated: Boolean = addNewTransactionLogsUseCase.execute(
            txnId,
            transactionType,
            transactionCategory,
            merchantId,
            date,
            amount,
            referenceId,
            latitude,
            longitude,
            terminalId,
            txnStatus,
            accessKeys.readWriteAccessKey
        )

        Log.i(TAG, "debitAccount: transactionLogUpdated $transactionLogUpdated")

        val tripLogUpdated: Boolean = addTripInfoUseCase.execute(
            tripId,
            shiftNo,
            terminalId,
            busId,
            startTime,
            endTime,
            syncedOn,
            status,
            accessKeys.readWriteAccessKey
        )

        Log.i(TAG, "debitAccount: tripLogUpdated $tripLogUpdated")

        return balanceInfoUpdated && debitLedgerUpdated && tripLogUpdated && transactionLogUpdated

    }*/


    /*fun updateTopUpTransactionOnCard(
        updatedBalance: Int
//        txnId: String,
//        transactionType: TransactionType,
//        transactionCategory: TransactionCategory,
//        merchantId: Int,
//        date: String,
        amount: String,
//        referenceId: String,
//        latitude: String,
//        longitude: String,
//        terminalId: String,
//        txnStatus: String,
//        tripId: String,
//        shiftNo: String,
//        busId: Int,
//        startTime: String,
//        endTime: String,
//        syncedOn: String,
//        status: String
    ): Boolean {
        val serverBalance = 0
        val balanceInfoUpdated = updateBalanceInfoUseCase.execute(
            updatedBalance,
            serverBalance,
            Calendar.getInstance().time.toString(),
            accessKeys.readWriteAccessKey
        )

        Log.i(TAG, "creditAccount: balanceInfoUpdated $balanceInfoUpdated")

        val creditLedgerUpdated =
            creditLedgerUseCase.execute(updatedBalance, accessKeys.readWriteAccessKey)

        Log.i(TAG, "creditAccount: creditLedgerUpdated $creditLedgerUpdated")

        val lastSaleLog = getLastSaleLogUseCase.execute(accessKeys.readWriteAccessKey)
        var saleLogId = 1
        if(lastSaleLog.isNotEmpty())
            saleLogId = lastSaleLog[0].transactionId + 1
        val saleLogUpdated: Boolean = addNewSaleLogUseCase.execute(
            saleLogId,
            Integer.parseInt(amount),
            TransactionCategory.BusSales
        )

        val transactionLogUpdated: Boolean = addNewTransactionLogsUseCase.execute(
            txnId,
            transactionType,
            transactionCategory,
            merchantId,
            date,
            amount,
            referenceId,
            latitude,
            longitude,
            terminalId,
            txnStatus,
            accessKeys.readWriteAccessKey
        )

        Log.i(TAG, "debitAccount: transactionLogUpdated $transactionLogUpdated")




        return balanceInfoUpdated && debitLedgerUpdated && tripLogUpdated && transactionLogUpdated

    }*/


    /*fun debitAccount(
        value: Int
    ): Boolean {
        val accountDebited = debitAccountUseCase.execute(value, accessKeys.readWriteAccessKey)
        Log.i(TAG, "debitAccount: accountDebited $accountDebited")

        return accountDebited
    }

    fun creditAccount(value: Int): Boolean {
        return creditAccountUseCase.execute(value, accessKeys.readWriteAccessKey)
    }*/

    /*fun getCardUID(): String {
        return Utilities.byteToHexString(uid)
    }*/

}
