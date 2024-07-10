package com.truvideoreactcamerasdk

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class TruVideoReactCameraSdkModule(reactContext: ReactApplicationContext)  :
  ReactContextBaseJavaModule(reactContext) {
  private val scope = CoroutineScope(Dispatchers.Main)
  override fun getName(): String {
    return NAME
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
//    promise.resolve(a * b)
  }


  @ReactMethod
  fun initCameraScreen(configuration:String,promise: Promise){
    Log.d("TAG", "initCameraScreen: $configuration")
    promise2 = promise
    currentActivity!!.startActivity(Intent(currentActivity, CameraActivity::class.java).putExtra("configuration",configuration))
  }



  companion object {
    const val NAME = "TruVideoReactCameraSdk"
    var promise2 : Promise? = null
  }
}
