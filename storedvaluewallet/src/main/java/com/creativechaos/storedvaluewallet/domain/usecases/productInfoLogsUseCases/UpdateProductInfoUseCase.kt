package com.creativechaos.storedvaluewallet.domain.usecases.productInfoLogsUseCases

import com.creativechaos.storedvaluewallet.data.model.ProductInfo
import com.creativechaos.storedvaluewallet.domain.repo.ProductInfoRepository
import com.google.gson.Gson

class UpdateProductInfoUseCase(private val productInfoRepository: ProductInfoRepository) {
    fun execute(
        productId: Int,
        productExpiry: Long,
        productPurchasedOn: Long,
        accessKey: Int
    ): Boolean {
        val data = Gson().toJson(ProductInfo(productId, productExpiry, productPurchasedOn))
            .toByteArray()
        return productInfoRepository.updateProductInfo(data, accessKey)
    }
}