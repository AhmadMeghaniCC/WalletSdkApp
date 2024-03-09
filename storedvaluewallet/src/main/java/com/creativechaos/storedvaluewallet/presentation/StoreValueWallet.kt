package com.creativechaos.storedvaluewallet.presentation

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.creativechaos.storedvaluewallet.Utils.Companion.getAccessInfo
import com.creativechaos.storedvaluewallet.data.remote.ApiService
import com.creativechaos.storedvaluewallet.data.remote.TransactionDetailResponse
import com.creativechaos.storedvaluewallet.data.remote.TransactionListingResponse
import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.BASE_URL
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.FileStructure
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.GetBalanceResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.Wallet
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetail
import com.creativechaos.storedvaluewallet.data.repoImpl.AccountRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.BalanceInfoRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.CardInfoRepositoryImpl
import com.creativechaos.storedvaluewallet.data.repoImpl.CardRepositoryImpl
import com.creativechaos.storedvaluewallet.domain.usecases.CheckIfCardIsInitialised
import com.creativechaos.storedvaluewallet.domain.usecases.InitialiseCardUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.balanceInfoUseCases.GetBalanceInfoUseCase
import com.creativechaos.storedvaluewallet.domain.usecases.cardInfoUseCases.GetCardInfoUseCase
import com.nxp.nfclib.CardType
import com.nxp.nfclib.NxpNfcLib
import com.nxp.nfclib.defaultimpl.KeyData
import com.nxp.nfclib.desfire.DESFireFactory
import com.nxp.nfclib.desfire.IDESFireEV1
import com.nxp.nfclib.exceptions.NxpNfcLibException
import com.nxp.nfclib.interfaces.IKeyData
import com.nxp.nfclib.utils.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.Key
import javax.crypto.spec.SecretKeySpec


class StoreValueWallet private constructor(
) {
    private var isInitialized = false
    private lateinit var accountManager: AccountInfoManager
    private var startTime: Long = 0

    private lateinit var desFireInterface: IDESFireEV1
    private lateinit var nfcLibInstance: NxpNfcLib

    var walletDetailList: List<WalletDetail> = emptyList()
    var walletStructureList: List<Wallet> = emptyList()

    private suspend fun postLoginOperations(): List<WalletDetail>? {
        val walletDetailResponse = ApiService.create().getWalletDetail()
        walletDetailResponse?.let { walletDetail ->
            if (walletDetail.status == 1) {
                walletDetailList = walletDetail.data
                val adminWallet = walletDetail.data.find { it.role == "administration" }
                adminWallet?.let {
                    val walletStructureResponse =
                        ApiService.create().getWalletStructure(adminWallet.walletId)
                    walletStructureResponse?.let {
                        if (it.status == 1)
                            walletStructureList = it.data
                    }
                }

                return walletDetail.data
            }
        }

        return null
    }

    companion object {
        const val licenseKey = "01cb198b05248a705dd378093139a43f"
        const val offlineLicenseKey =
            "ATJzmhfc5UzOIYSse4NriSZQPCtAMCCcNIGvXPRUE+vOSjXlwjUvXmzBFmf5PYORC3oQEvzyaWRlMhmVI6BwIIEOKXxCciDMizo0JBtyoguy5jo3x3hZUk2vY2pPtcE5oO2CgDi45JziWpb1ILWpHV58cNhMRE7HJuLysAUz1OWJ1tC2J767OT/gItzLWfHfhgNNyqpunZIgx0K3T0c2nAAsOFQnyQQLGefsl1LVymE29OSobFgOOSJBoVCkA1hPiYJQq6udu+8OA3aKDZobXfqfVx6xxvmpD40TLwmRZVT+vpqh1wndbCq7oktCUrf4Sc1E4JRSSn+UKW/a8x9bCQ=="

        //            "MuFeJ+UAPLvlo7fnKplC1drb5E0ZRwnkXlLcZgDHS0s95l3WINyWRFYKs6NfB2Q0TEEbC74iloVtsskNs5jNHNduHgdoZDPQusKMv9u2NK0luatwGprrKrJj6HZh8F9OhCJkLT5beQDWkFfsFq+X3wmz6DX1KE4A3c019m5YwMYTrTog2WQbmlwsCZG/d0OYC6CgbM4Nzk8T2YPjtLTYWCO/wn7QN4KZViML03o29w8roNM+FxtECD7EL3TwjEaE/mcouNDsHPUt+Qu74Gs6JIEDcmqAYpwGCCSfxXCDEUEIV/1DG43wWDnO+Wg8ZxCIJgdUecwHHQZY/N2tIcxoyQ=="
        var objKEY_2TDEA: IKeyData? = null
        val KEY_2TDEA = Utilities.stringToBytes("00000000000000000000000000000000")
        const val timeOut = 2000L
        const val TAG = "SVW Library"

        private var instance: StoreValueWallet? = null

        fun getInstance(): StoreValueWallet {
            return instance ?: synchronized(this) {
                instance ?: StoreValueWallet().also { instance = it }
            }
        }
    }

    init {
        nfcLibInstance = NxpNfcLib.getInstance()
        initializeKeys()
    }

    fun initialize(
        walletName: String
    ): Boolean {
        startTime = System.currentTimeMillis()
        logStart("initialize")
        if (isCardInitialized(walletName)) {
            return true
        }

        connectWithNFC()

        isInitialized =
            createStoredValueWallet(
                desFireInterface,
                walletName
            )


        logEnd("initialize")

        return isInitialized
    }

    fun getWalletNamesList(): List<String> {
        if (walletDetailList.isNotEmpty()) {
            return walletDetailList.map {
                it.walletName
            }
        }
        return emptyList()
    }

    fun getWalletByName(walletName: String): WalletDetail? {
        return walletDetailList.find { it.walletName == walletName }

    }


    fun getWalletIdByName(walletName: String): Int {
        return walletDetailList.find { it.walletName == walletName }?.walletId ?: 0
    }

    fun isCardInitialized(
        walletName: String
    ): Boolean {
        var isCardInitialized = false
        val selectedWallet = walletDetailList.find { it.walletName == walletName }
        return if (selectedWallet != null) {
            try {
                if (connectWithNFC()) {
                    val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
                    val checkIfCardIsInitialised =
                        CheckIfCardIsInitialised(
                            cardRepositoryImpl,
                            selectedWallet.appId.toByteArray(),
                            selectedWallet.rights.map { it.fileNumber }.distinct()
                        )
                    isCardInitialized = checkIfCardIsInitialised.execute()
                }

                isCardInitialized;
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else
            false
    }

    fun getAllApplications(): List<String> {
        return try {
            if (connectWithNFC()) {
                val appList: ArrayList<String> = ArrayList()
                CardManager(desFireInterface).performInitialChecks()
                val applications = desFireInterface.applicationIDs
                for (appId in applications) {
                    val ids: ByteArray = Utilities.intToBytes(appId, 3)
                    Log.i(TAG, "ids: $ids")
                    val str: String = Utilities.dumpHexAscii(ids)
                    Log.i(TAG, "str: $str")
                    appList.add(str)

                }
                Log.i(TAG, "getAllApplications: ${applications}")
                appList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    fun isReaderConnected(): Boolean {
        return try {
            desFireInterface.reader.isConnected
        } catch (e: Exception) {
            false
        }
    }

    private fun connectWithNFC(): Boolean {
        return try {
            desFireInterface = DESFireFactory.getInstance().getDESFire(nfcLibInstance.customModules)
            if (!isReaderConnected()) {
                desFireInterface.reader.connect()
                desFireInterface.reader.timeout = timeOut
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun logAllCardFiles(accessKey: Int) {
        val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
//        cardRepositoryImpl.logAllCardFiles(accessKey)
    }

    /*fun getAllCardFiles(accessKey: Int): List<Pair<Int, String>> {
        val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
        return cardRepositoryImpl.getAllCardFiles(accessKey)
    }*/

    fun isAccountLinked(
        walletName: String
    ): Boolean {
        val selectedWallet = walletDetailList.find { it.walletName == walletName }
        return if (selectedWallet != null) {
            val cardInfoAccess = getAccessInfo(selectedWallet, "Card Info") ?: return false
            val cardInfo = GetCardInfoUseCase(
                CardInfoRepositoryImpl(
                    selectedWallet.appId.toByteArray(),
                    desFireInterface
                )
            ).execute(cardInfoAccess.fileNumber, cardInfoAccess.accessKey)
            cardInfo?.cardStatus == 10

        } else false
    }

    suspend fun getCardBalanceVersion(
        walletName: String
    ) = withContext(Dispatchers.IO) {
        val selectedWallet = getWalletByName(walletName) ?: return@withContext 0L
        return@withContext try {
            val useCase =
                GetBalanceInfoUseCase(
                    BalanceInfoRepositoryImpl(
                        selectedWallet.appId.toByteArray(),
                        desFireInterface
                    )
                )
            val balanceInfoAccess =
                getAccessInfo(selectedWallet, "Balance Info") ?: return@withContext 0L
            val result =
                useCase.execute(
                    balanceInfoAccess.fileNumber,
                    balanceInfoAccess.accessKey
                )
            result?.versionNo ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }


    suspend fun getCardStatus(
        walletName: String
    ): Int = withContext(Dispatchers.IO) {
        val selectedWallet = getWalletByName(walletName) ?: return@withContext 0
        val cardInfoAccess = getAccessInfo(selectedWallet, "Card Info") ?: return@withContext 0

        return@withContext GetCardInfoUseCase(
            CardInfoRepositoryImpl(
                selectedWallet.appId.toByteArray(),
                desFireInterface
            )
        ).execute(
            cardInfoAccess.fileNumber,
            cardInfoAccess.accessKey
        )?.cardStatus ?: 0
    }

    fun getAllCardFiles(
        walletName: String
    ): List<Pair<FileStructure, String>> {
        val selectedWalletStructure =
            walletStructureList.find { it.walletName == walletName } ?: return emptyList()

        val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
        return cardRepositoryImpl.getAllCardFiles(
            selectedWalletStructure.appId.toByteArray(),
            selectedWalletStructure.fileStructure
        )
    }

    fun getCardFile(
        appId: ByteArray,
        cardFileStructure: FileStructure
    ): Pair<FileStructure, String> {
        val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
        return cardRepositoryImpl.getCardFile(appId, cardFileStructure)
    }

    private fun createStoredValueWallet(
        desFireInterface: IDESFireEV1,
        walletName: String
    ): Boolean {
        return try {
            startTime = System.currentTimeMillis()
            logStart("createStoredValueWallet")

            val selectedWallet = walletStructureList.find { it.walletName == walletName }
            if (selectedWallet != null) {
                val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
                val initialiseCardUseCase =
                    InitialiseCardUseCase(
                        cardRepositoryImpl,
                        selectedWallet.fileStructure,
                        selectedWallet.appId.toByteArray()
                    )
                initialiseCardUseCase.execute()
            } else
                false

        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            logEnd("createStoredValueWallet")
        }
    }


    fun enableForegroundService() {
        try {
            nfcLibInstance.startForeGroundDispatch()
        } catch (_: Exception) {
        }
    }

    fun disableForegroundService() {
        try {
            nfcLibInstance.stopForeGroundDispatch()
        } catch (_: Exception) {
        }
    }

    suspend fun getAccountManager(
        walletName: String
    ): AccountInfoManager? = withContext(Dispatchers.IO){
        startTime = System.currentTimeMillis()
        logStart("getAccountManager")
        val selectedWallet = walletDetailList.find { it.walletName == walletName }
        return@withContext if (selectedWallet != null) {
            isInitialized = isCardInitialized(walletName)
            if (!isInitialized) {
                return@withContext null
            }
            if (!::accountManager.isInitialized) {
                accountManager =
                    AccountInfoManager(
                        desFireInterface.uid,
                        accountRepository = AccountRepositoryImpl(
                            selectedWallet.appId.toByteArray(),
                            desFireInterface
                        ),
                        cardInfoRepository = CardInfoRepositoryImpl(
                            selectedWallet.appId.toByteArray(),
                            desFireInterface
                        ),
                        walletDetail = selectedWallet
                    )
            }

            logEnd("getAccountManager")
            accountManager
        } else null
    }

    fun getCardUid(): String {
        connectWithNFC()
        return Utilities.byteToHexString(desFireInterface.uid)
    }


    suspend fun formatCard(walletName: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext if (connectWithNFC() && ApiService.create()
                .resetCard(getCardUid())
        ) {
            val selectedWallet = getWalletByName(walletName) ?: return@withContext false
            try {
                val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
                return@withContext cardRepositoryImpl.formatCard(selectedWallet.appId.toByteArray())
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        } else
            false
    }

    suspend fun getCardStatusFromServer(walletName: String): Int = withContext(Dispatchers.IO) {
        try {
            val cardStatusResponse =
                ApiService.create()
                    .getCardStatus(getCardUid(), getWalletIdByName(walletName).toString())
            cardStatusResponse?.cardData?.card?.status
                ?: -2 // Return -2 if cardStatusResponse or card is null
        } catch (e: Exception) {
            e.printStackTrace()
            -2 // Return -3 for IO errors
        } catch (e: Exception) {
            e.printStackTrace()
            -2 // Return -4 for other exceptions
        }
    }


    fun doesMasterAppExist(masterAppId: ByteArray): Boolean {
        return if (connectWithNFC()) {
            val cardRepositoryImpl = CardRepositoryImpl(desFireInterface)
            cardRepositoryImpl.doesMasterAppExist(masterAppId)
        } else false
    }

    suspend fun authorizeSDK(
        appActivity: Activity,
        baseUrl: String,
        clientId: String,
        clientSecret: String
    ): List<WalletDetail>? = withContext(Dispatchers.IO) {
        BASE_URL = baseUrl
        val loginResponse = ApiService.create().login(clientId, clientSecret)
        if (loginResponse != null && loginResponse.status == 1) {
            Log.i(TAG, "authorizeSDK: ${loginResponse.message}")
            val walletDetails = postLoginOperations()
            if (!walletDetails.isNullOrEmpty()) {
                initializeLibrary(appActivity)
                return@withContext walletDetails
            } else
                return@withContext null

        } else
            return@withContext null
    }

    suspend fun getTransactionDetail(
        accountNo: String,
        coRelationId: String
    ): TransactionDetailResponse? = withContext(Dispatchers.IO) {
        return@withContext ApiService.create().getTransactionDetail(accountNo, coRelationId)
    }

    suspend fun getTransactionsFromServer(
        limit: Int = 5,
        accountNo: String,
        mode: String
    ): TransactionListingResponse? = withContext(Dispatchers.IO) {
        return@withContext ApiService.create().getTransactionListing(accountNo, limit, mode)
    }

    suspend fun checkBalanceFromServer(
        accountNo: String
    ): GetBalanceResponse? = withContext(Dispatchers.IO) {
        return@withContext ApiService.create().getBalanceFromServer(accountNo)
    }


    private fun initializeLibrary(appActivity: Activity): Boolean {

        return try {
//            nfcLibInstance = NxpNfcLib.getInstance()
            nfcLibInstance.registerActivity(
                appActivity, licenseKey, offlineLicenseKey
            )
            enableForegroundService()
            true
        } catch (ex: NxpNfcLibException) {
            Log.i(TAG, ex.message.toString())
            false
        } catch (e: Exception) {
            Log.i(TAG, e.message!!)
            false
        }
    }

    private fun initializeKeys() {
        startTime = System.currentTimeMillis()
        logStart("initializeKeys")

        val keyDataObj = KeyData()
        val k: Key = SecretKeySpec(KEY_2TDEA, "DESede")
        keyDataObj.key = k
        objKEY_2TDEA = keyDataObj

        logEnd("initializeKeys")
    }

    fun isCardDesFire(intent: Intent): Boolean {
        try {
            startTime = System.currentTimeMillis()
            logStart("isCardDesFire")

            val type = nfcLibInstance.getCardType(intent) //Get the type of the card
            Log.i(TAG, type.tagName)



            return type == CardType.DESFireEV1
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            logEnd("isCardDesFire")
        }
    }

    private fun logStart(methodName: String) {
        Log.i(TAG, "Method $methodName started.")
    }

    private fun logEnd(methodName: String) {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.i(TAG, "Method $methodName finished. Duration: $duration ms")
    }

    fun getBalanceManager(
        walletName: String
    ): BalanceManager? {
        val selectedWallet = getWalletByName(walletName) ?: return null
        selectedWallet.let {
            return BalanceManager(
                getCardUid(),
                desFireInterface = desFireInterface,
                wallet = it
            )
        }
    }

    /*fun topUpWallet(
        appId: ByteArray,
        amount: Int,
        balanceFileNumber: Int,
        balanceAccessKey: Int,
        balanceInfoAccessKey: Int,
        ledgerAccessKey: Int,
        salesLogAccessKey: Int,
        transactionLogAccessKey: Int
    ): Boolean {
        connectWithNFC()
        val balanceManager = BalanceManager(
            balanceAccessKey = balanceAccessKey,
            balanceInfoAccessKey = balanceInfoAccessKey,
            ledgerAccessKey = ledgerAccessKey,
            salesLogAccessKey = salesLogAccessKey,
            transactionLogAccessKey = transactionLogAccessKey,
            balanceRepository = BalanceRepositoryImpl(appId, desFireInterface),
            balanceInfoRepository = BalanceInfoRepositoryImpl(appId, desFireInterface),
            ledgerRepository = LedgerRepositoryImpl(appId, desFireInterface),
            salesLogRepository = SalesLogRepositoryImpl(appId, desFireInterface),
            transactionLogRepository = TransactionLogRepositoryImpl(appId, desFireInterface)
        )

        balanceManager.creditAccount()
        val balanceRepository = BalanceRepositoryImpl(appId, desFireInterface)
        val creditAccountUseCase = CreditAccountUseCase(balanceRepository)
        val isDone = creditAccountUseCase.execute(amount, balanceFileNumber, accessKey)
        if (isDone) {
            val getAccountBalance = GetBalanceUseCase(balanceRepository)
            val updatedAmount = getAccountBalance.execute(balanceFileNumber, accessKey)
            *//*Toast.makeText(appContext, "Updated Balance is $updatedAmount", Toast.LENGTH_SHORT)
                .show()*//*
        } else {
//            Toast.makeText(appContext, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
        return isDone
    }*/
}
