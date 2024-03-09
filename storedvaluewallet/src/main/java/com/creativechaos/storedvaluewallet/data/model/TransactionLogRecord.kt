package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class TransactionLogRecord(
    @SerializedName("cri") val coRelationId: String,
    @SerializedName("txt") val transactionType: TransactionType,
    @SerializedName("mai") val merchantAccountId: Long,
    @SerializedName("tdt") val txnDateTime: Long,
    @SerializedName("amt") val amount: Long
)

sealed class TransactionType(val intValue: Int) {
    object CashIn : TransactionType(1)
    object CashOut : TransactionType(2)
    object Adjustment : TransactionType(3)
    object BusSales : TransactionType(4)
    object Sale : TransactionType(5)
    object ReversalCashIn : TransactionType(11)
    object ReversalCashOut : TransactionType(12)
    object ReversalAdjustment : TransactionType(13)

    object JavaField {
        @JvmField
        val CashIn = TransactionType.CashIn

        @JvmField
        val CashOt = TransactionType.CashOut

        @JvmField
        val Adjustment = TransactionType.Adjustment

        @JvmField
        val Sale = TransactionType.Sale

        @JvmField
        val BusSales = TransactionType.BusSales

        @JvmField
        val ReversalCashIn = TransactionType.ReversalCashIn

        @JvmField
        val ReversalCashOut = TransactionType.ReversalCashOut

        @JvmField
        val ReversalAdjustment = TransactionType.ReversalAdjustment
    }

    companion object {
        fun fromInt(value: Int): TransactionType {
            return when (value) {
                1 -> CashIn
                2 -> CashOut
                3 -> Adjustment
                4 -> BusSales
                5 -> Sale
                11 -> ReversalCashIn
                12 -> ReversalCashOut
                13 -> ReversalAdjustment
                else -> throw IllegalArgumentException("Unexpected value for TransactionType")
            }
        }

        fun fromString(transactionTypeString: String): TransactionType {
            return when (transactionTypeString) {
                "Cash-In" -> CashIn
                "Cash-Out" -> CashOut
                "Adjustment" -> Adjustment
                "Sale" -> Sale
                "Bus Sales" -> BusSales
                "Reversal Cash-In" -> ReversalCashIn
                "Reversal Cash-Out" -> ReversalCashOut
                "Reversal Adjustment" -> ReversalAdjustment
                else -> throw IllegalArgumentException("Unexpected value for TransactionType string: $transactionTypeString")
            }
        }


        fun getLedgerTypeFromString(transactionTypeString: String): LedgerType {
            return when (transactionTypeString) {
                "Cash-In" -> LedgerType.Credit
                "Cash-Out" -> LedgerType.Debit
                "Adjustment" -> LedgerType.Credit
                "Sale" -> LedgerType.Debit
                "Bus Sales" -> LedgerType.Debit
                "Reversal Cash-In" -> LedgerType.Debit
                "Reversal Cash-Out" -> LedgerType.Credit
                "Reversal Adjustment" -> LedgerType.Debit
                else -> throw IllegalArgumentException("Unexpected value for TransactionType string: $transactionTypeString")
            }
        }

        fun fromJson(transactionTypeString: String): TransactionType {
            return when (transactionTypeString) {
                "cash_in" -> CashIn
                "cash_out" -> CashOut
                "adjustment" -> Adjustment
                "sale" -> Sale
                "bus_sales" -> BusSales
                "reversal_cash_in" -> ReversalCashIn
                "reversal_cash_out" -> ReversalCashOut
                "reversal_adjustment" -> ReversalAdjustment
                else -> throw IllegalArgumentException("Unexpected value for TransactionType string: $transactionTypeString")
            }
        }
    }

    // Method to get string representation of TransactionType
    fun toStringRepresentation(): String {
        return when (this) {
            is CashIn -> "Cash-In"
            is CashOut -> "Cash-Out"
            is Adjustment -> "Adjustment"
            is Sale -> "Sale"
            is BusSales -> "Bus Sales"
            is ReversalCashIn -> "Reversal Cash-In"
            is ReversalCashOut -> "Reversal Cash-Out"
            is ReversalAdjustment -> "Reversal Adjustment"
        }
    }

    fun toJsonStringRepresentation(): String {
        return when (this) {
            is CashIn -> "cash_in"
            is CashOut -> "cash_out"
            is Adjustment -> "adjustment"
            is Sale -> "sale"
            is BusSales -> "bus_sales"
            is ReversalCashIn -> "reversal_cash_in"
            is ReversalCashOut -> "reversal_cash_out"
            is ReversalAdjustment -> "reversal_adjustment"
        }
    }

}

class TransactionTypeAdapter : TypeAdapter<TransactionType>() {
    override fun write(out: JsonWriter, value: TransactionType?) {
        if (value != null) {
            out.value(value.intValue)
        } else {
            out.nullValue()
        }
    }

    override fun read(`in`: JsonReader): TransactionType {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return TransactionType.CashIn
        }

        return when (`in`.nextInt()) {
            1 -> TransactionType.CashIn
            2 -> TransactionType.CashOut
            3 -> TransactionType.Adjustment
            4 -> TransactionType.BusSales
            5 -> TransactionType.Sale
            11 -> TransactionType.ReversalCashIn
            12 -> TransactionType.ReversalCashOut
            13 -> TransactionType.ReversalAdjustment
            else -> throw IllegalArgumentException("Unexpected value for TransactionType")
        }
    }
}

class TransactionTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if (type?.rawType == TransactionType::class.java) {
            return TransactionTypeAdapter() as TypeAdapter<T>
        }
        return null
    }
}

/*
sealed class TransactionType {
    object Sale : TransactionType()
    object Purchase : TransactionType()
}

object TransactionTypes {
    @JvmField
    val Sale = TransactionType.Sale

    @JvmField
    val Purchase = TransactionType.Purchase
}

sealed class TransactionCategory {
    object BillPayment : TransactionCategory()
    object BusSales : TransactionCategory()
    object TaxiTicket : TransactionCategory()
    object EventTicket : TransactionCategory()
    object TopUp : TransactionCategory()
}

object TransactionCategories {
    @JvmField
    val BillPayment = TransactionCategory.BillPayment

    @JvmField
    val BusSales = TransactionCategory.BusSales

    @JvmField
    val TaxiTicket = TransactionCategory.TaxiTicket

    @JvmField
    val EventTicket = TransactionCategory.EventTicket

    @JvmField
    val TopUp = TransactionCategory.TopUp
}*/
