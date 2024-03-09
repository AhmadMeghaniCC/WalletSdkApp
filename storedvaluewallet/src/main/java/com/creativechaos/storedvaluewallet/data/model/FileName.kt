package com.creativechaos.storedvaluewallet.data.model


sealed class FileName(val fileName: String) {
    class CardInfo : FileName("Card Info")
    class AccountInfo : FileName("Account Info")
    class Balance : FileName("Balance")
    class BalanceInfo : FileName("Balance Info")
    class ProductInfo : FileName("Product Info")
    class Ledger : FileName("Ledger")
    class TripInfoLog : FileName("Trip Info Log")
    class TransactionLog : FileName("Transaction Log")
    class SalesInfoLog : FileName("Sales Info Log")
}