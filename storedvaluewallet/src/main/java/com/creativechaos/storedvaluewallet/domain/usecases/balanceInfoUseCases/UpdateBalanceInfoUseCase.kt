package com.creativechaos.storedvaluewallet.domain.usecases.balanceInfoUseCases

import com.creativechaos.storedvaluewallet.Utils.Companion.logError
import com.creativechaos.storedvaluewallet.Utils.Companion.logInfo
import com.creativechaos.storedvaluewallet.data.model.BalanceInfoModel
import com.creativechaos.storedvaluewallet.domain.repo.BalanceInfoRepository
import com.google.gson.Gson

class UpdateBalanceInfoUseCase(private val balanceInfoRepository: BalanceInfoRepository) {

    fun execute(
        updatedBalanceInfo: BalanceInfoModel,
        balanceInfoFileNumber: Int,
        accessKey: Int
    ): Boolean {

        try {
            val data = Gson().toJson(updatedBalanceInfo)

            // Log the updated balance info data for debugging purposes
            logInfo("UpdateBalanceInfoUseCase: $data")
            logInfo("UpdateBalanceInfoUseCase: ${data.toByteArray()}")
            logInfo("UpdateBalanceInfoUseCase: ${data.toByteArray().size}")

            // Update the balance info in the repository
            return balanceInfoRepository.updateBalanceInfo(
                data.toByteArray(),
                balanceInfoFileNumber,
                accessKey
            )
        } catch (e: Exception) {
            // Log the exception for debugging purposes
            logError("An error occurred while executing UpdateBalanceInfoUseCase", e)
        }


        return false
    }
}
