package com.truvideoreactcamerasdk

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.truvideo.sdk.camera.TruvideoSdkCamera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.IOException
import java.util.Properties

class TruVideoReactCameraSdkModule(reactContext: ReactApplicationContext)  :
  ReactContextBaseJavaModule(reactContext) {
  private val scope = CoroutineScope(Dispatchers.Main)
  override fun getName(): String {
    return NAME
  }



  companion object {
    private const val VERSION_CAMERA_ASSET = "version-camera.properties"  // add this line
    lateinit var reactContext : ReactApplicationContext
    const val NAME = "TruVideoReactCameraSdk"
    var promise2 : Promise? = null
  }



//  @ReactMethod
//  fun version(promise: Promise){
//    promise.resolve(TruvideoSdkCamera.version)
//  }
//
//  @ReactMethod
//  fun environment(promise: Promise){
//    promise.resolve(TruvideoSdkCamera.environment)
//  }

  // ✅ Fix: Read from properties file instead of TruvideoSdkCamera.version
  private fun readSdkCameraManifestProperty(key: String): String? =
    try {
      Properties().apply {
        reactApplicationContext.assets.open(VERSION_CAMERA_ASSET).use { load(it) }
      }.getProperty(key)
    } catch (_: IOException) {
      null
    }

  @ReactMethod
  fun version(promise: Promise) {
    promise.resolve(readSdkCameraManifestProperty("versionName").orEmpty())
  }

  @ReactMethod
  fun environment(promise: Promise) {
    promise.resolve(readSdkCameraManifestProperty("environment").orEmpty())
  }


  @ReactMethod
  fun isAugmentedRealityInstalled(promise: Promise){
    promise.resolve(TruvideoSdkCamera.isAugmentedRealityInstalled)
  }

  @ReactMethod
  fun isAugmentedRealitySupported(promise: Promise){
    promise.resolve(TruvideoSdkCamera.isAugmentedRealitySupported)
  }

  @ReactMethod
    fun requestInstallAugmentedReality(promise: Promise?) {
        val activity = reactApplicationContext.currentActivity
        if (activity != null) {
            TruvideoSdkCamera.requestInstallAugmentedReality(activity)
            promise?.resolve(true)
        } else {
            promise?.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
        }
    }


  @ReactMethod
  fun initCameraScreen(configuration:String,promise: Promise){
    Log.d("initCameraScreen","initCameraScreen")
    promise2 = promise
    reactContext = reactApplicationContext
    Log.d("initCameraScreen", configuration)
    val activity = reactApplicationContext.currentActivity
    if (activity != null) {
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra("configuration", configuration)
        activity.startActivity(intent)
    } else {
        promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
    }
  }

  @ReactMethod
  fun initARCameraScreen(configuration:String,promise: Promise){
    Log.d("initCameraScreen","initCameraScreen")
    promise2 = promise
    reactContext = reactApplicationContext
    Log.d("initCameraScreen", configuration)
    val activity = reactApplicationContext.currentActivity
    if (activity != null) {
        val intent = Intent(activity, ArCameraActivity::class.java)
        intent.putExtra("configuration", configuration)
        activity.startActivity(intent)
    } else {
        promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
    }
  }

  @ReactMethod
  fun initScanerScreen(configuration:String,promise: Promise){
    Log.d("initCameraScreen","initCameraScreen")
    promise2 = promise
    reactContext = reactApplicationContext
    val activity = reactApplicationContext.currentActivity
    if (activity != null) {
        val intent = Intent(activity, ScannerActivity::class.java)
        activity.startActivity(intent)
    } else {
        promise.reject("E_ACTIVITY_DOES_NOT_EXIST", "Activity doesn't exist")
    }
  }
}
