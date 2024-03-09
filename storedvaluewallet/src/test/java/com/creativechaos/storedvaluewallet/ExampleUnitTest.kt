package com.creativechaos.storedvaluewallet

import com.creativechaos.storedvaluewallet.data.model.BalanceInfoModel
import com.creativechaos.storedvaluewallet.data.model.CardInfoModel
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.data.model.LedgerType
import com.creativechaos.storedvaluewallet.data.model.ProductInfo
import com.creativechaos.storedvaluewallet.data.model.SalesInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionLogRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionType
import com.creativechaos.storedvaluewallet.data.model.TripInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TripLogType
import com.creativechaos.storedvaluewallet.data.model.UserAccount
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun checkAccountInfoJson() {
        val builder = UserAccount.UserAccountBuilder()
        builder.accountNo(1234567890).accountTitle("Muhammad Ahmad Meghani").accountStatus(99)
            .phoneNumber("1234567890").accountCurrency("ZAR").accountPrecision(2)
        println(Gson().toJson(builder.build()))
        println(Gson().toJson(builder.build()).length)
    }

    @Test
    fun checkCardInfoJson() {
        val cardInfo = CardInfoModel(
            UID = "AHFKSM56YFH34M",
            issueDate = (System.currentTimeMillis() / 1000),
            cardStatus = 2,
            walletId = 23,
            walletCode = "EP",
            validity = 5,
            walletStructureVersionNumber = 1
        )

        println(Gson().toJson(cardInfo))
        println(Gson().toJson(cardInfo).length)
    }

    @Test
    fun checkBalanceInfoJson() {
        val balanceInfo = BalanceInfoModel(
            lastModifiedAt = System.currentTimeMillis() / 1000,
            versionNo = 1,
            syncStatus = 99,
            lastSyncedOn = System.currentTimeMillis() / 1000
        )

        println(Gson().toJson(balanceInfo))
        println(Gson().toJson(balanceInfo).length)
    }

    @Test
    fun checkProductInfoJson() {
        val productInfo = ProductInfo(
            productId = 1,
            productExpiry = System.currentTimeMillis() / 1000,
            productPurchasedOn = System.currentTimeMillis() / 1000
        )

        println(Gson().toJson(productInfo))
        println(Gson().toJson(productInfo).length)
    }

    @Test
    fun checkLedgerJson() {
        val ledger = LedgerRecord(
            amount = 345,
            dateTime = System.currentTimeMillis() / 1000,
            LedgerType.Debit
        )

        println(Utils.getLedgerStringFromLedgerObject(ledger))
        println(Utils.getLedgerStringFromLedgerObject(ledger).length)
    }

    @Test
    fun checkTripInfoJson() {
        val tripInfo = TripInfoRecord(
            tripId = "zaf0045-AbF232400",
            waybillNo = 11111111,
            txnDateTime = System.currentTimeMillis() / 1000,
            stationId = 1231,
            amount = 232,
            merchantAccountId = 100555,
            tripLogType = TripLogType.TapIn
        )


        println(Utils.getTripLogStringFromTripLogObject(tripInfo))
        println(Utils.getTripLogStringFromTripLogObject(tripInfo).length)
    }

    @Test
    fun checkTxnInfoLogJson() {
        val tripInfo = TransactionLogRecord(
            coRelationId = "zaf0045-AbF232400",
            txnDateTime = System.currentTimeMillis() / 1000,
            amount = 232,
            merchantAccountId = 100555,
            transactionType = TransactionType.CashIn
        )

        println(Utils.getTxnLogStringFromTxnLogObject(tripInfo))
        println(Utils.getTxnLogStringFromTxnLogObject(tripInfo).length)

    }

    @Test
    fun checkSalesInfoLogJson() {
        val tripInfo = SalesInfoRecord(
            coRelationId = "zaf0045-AbF232400",
            txnDateTime = System.currentTimeMillis() / 1000,
            amount = 232,
            merchantAccountId = 100555,
            transactionType = TransactionType.CashIn,
            referenceId = "232311"
        )

        println(Utils.getSalesLogStringFromTxnLogObject(tripInfo))
        println(Utils.getSalesLogStringFromTxnLogObject(tripInfo).length)
    }

    @Test
    fun encryptData() {
        val password = "ahmed12345"
        val encryptedData = Utils.encryptData(password)
        val decryptedData = Utils.decryptData(encryptedData)

        println("Encrypted Password $encryptedData")
        println("Decrypted Password $decryptedData")

        val encryptedClientId = "limJMHzy73SH8omLjNtxFkQL1Gju6NrKpfAiE9S3Yag="
        val decryptedClientId = Utils.decryptData(encryptedClientId)
        println("Encrypted ClientId $encryptedClientId")
        println("Decrypted ClientId $decryptedClientId")

    }

    @Test
    fun decodeToken() {
        val token =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJzdmNfd2FsbGV0X2FwcF8xLjAiLCJhdWQiOiJjMnBheSIsImlhdCI6MTcwNjY4ODc0MiwiZXhwIjoxNzM4MjI0NzQyLCJhdXRoX3RpbWUiOjE3MDY2ODg3NDIsImVudGl0eSI6ImFtZWdoYW5pIiwiY2xpZW50X2lkIjoid3BpUmlyQkpuUjduTUM0VFpoUUpTQT09IiwiY2xpZW50X3NlY3JldCI6IndwaVJpckJKblI3bk1DNFRaaFFKU0E9PSIsIm1lcmNoYW50X2lkIjoyNTEsIm1lcmNoYW50X25hbWUiOiJUZXN0IFRyYW5zLiBDb3Jwb3JhdGlvbiIsImN1cnJlbmN5IjoiUEtSIiwidGltZXpvbmUiOiIrMDU6MDAiLCJwcmVjaXNpb24iOm51bGwsInBob25lX2xlbmd0aCI6bnVsbCwiZGV2aWNlX2lkIjpudWxsLCJ1c2VyX2lkIjpudWxsLCJ0Ijo1MCwiYWNjZXNzX3JpZ2h0cyI6W119.CoCSetiqWnWUVZ69ZkPLz8DkJAJoqX7K7Jqw_fdwJEPMDQrdEKbZjaD8CFwQcrQczEmoLP6iW46ROSf9LnBC2XxZ8goxDU9K-91DHdLxWMVTDxKs1PlT6h7O9vE3GVHnhL4BiecQVJRt7vkEDQntkVEgpcsgmW56QGre8HfMOaebbkRltRwfId1lLeWAUBBv8FlZz2rEO-h2aojn4aM8IYw4QxX5akklYJEbx_lcYODnssTx5b6RCFOlUDKoAKQ8n49Q8sPqbiHawR7B-Qvqv62PY4rWP2crCWldC04ZOc185ukRWvkCLPXVLYa7grlje4uhw3s6qQHekxZxZT8v5Q"
        val key = "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxUiAfjy764fN36CQvw2L\n" +
                "MrECyQ10e102iHSFLnS6Vf4ibE+gvHrUrb267OSH2vf9nrMtulcxOxOUwiSJqOGW\n" +
                "f3v7RrDifDrLr7w+3lXnF36ngtz3GMrpK7qBw66olb8n/sGv9pEO4yPo+N6/z1Ip\n" +
                "lTtrPAeugVuaHPhFfMmy63uWBuFGHDv9YrqFOjkfpeYcR6XNwqV26zMYJwDfajBi\n" +
                "8UyiUE/HXMFEMZ3lFJQ7uGdbIA7R5Plogv6w4uw4/HkaSMGNmzCTHQrZnLPlfsQj\n" +
                "7kzT4U8keLy035pGJLmptvt8R20EfTw3wejAqYpPgE0/YN3Usn5GsRJUtv942iIo\n" +
                "JwIDAQAB\n" +
                "-----END PUBLIC KEY-----".trimIndent()
        val decodedToken = Utils.decodeJwtToken(token, key)

//        println(decodedToken?.body)

    }
}