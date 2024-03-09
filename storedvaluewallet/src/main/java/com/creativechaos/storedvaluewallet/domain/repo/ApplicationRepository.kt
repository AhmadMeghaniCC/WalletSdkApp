package com.creativechaos.storedvaluewallet.domain.repo

import com.creativechaos.storedvaluewallet.data.model.CardApplication

interface ApplicationRepository {
    fun getApplicationsFromCard(): List<CardApplication>
    fun checkIfApplicationExist(appAID: ByteArray): Boolean
    fun selectApplicationById(): Boolean
    fun createMasterApplication(): Boolean
    fun authenticateToApplication(accessKey: Int = 0)
    fun checkIfFileExists(fileNo: Int): Boolean
    fun createFileIfNotExists(fileNo: Int): Boolean
    fun createMandatoryFiles(): Boolean
}
