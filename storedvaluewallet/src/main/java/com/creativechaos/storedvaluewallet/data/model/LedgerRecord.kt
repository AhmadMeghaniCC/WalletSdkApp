package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class LedgerRecord(
    @SerializedName("amt") val amount: Long,
    @SerializedName("ldt") val dateTime: Long,
    @SerializedName("lty") val ledgerType: LedgerType,
    @SerializedName("vno") var versionNo: Long
)


sealed class LedgerType(val intValue: Int) {
    object Debit : LedgerType(0)
    object Credit : LedgerType(1)
}

class LedgerTypeAdapter : TypeAdapter<LedgerType>() {
    override fun write(out: JsonWriter, value: LedgerType?) {
        if (value != null) {
            out.value(value.intValue)
        } else {
            out.nullValue()
        }
    }

    override fun read(`in`: JsonReader): LedgerType {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return LedgerType.Credit
        }

        return when (`in`.nextInt()) {
            1 -> LedgerType.Credit
            0 -> LedgerType.Debit
            else -> throw IllegalArgumentException("Unexpected value for LedgerType")
        }
    }
}

class LedgerTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if (type?.rawType == LedgerType::class.java) {
            return LedgerTypeAdapter() as TypeAdapter<T>
        }
        return null
    }
}