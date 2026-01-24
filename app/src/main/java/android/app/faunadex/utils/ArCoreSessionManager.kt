package android.app.faunadex.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.*

class ArCoreSessionManager(private val context: Context) {

    private var arSession: Session? = null
    private var installRequested = false

    fun checkArCoreAvailability(): ArCoreStatus {
        return try {
            when (ArCoreApk.getInstance().checkAvailability(context)) {
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

    fun createSession(): Session? {
        if (arSession != null) {
            return arSession
        }

        return try {
            val session = Session(context)

            val config = Config(session).apply {
                planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL

                if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                    depthMode = Config.DepthMode.AUTOMATIC
                }

                updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

                lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            }

            session.configure(config)
            arSession = session

            Log.d(TAG, "ARCore session created successfully")
            session
        } catch (e: UnavailableArcoreNotInstalledException) {
            Log.e(TAG, "ARCore not installed", e)
            null
        } catch (e: UnavailableApkTooOldException) {
            Log.e(TAG, "ARCore APK too old", e)
            null
        } catch (e: UnavailableSdkTooOldException) {
            Log.e(TAG, "SDK too old for ARCore", e)
            null
        } catch (e: UnavailableDeviceNotCompatibleException) {
            Log.e(TAG, "Device not compatible with ARCore", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating ARCore session", e)
            null
        }
    }

    fun pause() {
        arSession?.pause()
        Log.d(TAG, "ARCore session paused")
    }

    fun resume() {
        try {
            arSession?.resume()
            Log.d(TAG, "ARCore session resumed")
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming ARCore session", e)
        }
    }

    fun close() {
        arSession?.close()
        arSession = null
        Log.d(TAG, "ARCore session closed")
    }

    fun getSession(): Session? = arSession

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
