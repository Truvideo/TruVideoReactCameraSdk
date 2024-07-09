package com.truvideoreactcamerasdk

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.truvideo.sdk.camera.TruvideoSdkCamera
import com.truvideo.sdk.camera.model.TruvideoSdkCameraConfiguration
import com.truvideo.sdk.camera.model.TruvideoSdkCameraFlashMode
import com.truvideo.sdk.camera.model.TruvideoSdkCameraLensFacing
import com.truvideo.sdk.camera.model.TruvideoSdkCameraMode
import com.truvideo.sdk.camera.model.TruvideoSdkCameraOrientation
import com.truvideo.sdk.camera.model.TruvideoSdkCameraResolution
import com.truvideo.sdk.camera.usecase.TruvideoSdkCameraScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class CameraActivity : ComponentActivity() {
  var configuration = ""
  var lensFacing = TruvideoSdkCameraLensFacing.BACK
  var flashMode = TruvideoSdkCameraFlashMode.OFF
  var orientation: TruvideoSdkCameraOrientation? = null
  var mode = TruvideoSdkCameraMode.VIDEO_AND_PICTURE
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cameraScreen = TruvideoSdkCamera.initCameraScreen(this)
        getIntentData()
        CoroutineScope(Dispatchers.Main).launch {
          openCamera(this@CameraActivity,cameraScreen)
        }


    }
    fun getIntentData(){
        configuration = intent.getStringExtra("configuration")!!
    }
    suspend fun openCamera(context: Context, cameraScreen: TruvideoSdkCameraScreen?) {
      // Start camera with configuration
      // if camera is not available, it will return null
      if (cameraScreen == null) return
      // Get camera information
      val cameraInfo = TruvideoSdkCamera.getInformation()

      var outputPath = context.filesDir.path + "/camera"
      val jsonConfiguration = JSONObject(configuration)
      if(jsonConfiguration.has("outputPath")){
        val newOutputPath = jsonConfiguration.getString("outputPath")
        if(newOutputPath.isNotEmpty()){
          outputPath = newOutputPath
        }
      }
      var frontResolutions: List<TruvideoSdkCameraResolution> = ArrayList()
      if (cameraInfo.frontCamera != null) {
        // if you don't want to decide the list of allowed resolutions, you can s1end all the resolutions or an empty list
        frontResolutions = cameraInfo.frontCamera!!.resolutions
      }


      // You can decide the default resolution for the front camera
      var frontResolution: TruvideoSdkCameraResolution? = null
      if (cameraInfo.frontCamera != null) {
        // Example of how tho pick the first resolution as the default one
        val resolutions = cameraInfo.frontCamera!!.resolutions
        if (resolutions.isNotEmpty()) {
          frontResolution = resolutions[0]
        }
      }
      val backResolutions: List<TruvideoSdkCameraResolution> = ArrayList()
      val backResolution: TruvideoSdkCameraResolution? = null
      checkConfigure()
      val configuration = TruvideoSdkCameraConfiguration(
        lensFacing = lensFacing,
        flashMode = flashMode,
        orientation = orientation,
        outputPath = outputPath,
        frontResolutions = frontResolutions,
        frontResolution = frontResolution,
        backResolutions = backResolutions,
        backResolution = backResolution,
        mode = mode
      )

      val result = cameraScreen?.open(configuration)
      val gson = Gson()
      val jsonResult = gson.toJson(result)
      TruVideoReactCameraSdkModule.promise2!!.resolve(jsonResult)
      finish()
    }

    fun checkConfigure() {
      val jsonConfiguration = JSONObject(configuration)
      if (jsonConfiguration.has("lensFacing")) {
        when (jsonConfiguration.getString("lensFacing")) {
          "back" -> lensFacing = TruvideoSdkCameraLensFacing.BACK
          "front" -> lensFacing = TruvideoSdkCameraLensFacing.FRONT
        }
      }
      if(jsonConfiguration.has("flashMode")) {
        when (jsonConfiguration.getString("flashMode")) {
          "on" -> flashMode = TruvideoSdkCameraFlashMode.ON
          "off" -> flashMode = TruvideoSdkCameraFlashMode.OFF

        }
      }
      if(jsonConfiguration.has("orientation")) {
        when(jsonConfiguration.getString("orientation")){
          "portrait" -> orientation = TruvideoSdkCameraOrientation.PORTRAIT
          "landscapeLeft" -> orientation = TruvideoSdkCameraOrientation.LANDSCAPE_LEFT
          "landscapeRight" -> orientation = TruvideoSdkCameraOrientation.LANDSCAPE_RIGHT
          "portraitReverse" -> orientation = TruvideoSdkCameraOrientation.PORTRAIT_REVERSE
        }
      }
      if(jsonConfiguration.has("mode")){
        when(jsonConfiguration.getString("mode")) {
          "videoAndPicture" -> mode = TruvideoSdkCameraMode.VIDEO_AND_PICTURE
          "video" -> mode = TruvideoSdkCameraMode.VIDEO
          "picture" -> mode = TruvideoSdkCameraMode.PICTURE
        }
      }
    }

}
