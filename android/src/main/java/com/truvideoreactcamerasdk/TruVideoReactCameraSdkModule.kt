package com.truvideoreactcamerasdk

import android.content.Intent
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

  // init camera screen
  @ReactMethod
  fun initCameraScreen(configuration:String,promise: Promise){
    promise2 = promise
    reactContext = reactApplicationContext
    currentActivity!!.startActivity(Intent(currentActivity, CameraActivity::class.java).putExtra("configuration",configuration))
  }

  companion object {
    lateinit var reactContext : ReactApplicationContext
    const val NAME = "TruVideoReactCameraSdk"
    var promise2 : Promise? = null
  }
}
