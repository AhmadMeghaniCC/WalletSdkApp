package com.creativechaos.storedvaluewallet.data.model


sealed class File(val value: Int) {
    class CardInfo : File(0x01)
    class AccountInfo : File(0x02)
    class Balance : File(0x03)
    class BalanceInfo : File(0x04)
    class ProductInfo : File(0x05)
    class Ledger : File(0x06)
    class TripInfoLog : File(0x07)
    class TransactionLog : File(0x08)
    class SalesInfoLog : File(0x09)
}