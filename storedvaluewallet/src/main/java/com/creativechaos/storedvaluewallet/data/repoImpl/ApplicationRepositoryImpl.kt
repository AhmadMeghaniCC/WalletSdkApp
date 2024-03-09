package com.creativechaos.storedvaluewallet.data.repoImpl

import com.creativechaos.storedvaluewallet.data.model.CardApplication
import com.creativechaos.storedvaluewallet.domain.repo.ApplicationRepository
import com.creativechaos.storedvaluewallet.presentation.CardManager
import com.creativechaos.storedvaluewallet.presentation.DataFileManager
import com.nxp.nfclib.desfire.IDESFireEV1

class ApplicationRepositoryImpl(private val desFireInterface: IDESFireEV1, private val dataFileManager: DataFileManager
                                , private val cardManager: CardManager): ApplicationRepository {
    override fun getApplicationsFromCard(): List<CardApplication> {
        TODO("Not yet implemented")
    }

    override fun checkIfApplicationExist(appAID: ByteArray): Boolean {
        TODO("Not yet implemented")
    }

    override fun selectApplicationById(): Boolean {
        TODO("Not yet implemented")
    }

    override fun createMasterApplication(): Boolean {
        TODO("Not yet implemented")
    }

    override fun authenticateToApplication(accessKey: Int) {
        TODO("Not yet implemented")
    }

    override fun checkIfFileExists(fileNo: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun createFileIfNotExists(fileNo: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun createMandatoryFiles(): Boolean {
        TODO("Not yet implemented")
    }
}