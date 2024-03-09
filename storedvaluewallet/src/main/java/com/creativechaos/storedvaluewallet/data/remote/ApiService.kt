package com.creativechaos.storedvaluewallet.data.remote

import android.util.Log
import com.creativechaos.storedvaluewallet.data.remote.WalletApiEndpoints.TOKEN
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.GetBalanceResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.GetCardResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetailResponse
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletStructureResponse
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

interface ApiService {
    suspend fun getWalletStructure(walletId: Int): WalletStructureResponse?
    suspend fun getWalletDetail(): WalletDetailResponse?
    suspend fun cashIn(transactionRequest: TransactionRequest): TransactionResponse?
    suspend fun cashOut(transactionRequest: TransactionRequest): TransactionResponse?
    suspend fun adjustment(transactionRequest: TransactionRequest): TransactionResponse?
    suspend fun reversal(transactionRequest: TransactionRequest): TransactionResponse?
    suspend fun login(clientId: String, clientSecret: String): LoginResponse?
    suspend fun resetCard(uid: String): Boolean
    suspend fun getBalanceFromServer(accountNo: String): GetBalanceResponse?
    suspend fun getTransactionListing(
        accountNo: String,
        limit: Int,
        mode: String
    ): TransactionListingResponse?

    suspend fun getTransactionDetail(
        accountNo: String,
        coRelationId: String
    ): TransactionDetailResponse?

    suspend fun getCardStatus(uid: String, walletId: String): GetCardResponse?

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun create(): ApiService {
            return ApiServiceImpl(
                httpClient = HttpClient {
                    install(Logging) {
                        level = LogLevel.ALL
                    }

                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                            explicitNulls = false
                            prettyPrint = true
                            coerceInputValues = true
                        })
                    }



                    install(ResponseObserver) {
                        onResponse { response ->
                            Log.d("HTTP status:", "${response.request.url}")
                            Log.d("HTTP status:", "${response.status.value}")
                            Log.d("HTTP status:", response.bodyAsText())
                        }
                    }

                    install(DefaultRequest) {
                        header(HttpHeaders.ContentType, ContentType.Application.Json)
                        if (TOKEN.isNotEmpty())
                            header(HttpHeaders.Authorization, "Bearer $TOKEN")
                    }

                }
            )
        }
    }
}