package android.app.faunadex.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.ar.core.ArCoreApk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Helper class to check ARCore availability and request installation.
 * Note: ArSceneView manages its own ARCore session internally,
 * so this class only handles availability checking and install requests.
 */
class ArCoreSessionManager(private val context: Context) {

    private var installRequested = false

    suspend fun checkArCoreAvailability(): ArCoreStatus = withContext(Dispatchers.Main) {
        try {
            val arCoreApk = ArCoreApk.getInstance()
            var availability = arCoreApk.checkAvailability(context)
            var retries = 0

            // Emulator checks are often transient right after boot; poll briefly before failing.
            while (availability.isTransient && retries < 8) {
                delay(250L)
                availability = arCoreApk.checkAvailability(context)
                retries++
            }

            Log.d(TAG, "Availability=$availability retries=$retries model=${Build.MODEL} fp=${Build.FINGERPRINT}")

            when (availability) {
                ArCoreApk.Availability.SUPPORTED_INSTALLED -> ArCoreStatus.SUPPORTED
                ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD,
                ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED -> ArCoreStatus.NOT_INSTALLED
                ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE -> ArCoreStatus.UNSUPPORTED
                else -> ArCoreStatus.UNKNOWN
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking ARCore availability", e)
            ArCoreStatus.ERROR
        }
    }

    fun requestInstall(activity: Activity): Boolean {
        return try {
            when (ArCoreApk.getInstance().requestInstall(activity, !installRequested)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    installRequested = true
                    false
                }
                ArCoreApk.InstallStatus.INSTALLED -> {
                    true
                }
                else -> false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting ARCore install", e)
            false
        }
    }


    companion object {
        private const val TAG = "ArCoreSessionManager"
    }
}

enum class ArCoreStatus {
    SUPPORTED,
    NOT_INSTALLED,
    UNSUPPORTED,
    UNKNOWN,
    ERROR
}
