package com.creativechaos.storedvaluewallet.data.model

import com.google.gson.annotations.SerializedName

data class UserAccount(
    @SerializedName("ano") var accountNo: Long,
    @SerializedName("atl") var accountTitle: String,
    @SerializedName("ast") var accountStatus: Int,
    @SerializedName("pno") var phoneNumber: String,
    @SerializedName("cur") var accountCurrency: String,
    @SerializedName("abp") var accountPrecision: Int
) {
    // Builder class for UserAccount
    data class UserAccountBuilder(
        @SerializedName("ano") var accountNo: Long = 0L,
        @SerializedName("atl") var accountTitle: String = "",
        @SerializedName("ast") var accountStatus: Int = 0,
        @SerializedName("pno") var phoneNumber: String = "",
        @SerializedName("cur") private var accountCurrency: String = "",
        @SerializedName("abp") private var accountPrecision: Int = 0
    ) {
        fun accountNo(accountId: Long) = apply { this.accountNo = accountId }
        fun accountTitle(accountTitle: String) = apply { this.accountTitle = accountTitle }
        fun accountStatus(accountStatus: Int) = apply { this.accountStatus = accountStatus }
        fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
        fun accountCurrency(accountCurrency: String) =
            apply { this.accountCurrency = accountCurrency }

        fun accountPrecision(accountPrecision: Int) =
            apply { this.accountPrecision = accountPrecision }

        fun build() = UserAccount(
            accountNo,
            accountTitle,
            accountStatus,
            phoneNumber,
            accountCurrency,
            accountPrecision
        )

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as UserAccountBuilder
            return true
        }

        override fun hashCode(): Int {
            var result = accountNo.hashCode()
            result = 31 * result + accountTitle.hashCode()
            result = 31 * result + accountStatus.hashCode()
            result = 31 * result + phoneNumber.hashCode()
            result = 31 * result + accountCurrency.hashCode()
            result = 31 * result + accountPrecision
            return result
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAccount
        return true
    }

    override fun hashCode(): Int {
        var result = accountNo.hashCode()
        result = 31 * result + accountTitle.hashCode()
        result = 31 * result + accountStatus.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + accountCurrency.hashCode()
        result = 31 * result + accountPrecision
        return result
    }
}
