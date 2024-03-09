package com.creativechaos.storedvaluewallet.domain.usecases.balanceInfoUseCases

import com.creativechaos.storedvaluewallet.Utils.Companion.logError
import com.creativechaos.storedvaluewallet.Utils.Companion.logInfo
import com.creativechaos.storedvaluewallet.Utils.Companion.logWarning
import com.creativechaos.storedvaluewallet.data.model.BalanceInfoModel
import com.creativechaos.storedvaluewallet.domain.repo.BalanceInfoRepository
import com.google.gson.Gson
import java.nio.charset.StandardCharsets

class GetBalanceInfoUseCase(private val balanceInfoRepository: BalanceInfoRepository) {

    fun execute(balanceInfoFileNumber: Int, accessKey: Int): BalanceInfoModel? {
        return try {
            val data = balanceInfoRepository.getBalanceInfo(balanceInfoFileNumber, accessKey)

            if (data.isEmpty()) {
                // Log a warning if the retrieved data is empty or null
                logWarning("No data retrieved for balanceInfoFileNumber: $balanceInfoFileNumber, accessKey: $accessKey")
                null
            } else {
                // Assuming "\u0000" is used to denote the end of the string, remove it before deserialization
                val cleanedData = String(data, StandardCharsets.UTF_8).replace("\u0000", "")

                // Log the cleaned data for debugging purposes
                logInfo("Cleaned data: $cleanedData")

                Gson().fromJson(cleanedData, BalanceInfoModel::class.java)
            }
        } catch (e: Exception) {
            // Log the exception for debugging purposes
            logError("An error occurred while executing GetBalanceInfoUseCase", e)

            // Return null or another default value in case of an error
            null
        }
    }
}
