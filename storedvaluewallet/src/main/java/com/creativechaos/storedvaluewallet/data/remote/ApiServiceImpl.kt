package com.creativechaos.storedvaluewallet.data.remote

import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.BASE_URL
import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.CARD
import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.GET_BALANCE
import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.TOKEN
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.GetBalanceResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.GetCardResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetailResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletStructureResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ApiServiceImpl(
    private val httpClient: HttpClient
) : ApiService {
    override suspend fun getWalletStructure(walletId: Int): WalletStructureResponse? {
        return try {
            return httpClient.get {
                url("$BASE_URL${WalletApiEndpoints.WALLET_STRUCTURE}")
                parameter("wallet_id", walletId)
            }.body<WalletStructureResponse>()

        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<WalletStructureResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<WalletStructureResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun getWalletDetail(): WalletDetailResponse? {
        return try {
            return httpClient.get {
                url("$BASE_URL${WalletApiEndpoints.WALLET_DETAIL}")
            }.body<WalletDetailResponse>()

        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<WalletDetailResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<WalletDetailResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun cashIn(transactionRequest: TransactionRequest): TransactionResponse? {
        return try {
            httpClient.post {
                url(BASE_URL + WalletApiEndpoints.CASH_IN)
                setBody(transactionRequest)
            }.body<TransactionResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun cashOut(transactionRequest: TransactionRequest): TransactionResponse? {
        return try {
            httpClient.post {
                url(BASE_URL + WalletApiEndpoints.CASH_OUT)
                setBody(transactionRequest)
            }.body<TransactionResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun adjustment(transactionRequest: TransactionRequest): TransactionResponse? {
        return try {
            httpClient.post {
                url(BASE_URL + WalletApiEndpoints.ADJUSTMENT)
                setBody(transactionRequest)
            }.body<TransactionResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun reversal(transactionRequest: TransactionRequest): TransactionResponse? {
        return try {
            httpClient.post {
                url(BASE_URL + WalletApiEndpoints.REVERSAL)
                setBody(transactionRequest)
            }.body<TransactionResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun login(clientId: String, clientSecret: String): LoginResponse? {
        return try {
            val body = httpClient.post {
                url("$BASE_URL${WalletApiEndpoints.LOGIN}")
                headers {
                    append("X-client-id", clientId)
                    append("X-client-secret", clientSecret)
                }
            }.body<LoginResponse>()
            if (body.status == 1) TOKEN = body.data?.token!!
            body
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<LoginResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<LoginResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun resetCard(uid: String): Boolean {
        val requestBody = mapOf(
            "uid" to uid
        )
        return try {
            val body = httpClient.post {
                url(BASE_URL + WalletApiEndpoints.RESET_CARD)
                setBody(Json.encodeToString(requestBody))
            }.body<ResetCardResponse>()
            body.status == 1
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            false
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            false
        } catch (e: Exception) {
            println(e.message)
            false
        }
    }

    override suspend fun getBalanceFromServer(accountNo: String): GetBalanceResponse? {
        val requestBody = mapOf(
            "account_no" to accountNo
        )
        return try {
            httpClient.post {
                url(BASE_URL + GET_BALANCE)
                setBody(Json.encodeToString(requestBody))
            }.body<GetBalanceResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<GetBalanceResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<GetBalanceResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun getTransactionListing(
        accountNo: String, limit: Int, mode: String
    ): TransactionListingResponse? {
        return try {
            httpClient.get {
                url(BASE_URL + WalletApiEndpoints.TRANSACTION)
                parameter("account_no", accountNo)
                parameter("limit", limit.toString())
                parameter("transaction_mode", mode)
            }.body<TransactionListingResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionListingResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionListingResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun getTransactionDetail(
        accountNo: String, coRelationId: String
    ): TransactionDetailResponse? {
        return try {
            httpClient.get {
                url(BASE_URL + WalletApiEndpoints.TRANSACTION)
                parameter("account_no", accountNo)
                parameter("correlation_id", coRelationId)
            }.body<TransactionDetailResponse>()
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<TransactionDetailResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<TransactionDetailResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    override suspend fun getCardStatus(uid: String, walletId: String): GetCardResponse? {

        return try {
            val response = httpClient.get {
                url(BASE_URL + CARD)
                parameter("uid", uid)
                parameter("wallet_id", walletId)
            }.body<GetCardResponse>()

            return response
        } catch (e: RedirectResponseException) {
            println(e.response.status.description)
            e.response.body<GetCardResponse>()
        } catch (e: ClientRequestException) {
            println(e.response.status.description)
            e.response.body<GetCardResponse>()
        } catch (e: Exception) {
            println(e.message)
            null
        }

    }
}