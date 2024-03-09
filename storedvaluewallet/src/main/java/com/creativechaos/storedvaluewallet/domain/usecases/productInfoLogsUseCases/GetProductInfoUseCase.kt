package com.creativechaos.storedvaluewallet.domain.usecases.productInfoLogsUseCases

import com.creativechaos.storedvaluewallet.data.model.ProductInfo
import com.creativechaos.storedvaluewallet.domain.repo.ProductInfoRepository
import com.google.gson.Gson

class GetProductInfoUseCase(private val productInfoRepository: ProductInfoRepository) {
    fun execute(accessKey: Int): ProductInfo {
        val data = productInfoRepository.getProductInfo(accessKey)
        return Gson().fromJson(String(data).replace("\u0000", ""), ProductInfo::class.java)
    }

}