package com.creativechaos.storedvaluewallet.presentation

import android.util.Log
import com.creativechaos.storedvaluewallet.presentation.StoreValueWallet.Companion.objKEY_2TDEA
import com.nxp.nfclib.KeyType
import com.nxp.nfclib.desfire.EV1ApplicationKeySettings
import com.nxp.nfclib.desfire.IDESFireEV1
import com.nxp.nfclib.utils.Utilities

/**
 * This class manages the operations related to the DESFire card.
 *
 * @param desFireEV1 The DESFire EV1 instance.
 */
class CardManager(private val desFireEV1: IDESFireEV1) {

    private val TAG = "CardManager"

    /**
     * Performs initial checks and authentication for the card.
     */
    fun performInitialChecks() {
        try {
            val version = desFireEV1.version

            if (version[0] != 0x04.toByte()) {
                Log.i(TAG, "Card is not NXP")
            }

            if (version[6] == 0x05.toByte()) {
                //            Log.i(MainActivity.TAG, "Card uses ISO/IEC 14443–4 protocol")
            } else {
                Log.i(TAG, "Unknown card protocol")
            }

            selectApplicationByIndex(0)
            authenticateToApplication()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Selects the application by its AID (Application ID).
     *
     * @param appAID The application AID.
     */
    fun selectApplicationByAID(appAID: ByteArray) {
        try {
            desFireEV1.selectApplication(appAID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Selects the application by its index.
     *
     * @param appIndex The application index.
     */
    private fun selectApplicationByIndex(appIndex: Int = 0) {
        try {
            desFireEV1.selectApplication(appIndex)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun authenticateToApplication(ACCESS_KEY: Int = 0) {
        try {
            Log.d("Authentication", "ACCESS_KEY: $ACCESS_KEY")

            desFireEV1.authenticate(
                ACCESS_KEY,
                IDESFireEV1.AuthType.Native,
                KeyType.THREEDES,
                objKEY_2TDEA
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * Checks if the application with the specified AID exists.
     *
     * @param appAID The application AID to check.
     * @return True if the application exists, false otherwise.
     */
    fun checkApplicationExists(appAID: ByteArray): Boolean {
        try {
            val appIds = desFireEV1.applicationIDs
            for (appId in appIds) {
                val ids: ByteArray = Utilities.intToBytes(appId, 3)
                val str: String = Utilities.byteToHexString(ids)
                if (str.equals(Utilities.byteToHexString(appAID), ignoreCase = true)) {
                    return true // Application with targetAID exists
                }
            }
            return false // Application with targetAID does not exist
        } catch (e: Exception) {
            return false
        }
    }

    fun checkAllApplications(): IntArray? {
        return try {
            desFireEV1.applicationIDs
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Creates a new application with the specified AID (Application ID).
     *
     * @param appAID The application AID.
     */
    fun createApplication(appAID: ByteArray) {
        try {
            val appSetting = EV1ApplicationKeySettings.Builder()
                .setMaxNumberOfApplicationKeys(10)
                .setAppKeySettingsChangeable(true)
                .setAuthenticationRequiredForDirectoryConfigurationData(false)
                .setAuthenticationRequiredForFileManagement(false)
                .setAppMasterKeyChangeable(true)
                .build()

            desFireEV1.createApplication(appAID, appSetting)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
