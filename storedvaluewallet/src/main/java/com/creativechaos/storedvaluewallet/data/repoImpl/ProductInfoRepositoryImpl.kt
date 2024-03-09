package com.creativechaos.storedvaluewallet.data.repoImpl

import com.creativechaos.storedvaluewallet.domain.repo.ProductInfoRepository
import com.nxp.nfclib.desfire.IDESFireEV1

class ProductInfoRepositoryImpl (private val appID: ByteArray, desFireEV1: IDESFireEV1) :
    ProductInfoRepository {
    override fun getProductInfo(accessKey: Int): ByteArray {
        TODO("Not yet implemented")
    }

    override fun updateProductInfo(data: ByteArray, accessKey: Int): Boolean {
        TODO("Not yet implemented")
    }

}