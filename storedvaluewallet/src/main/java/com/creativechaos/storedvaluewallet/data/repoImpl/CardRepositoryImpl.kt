package com.creativechaos.storedvaluewallet.data.repoImpl

import android.util.Log
import com.creativechaos.storedvaluewallet.data.model.AccessKeys
import com.creativechaos.storedvaluewallet.data.model.CardInitializationException
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.CardData
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.FileStructure
import com.creativechaos.storedvaluewallet.domain.repo.CardRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.google.gson.Gson
import com.nxp.nfclib.desfire.IDESFireEV1
import com.nxp.nfclib.utils.Utilities

class CardRepositoryImpl(private val desFireInterface: IDESFireEV1) : CardRepository {

    private val cardManager: CardManager = CardManager(desFireInterface)
    private val dataFileManager: DataFileManager = DataFileManager(desFireInterface, cardManager)

    override fun initialiseCard(
        masterAppId: ByteArray, cardFileStructures: List<FileStructure>
    ): Boolean {
        return try {
            createMasterApplicationIfNotExists(masterAppId)
            createMandatoryFiles(cardFileStructures)
            true
        } catch (e: CardInitializationException) {
            e.printStackTrace()
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun isCardInitialised(masterAppId: ByteArray, filesToCheck: List<Int>): Boolean {
        return doesMasterApplicationExists(masterAppId) && checkIfFilesExist(
            filesToCheck
        )
    }

    override fun doesMasterAppExist(masterAppId: ByteArray): Boolean {
        return doesMasterApplicationExists(masterAppId)
    }

    override fun logAllCardFiles(accessKey: Int): String {
        TODO("Not yet implemented")
    }

    /*
        override fun logAllCardFiles(accessKey: Int): String {
            val mandatoryFiles = listOf(
                File.CardInfo().value,
                File.AccountInfo().value,
                File.Balance().value,
                File.BalanceInfo().value,
                File.ProductInfo().value,
                File.Ledger().value,
                File.TripInfoLog().value,
                File.TransactionLog().value,
                File.SalesInfoLog().value
            )

            val allFiles = dataFileManager.getAllFiles()
            val mandatoryFileData = StringBuilder()

            for (file in allFiles) {
                if (mandatoryFiles.contains(file)) {
                    val fileValue = readFile(file, accessKey)
                    mandatoryFileData.append("File: $file, Value: $fileValue\n")
                }
            }

            return mandatoryFileData.toString()

        }
    */

    override fun getCardFile(
        appId: ByteArray,
        cardFileStructure: FileStructure
    ): Pair<FileStructure, String> {
        return Pair(cardFileStructure, readFile(appId, cardFileStructure))

    }

    /*
        override fun getAllCardFiles(accessKey: Int): List<Pair<Int, String>> {
            val mandatoryFiles = listOf(
                File.CardInfo().value,
                File.AccountInfo().value,
                File.Balance().value,
                File.BalanceInfo().value,
                File.ProductInfo().value,
                File.Ledger().value,
                File.TripInfoLog().value,
                File.TransactionLog().value,
                File.SalesInfoLog().value
            )

            val allFiles = dataFileManager.getAllFiles()
            val fileDataList = mutableListOf<Pair<Int, String>>()


            for (file in allFiles) {
                if (mandatoryFiles.contains(file)) {
                    val fileValue = readFile(file, accessKey)
                    val fileDataPair = Pair(file, fileValue)
                    fileDataList.add(fileDataPair)
                } else {
                    fileDataList.add(Pair(file, ""))
                }
            }

            return fileDataList
        }
    */

    override fun getAllCardFiles(
        appId: ByteArray,
        cardFileStructures: List<FileStructure>
    ): List<Pair<FileStructure, String>> {
        val cardData = CardData()
        val fileDataList = mutableListOf<Pair<FileStructure, String>>()
        for (cardFile in cardFileStructures) {
            var fileContents = readFile(appId, cardFile)
            fileContents = fileContents.replace("\u0000", "")
            when (cardFile.fileName) {
                "Card Info" -> cardData.cardInfo = fileContents
                "Account Info" -> cardData.accountInfo = fileContents
                "Balance" -> cardData.balance = fileContents
                "Balance Info" -> cardData.balanceInfo = fileContents
                "Product Info" -> cardData.productInfo = fileContents
                "Ledger" -> cardData.ledger = fileContents
                "Trip Info Log" -> cardData.tripInfoLog = fileContents
                "Transaction Log" -> cardData.transactionLog = fileContents
                "Sales Info Log" -> cardData.salesInfoLog = fileContents
            }
            Log.i(
                TAG,
                "getAllCardFiles: ${Utilities.byteToHexString(appId)}, ${cardFile.fileName}: $fileContents"
            )
            fileDataList.add(Pair(cardFile, fileContents))
        }
        Log.i(TAG, "getAllCardFiles: ${Gson().toJson(cardData)}")
        return fileDataList
    }

    override fun formatCard(appId: ByteArray): Boolean {
        cardManager.performInitialChecks()
        desFireInterface.format()
        return true
    }


    private fun createMandatoryFiles(cardFileStructures: List<FileStructure>) {
        for (fileStructure in cardFileStructures) {
            createFile(fileStructure)
        }
        /*createFile(File.CardInfo(), accessKeys)
        createFile(File.AccountInfo(), accessKeys)
        createFile(File.Balance(), accessKeys)
        createFile(File.BalanceInfo(), accessKeys)
        createFile(File.ProductInfo(), accessKeys)
        createFile(File.Ledger(), accessKeys)
        createFile(File.TripInfoLog(), accessKeys)
        createFile(File.TransactionLog(), accessKeys)
        createFile(File.SalesInfoLog(), accessKeys)*/
    }

    private fun createFile(fileStructure: FileStructure) {
        when (fileStructure.fileType) {
            "Standard" -> {
                dataFileManager.createStandardDataFile(
                    standardFileNo = fileStructure.fileNo,
                    fileSize = fileStructure.fileSize,
                    accessKeys = AccessKeys(
                        readAccessKey = fileStructure.readKey,
                        writeAccessKey = fileStructure.writeKey,
                        readWriteAccessKey = fileStructure.readWriteKey,
                        changeAccessKey = fileStructure.changeKey
                    )
                )
            }

            "Value" -> {
                dataFileManager.createValueFile(
                    valueFileNo = fileStructure.fileNo,
                    lowerLimit = fileStructure.minValue!!,
                    upperLimit = fileStructure.maxValue!!,
                    initialValue = fileStructure.initialValue!!,
                    accessKeys = AccessKeys(
                        readAccessKey = fileStructure.readKey,
                        writeAccessKey = fileStructure.writeKey,
                        readWriteAccessKey = fileStructure.readWriteKey,
                        changeAccessKey = fileStructure.changeKey
                    )
                )
            }

            "Linear Record" -> {
                dataFileManager.createLinearRecordFile(
                    linearRecordFileNo = fileStructure.fileNo,
                    recordSize = fileStructure.recordSize!!,
                    maxRecords = fileStructure.noOfRecords!!,
                    accessKeys = AccessKeys(
                        readAccessKey = fileStructure.readKey,
                        writeAccessKey = fileStructure.writeKey,
                        readWriteAccessKey = fileStructure.readWriteKey,
                        changeAccessKey = fileStructure.changeKey
                    )
                )

            }

            "Cyclic Record" -> {
                dataFileManager.createCyclicRecordFile(
                    circularRecordFileNo = fileStructure.fileNo,
                    recordSize = fileStructure.recordSize!!,
                    maxRecords = fileStructure.noOfRecords!!,
                    accessKeys = AccessKeys(
                        readAccessKey = fileStructure.readKey,
                        writeAccessKey = fileStructure.writeKey,
                        readWriteAccessKey = fileStructure.readWriteKey,
                        changeAccessKey = fileStructure.changeKey
                    )
                )
            }
        }
    }

    /*
        private fun createFile(file: File, accessKeys: AccessKeys) {
            when (file) {
                is File.CardInfo -> dataFileManager.createStandardDataFile(
                    file.value, accessKeys
                )

                is File.AccountInfo -> dataFileManager.createStandardDataFile(
                    file.value, accessKeys
                )

                is File.Balance -> dataFileManager.createValueFile(
                    file.value, accessKeys
                )

                is File.BalanceInfo -> dataFileManager.createStandardDataFile(
                    file.value, accessKeys
                )

                is File.ProductInfo -> dataFileManager.createStandardDataFile(
                    file.value, accessKeys
                )

                is File.Ledger -> dataFileManager.createLinearRecordFile(
                    file.value, accessKeys
                )

                is File.TripInfoLog -> dataFileManager.createLinearRecordFile(
                    file.value, accessKeys
                )

                is File.TransactionLog -> dataFileManager.createCyclicRecordFile(
                    file.value, accessKeys
                )

                is File.SalesInfoLog -> dataFileManager.createLinearRecordFile(
                    file.value, accessKeys
                )
            }
        }
    */

    private fun performInitialOperations(appAID: ByteArray) {
        cardManager.performInitialChecks()

        if (!cardManager.checkApplicationExists(appAID))
            cardManager.createApplication(appAID)

        cardManager.selectApplicationByAID(appAID)
        cardManager.authenticateToApplication()
    }

    private fun readFile(appId: ByteArray, cardFileStructure: FileStructure): String {
        performInitialOperations(appId)
        return when (cardFileStructure.fileType) {
            "Standard" -> {
                String(
                    dataFileManager.readStandardFile(
                        standardFileNo = cardFileStructure.fileNo,
                        accessKey = cardFileStructure.readKey
                    )
                )
            }

            "Value" -> {
                dataFileManager.readValueFile(
                    valueFileNo = cardFileStructure.fileNo,
                    accessKey = cardFileStructure.readWriteKey
                ).toString()
            }

            "Linear Record" -> {
                dataFileManager.readLinearRecordFile(
                    linearRecordFileNo = cardFileStructure.fileNo,
                    accessKey = cardFileStructure.readWriteKey,
                    recordSize = cardFileStructure.recordSize!!
                ).toString()
            }

            "Cyclic Record" -> {
                dataFileManager.readCyclicRecordFile(
                    cyclicRecordFileNo = cardFileStructure.fileNo,
                    accessKey = cardFileStructure.readWriteKey,
                    recordSize = cardFileStructure.recordSize!!
                ).toString()
            }

            else -> {
                "--"
            }
        }
    }

    /*
        private fun readFile(fileValue: Int, accessKey: Int): String {
            val fileMap = mapOf(
                File.CardInfo().value to File.CardInfo(),
                File.AccountInfo().value to File.AccountInfo(),
                File.Balance().value to File.Balance(),
                File.BalanceInfo().value to File.BalanceInfo(),
                File.ProductInfo().value to File.ProductInfo(),
                File.Ledger().value to File.Ledger(),
                File.TripInfoLog().value to File.TripInfoLog(),
                File.TransactionLog().value to File.TransactionLog(),
                File.SalesInfoLog().value to File.SalesInfoLog()
            )

            val file = fileMap[fileValue]
                ?: throw IllegalArgumentException("Unsupported file value: $fileValue")

            return when (file) {
                is File.CardInfo -> String(
                    dataFileManager.readStandardFile(
                        file.value, accessKey
                    )
                )

                is File.AccountInfo -> String(
                    dataFileManager.readStandardFile(
                        file.value, accessKey
                    )
                )

                is File.Balance -> dataFileManager.readValueFile(file.value, accessKey).toString()
                is File.BalanceInfo -> String(
                    dataFileManager.readStandardFile(
                        file.value, accessKey
                    )
                )

                is File.ProductInfo -> String(
                    dataFileManager.readStandardFile(
                        file.value, accessKey
                    )
                )

                is File.Ledger -> dataFileManager.readCyclicRecordFile(
                    cyclicRecordFileNo = file.value, accessKey = accessKey
                ).toString()

                is File.TripInfoLog -> dataFileManager.readCyclicRecordFile(
                    cyclicRecordFileNo = file.value, accessKey = accessKey
                ).toString()

                is File.TransactionLog -> dataFileManager.readCyclicRecordFile(
                    cyclicRecordFileNo = file.value, accessKey = accessKey
                ).toString()

                is File.SalesInfoLog -> dataFileManager.readCyclicRecordFile(
                    cyclicRecordFileNo = file.value, accessKey = accessKey
                ).toString()
            }
        }
    */


    private fun createMasterApplicationIfNotExists(appId: ByteArray) {
        if (!doesMasterApplicationExists(appId)) cardManager.createApplication(appId)
        cardManager.selectApplicationByAID(appId)
        cardManager.authenticateToApplication()
    }

    private fun doesMasterApplicationExists(appId: ByteArray): Boolean {
        cardManager.performInitialChecks()
        val doesExist = cardManager.checkApplicationExists(appId)
        if (doesExist) {
            cardManager.selectApplicationByAID(appId)
            cardManager.authenticateToApplication()
        }
        return doesExist
    }

    private fun checkIfFilesExist(filesToCheck: List<Int>): Boolean {
        val allFiles = dataFileManager.getAllFiles()

        val allFilesSet = allFiles.toSet() // Convert to Set for faster lookup


        Log.i("StoreValueWallet", "mandatory Files $allFiles")
        Log.i("StoreValueWallet", "all Files $allFilesSet")
        Log.i("StoreValueWallet", "existingMandatoryFilesCount Files $filesToCheck")
        for (fileId in filesToCheck) {
            if (fileId !in allFilesSet) {
                return false // Return false if any file is missing
            }
        }

        return true // All files are found
    }

}
