package com.creativechaos.storedvaluewallet.presentation

import android.util.Log
import com.creativechaos.storedvaluewallet.Utils.Companion.logError
import com.creativechaos.storedvaluewallet.data.model.AccessKeys
import com.nxp.nfclib.desfire.DESFireFile
import com.nxp.nfclib.desfire.IDESFireEV1

/**
 * The [DataFileManager] class provides methods to manage data files on a DESFireEV1 card.
 *
 * @property desFireEV1 The DESFireEV1 instance.
 * @property cardManager The CardManager instance.
 */
class DataFileManager(
    private val desFireEV1: IDESFireEV1,
    private val cardManager: CardManager
) {

    private companion object {
        private const val TAG = "DataFileManager"
    }

    private var startTime: Long = 0

    /**
     * Creates a standard data file on the card.
     */
    fun createStandardDataFile(standardFileNo: Int, fileSize: Int, accessKeys: AccessKeys) {
        startTime = System.currentTimeMillis()
        logStart("createStandardDataFile")


        println("Debug Log - createStandardDataFile Function:")
        println("linearRecordFileNo: $standardFileNo")
        println("fileSize: $fileSize")
        println("accessKeys.readAccessKey: ${accessKeys.readAccessKey}")
        println("accessKeys.writeAccessKey: ${accessKeys.writeAccessKey}")
        println("accessKeys.readWriteAccessKey: ${accessKeys.readWriteAccessKey}")
        println("accessKeys.changeAccessKey: ${accessKeys.changeAccessKey}")

        if (!checkFileExists(standardFileNo)) {
            val fileSettings = DESFireFile.StdDataFileSettings(
                IDESFireEV1.CommunicationType.Enciphered,
                accessKeys.readAccessKey.toByte(),
                accessKeys.writeAccessKey.toByte(),
                accessKeys.readWriteAccessKey.toByte(),
                accessKeys.changeAccessKey.toByte(),
                fileSize
            )
            desFireEV1.createFile(standardFileNo, fileSettings)
        }

        logEnd("createStandardDataFile")
    }

    /**
     * Checks if a file with the given file number exists on the card.
     *
     * @param fileNo The file number to check.
     * @return `true` if the file exists, `false` otherwise.
     */
    fun checkFileExists(fileNo: Int): Boolean {
        startTime = System.currentTimeMillis()
        logStart("checkFileExists $fileNo")

        val fileIdsByteArray = desFireEV1.fileIDs
        val fileIds = fileIdsByteArray.map { it.toInt() }

        logEnd("checkFileExists $fileNo")

        return fileIds.contains(fileNo)
    }

    fun getAllFiles(): List<Int> {
        startTime = System.currentTimeMillis()
        logStart("getAllFiles")

        val fileIdsByteArray = desFireEV1.fileIDs
        val fileIds = fileIdsByteArray.map { it.toInt() }

        logEnd("getAllFiles")

        return fileIds
    }

    /**
     * Writes data to the standard data file on the card.
     */
    fun writeDataToStandardFile(standardFileNo: Int, content: ByteArray, accessKey: Int) {
        startTime = System.currentTimeMillis()
        logStart("writeDataToStandardFile")

        cardManager.authenticateToApplication(accessKey)
        val fileOffset = 0
        desFireEV1.writeData(standardFileNo, fileOffset, content)
        desFireEV1.commitTransaction()

        logEnd("writeDataToStandardFile")
    }

    /**
     * Creates a value file on the card.
     */
    fun createValueFile(
        valueFileNo: Int,
        lowerLimit: Int,
        upperLimit: Int,
        initialValue: Int,
        accessKeys: AccessKeys
    ) {
        startTime = System.currentTimeMillis()
        logStart("createValueFile")

        // Debug logs for parameters and their values
        println("Debug Log - createValueFile Function:")
        println("valueFileNo: $valueFileNo")
        println("lowerLimit: $lowerLimit")
        println("upperLimit: $upperLimit")
        println("initialValue: $initialValue")
        println("accessKeys.readAccessKey: ${accessKeys.readAccessKey}")
        println("accessKeys.writeAccessKey: ${accessKeys.writeAccessKey}")
        println("accessKeys.readWriteAccessKey: ${accessKeys.readWriteAccessKey}")
        println("accessKeys.changeAccessKey: ${accessKeys.changeAccessKey}")

        if (!checkFileExists(valueFileNo)) {
            val limitedCreditValueEnabled = false
            val freeValueEnabled = false

            val fileSettings = DESFireFile.ValueFileSettings(
                IDESFireEV1.CommunicationType.Enciphered,
                /*8.toByte(),
                9.toByte(),
                10.toByte(),
                11.toByte(),*/
                accessKeys.readAccessKey.toByte(),
                accessKeys.writeAccessKey.toByte(),
                accessKeys.readWriteAccessKey.toByte(),
                accessKeys.changeAccessKey.toByte(),
                lowerLimit,
                upperLimit,
                initialValue,
                limitedCreditValueEnabled,
                freeValueEnabled
            )

            // Debug log before creating the file
            println("Debug Log - Before creating file")

            desFireEV1.createFile(valueFileNo, fileSettings)
        }

        logEnd("createValueFile")
    }


    /**
     * Credits the value in the value file on the card.
     *
     * @param value The value to credit.
     */

    fun creditValue(value: Int, valueFileNo: Int, accessKey: Int) {
        startTime = System.currentTimeMillis()
        logStart("creditValue")

        cardManager.authenticateToApplication(accessKey)
        desFireEV1.credit(valueFileNo, value, IDESFireEV1.CommunicationType.Enciphered)
        desFireEV1.commitTransaction()

        logEnd("creditValue")
    }

    /**
     * Debits the value from the value file on the card.
     *
     * @param value The value to debit.
     */

    fun debitValue(value: Int, valueFileNo: Int, accessKey: Int) {
        startTime = System.currentTimeMillis()
        logStart("debitValue")

        cardManager.authenticateToApplication(accessKey)
        desFireEV1.debit(valueFileNo, value)
        desFireEV1.commitTransaction()

        logEnd("debitValue")
    }


    /**
     * Creates a linear record file on the card.
     */
    fun createLinearRecordFile(
        linearRecordFileNo: Int,
        recordSize: Int,
        maxRecords: Int,
        accessKeys: AccessKeys
    ) {
        startTime = System.currentTimeMillis()
        logStart("createLinearRecordFile")

        // Debug logs for parameters and their values
        println("Debug Log - createLinearRecordFile Function:")
        println("linearRecordFileNo: $linearRecordFileNo")
        println("recordSize: $recordSize")
        println("maxRecords: $maxRecords")
        println("accessKeys.readAccessKey: ${accessKeys.readAccessKey}")
        println("accessKeys.writeAccessKey: ${accessKeys.writeAccessKey}")
        println("accessKeys.readWriteAccessKey: ${accessKeys.readWriteAccessKey}")
        println("accessKeys.changeAccessKey: ${accessKeys.changeAccessKey}")

        if (!checkFileExists(linearRecordFileNo)) {
            val fileSettings = DESFireFile.LinearRecordFileSettings(
                IDESFireEV1.CommunicationType.Enciphered,
                /*9.toByte(),
                10.toByte(),
                11.toByte(),
                12.toByte(),*/
                accessKeys.readAccessKey.toByte(),
                accessKeys.writeAccessKey.toByte(),
                accessKeys.readWriteAccessKey.toByte(),
                accessKeys.changeAccessKey.toByte(),
                recordSize,
                maxRecords,
                0 // Set the initial number of records to 0
            )

            // Debug log before creating the file
            println("Debug Log - Before creating file")

            desFireEV1.createFile(linearRecordFileNo, fileSettings)
        }

        logEnd("createLinearRecordFile")
    }


    /**
     * Writes a record to the linear record file on the card.
     */
    fun writeRecordToFile(data: ByteArray, linearRecordFileNo: Int, accessKey: Int) {
        startTime = System.currentTimeMillis()
        logStart("writeRecordToFile")

        cardManager.authenticateToApplication(accessKey)
        val offsetInRecord = 0
        desFireEV1.writeRecord(
            linearRecordFileNo,
            offsetInRecord,
            data,
            IDESFireEV1.CommunicationType.Enciphered
        )
        desFireEV1.commitTransaction()

        logEnd("writeRecordToFile")
    }


    /**
     * Creates a cyclic record file on the card.
     */
    fun createCyclicRecordFile(
        circularRecordFileNo: Int,
        recordSize: Int,
        maxRecords: Int,
        accessKeys: AccessKeys
    ) {
        startTime = System.currentTimeMillis()
        logStart("createCyclicRecordFile")


        println("Debug Log - createCyclicRecordFile Function:")
        println("linearRecordFileNo: $circularRecordFileNo")
        println("recordSize: $recordSize")
        println("maxRecords: $maxRecords")
        println("accessKeys.readAccessKey: ${accessKeys.readAccessKey}")
        println("accessKeys.writeAccessKey: ${accessKeys.writeAccessKey}")
        println("accessKeys.readWriteAccessKey: ${accessKeys.readWriteAccessKey}")
        println("accessKeys.changeAccessKey: ${accessKeys.changeAccessKey}")

        if (!checkFileExists(circularRecordFileNo)) {
            val fileSettings = DESFireFile.CyclicRecordFileSettings(
                IDESFireEV1.CommunicationType.Enciphered,
                accessKeys.readAccessKey.toByte(),
                accessKeys.writeAccessKey.toByte(),
                accessKeys.readWriteAccessKey.toByte(),
                accessKeys.changeAccessKey.toByte(),
                recordSize,
                maxRecords,
                0, // Set the initial number of records to 0
            )
            desFireEV1.createFile(circularRecordFileNo, fileSettings)
            Log.i(
                TAG,
                "Cyclic record file with file number $circularRecordFileNo created successfully."
            )
        } else {
            Log.i(
                TAG,
                "Cyclic record file with file number $circularRecordFileNo already exists. Skipping creation."
            )
        }

        logEnd("createCyclicRecordFile")
    }


    /**
     * Writes a record to the cyclic record file on the card.
     */
    fun writeCyclicRecordToFile(data: ByteArray, circularRecordFileNo: Int, accessKey: Int) {
        startTime = System.currentTimeMillis()
        logStart("writeCyclicRecordToFile")

//        try {
            cardManager.authenticateToApplication(accessKey)
            val offsetInRecord = 0
            desFireEV1.writeRecord(
                circularRecordFileNo,
                offsetInRecord,
                data,
                IDESFireEV1.CommunicationType.Enciphered
            )
            desFireEV1.commitTransaction()
//        } catch (e: Exception) {
//            logError("error in writing", e)
//        }

        logEnd("writeCyclicRecordToFile")
    }

    /**
     * Reads the value from the value file on the card.
     */
    fun readValueFile(valueFileNo: Int, accessKey: Int): Int {
        try {
            startTime = System.currentTimeMillis()
            logStart("readValueFile")
            Log.d("readValueFile", "ACCESS_KEY: $accessKey")
            Log.d("readValueFile", "valueFileNo: $valueFileNo")

            cardManager.authenticateToApplication(accessKey)
            val value = desFireEV1.getValue(valueFileNo)
            Log.i(TAG, "Value File No: $valueFileNo: Current value is $value")


            return value
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        } finally {
            logEnd("readValueFile")
        }
    }

    /**
     * Reads the content from the standard file on the card.
     */
    fun readStandardFile(standardFileNo: Int, accessKey: Int): ByteArray {
        try {
            startTime = System.currentTimeMillis()
            logStart("readStandardFile")

            Log.d("readStandardFile", "ACCESS_KEY: $accessKey")
            Log.d("readStandardFile", "standardFileNo: $standardFileNo")

            cardManager.authenticateToApplication(accessKey)
            val content = desFireEV1.readData(standardFileNo, 0, 0)
            Log.i(TAG, "Standard File No: $standardFileNo: Content is ${String(content)}")
            return content
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        } finally {
            logEnd("readStandardFile")
        }
    }

    fun readLinearRecordFile(
        noOfRecords: Int = 0,
        linearRecordFileNo: Int,
        recordSize: Int,
        accessKey: Int
    ): MutableList<String> {
        startTime = System.currentTimeMillis()
        logStart("readLinearRecordFile")


        Log.d("readLinearRecordFile", "ACCESS_KEY: $accessKey")
        Log.d("readLinearRecordFile", "noOfRecords: $noOfRecords")
        Log.d("readLinearRecordFile", "linearRecordFileNo: $linearRecordFileNo")

        cardManager.authenticateToApplication(accessKey)

        val recordsList: MutableList<String> = mutableListOf<String>()
        try {
            val offsetRecords = 0
            val fileData = desFireEV1.readRecords(
                linearRecordFileNo,
                offsetRecords,
                noOfRecords
            )


            Log.i(TAG, "Linear Record File No: $linearRecordFileNo")
            for (i in 0 until fileData.size / recordSize) {
                val startIndex = i * recordSize
                val endIndex = startIndex + recordSize
                val record = fileData.copyOfRange(startIndex, endIndex)
                recordsList.add(String(record))
                Log.i(TAG, "Record $i: Linear is ${String(record)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        logEnd("readLinearRecordFile")

//        if (recordsList.isEmpty())
//            recordsList.add("--")
        return recordsList
    }

    fun readCyclicRecordFile(
        noOfRecords: Int = 0,
        cyclicRecordFileNo: Int,
        recordSize: Int,
        accessKey: Int
    ): MutableList<String> {
        startTime = System.currentTimeMillis()
        logStart("readCyclicRecordFile")


        Log.d("readCyclicRecordFile", "ACCESS_KEY: $accessKey")
        Log.d("readCyclicRecordFile", "noOfRecords: $noOfRecords")
        Log.d("readCyclicRecordFile", "linearRecordFileNo: $cyclicRecordFileNo")

        cardManager.authenticateToApplication(accessKey)
        val recordsList = mutableListOf<String>()

        try {
            val offsetRecords = 0
            val fileData = desFireEV1.readRecords(cyclicRecordFileNo, offsetRecords, noOfRecords)

            Log.i(TAG, "Cyclic Record File No: $cyclicRecordFileNo")
            Log.i(TAG, "Cyclic Record File Data SIze: ${fileData.size}")
            for (i in 0 until fileData.size / recordSize) {
                val startIndex = i * recordSize
                val endIndex = startIndex + recordSize
                val record = fileData.copyOfRange(startIndex, endIndex)
                recordsList.add(String(record))
                Log.i(TAG, "Record $i: Cyclic is ${String(record)}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        logEnd("readCyclicRecordFile")


//        if (recordsList.isEmpty())
//            recordsList.add("--")
        Log.i(TAG, "readCyclicRecordFile: records list size ${recordsList.size}")
//        Log.i(TAG, "readCyclicRecordFile: records list size ${recordsList[0].toString()}")
        return recordsList
    }

    private fun logStart(methodName: String) {
        Log.i(TAG, "Method $methodName started.")
    }

    private fun logEnd(methodName: String) {
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.i(TAG, "Method $methodName finished. Duration: $duration ms")
    }
}
