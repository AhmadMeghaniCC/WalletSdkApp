package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class TripInfoRecord(
    @SerializedName("trp") val tripId: String,
    @SerializedName("wbn") val waybillNo: Long,
    @SerializedName("tdt") val txnDateTime: Long,
    @SerializedName("sid") val stationId: Long,
    @SerializedName("amt") val amount: Int,
    @SerializedName("mai") val merchantAccountId: Long,
    @SerializedName("tlt") val tripLogType: TripLogType
)


sealed class TripLogType(val intValue: Int) {
    object TapOut : TripLogType(0)
    object TapIn : TripLogType(1)

    object JavaField {
        @JvmField
        val TapIn = TripLogType.TapIn

        @JvmField
        val TapOut = TripLogType.TapOut
    }
}

class TripLogTypeAdapter : TypeAdapter<TripLogType>() {
    override fun write(out: JsonWriter, value: TripLogType?) {
        if (value != null) {
            out.value(value.intValue)
        } else {
            out.nullValue()
        }
    }

    override fun read(`in`: JsonReader): TripLogType {
        if (`in`.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return TripLogType.TapIn
        }

        return when (`in`.nextInt()) {
            1 -> TripLogType.TapIn
            0 -> TripLogType.TapOut
            else -> throw IllegalArgumentException("Unexpected value for TripLogType")
        }
    }
}

class TripLogTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson?, type: TypeToken<T>?): TypeAdapter<T>? {
        if (type?.rawType == TripLogType::class.java) {
            return TripLogTypeAdapter() as TypeAdapter<T>
        }
        return null
    }
}
