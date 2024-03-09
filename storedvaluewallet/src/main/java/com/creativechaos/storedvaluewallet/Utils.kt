package com.creativechaos.storedvaluewallet

import android.util.Log
import com.auth0.android.jwt.JWT
import com.creativechaos.storedvaluewallet.data.model.LedgerRecord
import com.creativechaos.storedvaluewallet.data.model.LedgerTypeAdapterFactory
import com.creativechaos.storedvaluewallet.data.model.SalesInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionLogRecord
import com.creativechaos.storedvaluewallet.data.model.TransactionTypeAdapterFactory
import com.creativechaos.storedvaluewallet.data.model.TripInfoRecord
import com.creativechaos.storedvaluewallet.data.model.TripLogTypeAdapterFactory
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.AccessRights
import com.creativechaos.storedvaluewallet.data.remote.apiDataClasses.WalletDetail
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.TAG
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.security.Key
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class Utils {

    companion object {
        private const val pass = "90790c706275a734879dbe1d7a88c4ec"

        fun logInfo(message: String) {

            Log.i(TAG, "logInfo: $message")
        }

        fun logWarning(message: String) {
            // Implement your logging strategy for warning level messages
            // Example: Log.warning(message)
            Log.w(TAG, "logWarning: $message")
        }

        fun logError(message: String, throwable: Throwable) {
            // Implement your logging strategy for error level messages
            Log.e(TAG, "logError: $message and ${throwable.printStackTrace()}")

        }

        fun getLedgerStringFromLedgerObject(ledger: LedgerRecord): String {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(LedgerTypeAdapterFactory())
                .create()

            return gson.toJson(ledger)
        }

        fun getLedgerGsonObject(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(LedgerTypeAdapterFactory())
            .create()


        fun getTripLogStringFromTripLogObject(tripLog: TripInfoRecord): String {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(TripLogTypeAdapterFactory())
                .create()

            return gson.toJson(tripLog)
        }

        fun getTripLogGsonObject(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(TripLogTypeAdapterFactory())
            .create()


        fun getTxnLogGsonObject(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(TransactionTypeAdapterFactory())
            .create()

        fun getTxnLogStringFromTxnLogObject(txn: TransactionLogRecord): String {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(TransactionTypeAdapterFactory())
                .create()

            return gson.toJson(txn)
        }

        fun getSalesLogStringFromTxnLogObject(txn: SalesInfoRecord): String {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(TransactionTypeAdapterFactory())
                .create()

            return gson.toJson(txn)
        }

        fun getSalesLogGsonObject(): Gson = GsonBuilder()
            .registerTypeAdapterFactory(TransactionTypeAdapterFactory())
            .create()

        fun getFormattedDateTime(timestampInSeconds: Long): String {
            val date = Date(timestampInSeconds * 1000)
            val dateFormat = SimpleDateFormat("MMMM dd, yyyy h:mm a", Locale.ENGLISH)
            return dateFormat.format(date)
        }

        fun getFormattedDateTime1(timestampInSeconds: Long): String {
            val date = Date(timestampInSeconds * 1000)
            val dateFormat = SimpleDateFormat("dd-MMM-yy h:mm a", Locale.ENGLISH)
            return dateFormat.format(date)
        }

        fun getEpochTimeFromFormattedDateTime(formattedDateTime: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val date = dateFormat.parse(formattedDateTime)
            return (date?.time?.div(1000)) ?: -1 // If parsing fails, return -1
        }

        fun getFormattedDateTime2(timestampInSeconds: Long): String {
            val date = Date(timestampInSeconds * 1000)
            val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH)
            return dateFormat.format(date)
        }

        fun convertDateTime(
            inputDateTime: String,
            inputFormat: String,
            outputFormat: String
        ): String {
            val inputDateFormat = SimpleDateFormat(inputFormat, Locale.US)
            val outputDateFormat = SimpleDateFormat(outputFormat, Locale.US)

            try {
                val date: Date? = inputDateFormat.parse(inputDateTime)
                return outputDateFormat.format(date) ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return ""
        }


        @OptIn(ExperimentalEncodingApi::class)
        fun encryptData(dataToEncrypt: String): String {
            return try {
                val key: Key = SecretKeySpec(pass.toByteArray(), "AES")
                val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, key)
                val encryptedData: ByteArray = cipher.doFinal(dataToEncrypt.toByteArray())
                Base64.encode(encryptedData)
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        @OptIn(ExperimentalEncodingApi::class)
        fun decryptData(encryptedData: String?): String {
            return try {
                if (encryptedData != null) {
                    val decodedData: ByteArray = Base64.decode(encryptedData)
                    val key: ByteArray = pass.toByteArray()
                    val secretKey: SecretKeySpec = SecretKeySpec(key, "AES")
                    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
                    cipher.init(Cipher.DECRYPT_MODE, secretKey)
                    val decryptedData: ByteArray = cipher.doFinal(decodedData)
                    String(decryptedData)
                } else
                    "--"

            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }

        /*fun decodeJwtToken(token: String, privateKey: String = ""): Jws<Claims>? {
//            val key = Jwts.SIG.HS256.key().build()
//            val key1 = Jwts.SIG.get().
            return Jwts.parser().setSigningKey(privateKey.toByteArray()).build().parseSignedClaims(token)
        }*/

        fun decodeJwtToken(token: String, privateKey: String = ""): JWT {
            val jwtToken = JWT(token)
            return jwtToken
        }

        fun getFileNo(fileName: String): Int {
            return when (fileName) {
                "Card Info" -> 0
                "Account Info" -> 3
                "Balance" -> 6
                "Ledger" -> 15
                "Transaction Log" -> 21
                "Balance Info" -> 9
                "Product Info" -> 12
                "Trip Info Log" -> 18
                "Sales Info Log" -> 24
                else -> -1
            }
        }


        fun getRecordSize(fileNumber: Int): Int {
            return when (fileNumber) {
                getFileNo("Ledger") -> 48
                getFileNo("Transaction Log") -> 96
                getFileNo("Trip Info Log") -> 128
                getFileNo("Sales Info Log") -> 96
                else -> -1


            }
        }

        fun getAccessInfo(walletDetail: WalletDetail, fileName: String): AccessRights? {
            val pickedWallet =
                walletDetail.rights.find { it.fileNumber == getFileNo(fileName) && (it.rights == "readwrite" /*|| it.rights == "write" || it.rights == "read"*/) }
            Log.i(TAG, "getAccessInfo: $fileName - ${Gson().toJson(pickedWallet)}")
            return pickedWallet
        }
    }
}
