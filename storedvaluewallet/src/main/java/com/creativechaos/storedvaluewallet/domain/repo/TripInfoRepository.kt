package com.creativechaos.storedvaluewallet.domain.repo

interface TripInfoRepository {
    //    fun getLastTripInfoLog()
    fun getTripInfoLog(
        noOfRecords: Int = 0,
        recordSize: Int,
        tripInfoFileNumber: Int,
        accessKey: Int
    ): MutableList<String>

    fun addNewTripInfo(recordContent: ByteArray, tripInfoFileNumber: Int, accessKey: Int): Boolean
}