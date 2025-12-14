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

class TruVideoReactCameraSdkModule(reactContext: ReactApplicationContext)  :
  ReactContextBaseJavaModule(reactContext) {
  private val scope = CoroutineScope(Dispatchers.Main)
  override fun getName(): String {
    return NAME
  }



  companion object {
    lateinit var reactContext : ReactApplicationContext
    const val NAME = "TruVideoReactCameraSdk"
    var promise2 : Promise? = null
  }



  @ReactMethod
  fun version(promise: Promise){
    promise.resolve(TruvideoSdkCamera.version)
  }

  @ReactMethod
  fun environment(promise: Promise){
    promise.resolve(TruvideoSdkCamera.environment)
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
