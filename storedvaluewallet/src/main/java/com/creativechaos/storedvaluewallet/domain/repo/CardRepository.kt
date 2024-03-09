package com.creativechaos.storedvaluewallet.domain.repo

import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.FileStructure

interface CardRepository {
    fun initialiseCard(masterAppId: ByteArray, cardFileStructures: List<FileStructure>): Boolean
    fun isCardInitialised(masterAppId: ByteArray, filesToCheck: List<Int>): Boolean
    fun doesMasterAppExist(masterAppId: ByteArray): Boolean

    fun logAllCardFiles(accessKey: Int): String
    fun getCardFile(
        appId: ByteArray,
        cardFileStructure: FileStructure
    ): Pair<FileStructure, String>

    fun getAllCardFiles(
        appId: ByteArray,
        cardFileStructures: List<FileStructure>
    ): List<Pair<FileStructure, String>>

    fun formatCard(appId: ByteArray): Boolean
}