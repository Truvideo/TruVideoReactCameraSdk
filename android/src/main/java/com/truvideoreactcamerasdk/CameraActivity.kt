package com.truvideoreactcamerasdk

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class CameraActivity : ComponentActivity() {
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
        CoroutineScope(Dispatchers.Main).launch {
          openCamera(this@CameraActivity,cameraScreen)
        }


    }
    suspend fun openCamera(context: Context, cameraScreen: TruvideoSdkCameraScreen?) {
      // Start camera with configuration
      // if camera is not available, it will return null
      if (cameraScreen == null) return
      // Get camera information
      val cameraInfo = TruvideoSdkCamera.getInformation()
      // you can choose the default camera lens facing
      // options: Back, Front
      val lensFacing = TruvideoSdkCameraLensFacing.BACK
      // TruvideoSdkCameraLensFacing lensFacing = TruvideoSdkCameraLensFacing.FRONT;

      // You can choose if the flash is on or off by default
      val flashMode = TruvideoSdkCameraFlashMode.OFF
      // val flashMode = TruvideoSdkCameraFlashMode.ON

      // You can choose the camera orientation
      // Options: null, Portrait, LandscapeLeft, LandscapeRight, PortraitReverse
      // Null means any orientation
      val orientation: TruvideoSdkCameraOrientation? = null
      // TruvideoSdkCameraOrientation orientation = TruvideoSdkCameraOrientation.PORTRAIT;
      // TruvideoSdkCameraOrientation orientation = TruvideoSdkCameraOrientation.LANDSCAPE_LEFT;
      // TruvideoSdkCameraOrientation orientation = TruvideoSdkCameraOrientation.LANDSCAPE_RIGHT;
      // TruvideoSdkCameraOrientation orientation = TruvideoSdkCameraOrientation.PORTRAIT_REVERSE;

      // You can choose where the files will be saved
      val outputPath = context.filesDir.path + "/camera"

      // You can decide the list of allowed resolutions for the front camera
      // if you send an empty list, all the resolutions are allowed
      var frontResolutions: List<TruvideoSdkCameraResolution> = ArrayList()
      if (cameraInfo.frontCamera != null) {
        // if you don't want to decide the list of allowed resolutions, you can send all the resolutions or an empty list
        frontResolutions = cameraInfo.frontCamera!!.resolutions

        //frontResolutions = new ArrayList<>();

        // Example of how to allow only the one resolution
        // List<TruvideoSdkCameraResolution> resolutions = new ArrayList<>();
        // resolutions.add(cameraInfo.getFrontCamera().getResolutions().get(0));
        // frontResolutions = resolutions;
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


      // You can decide the mode of the camera
      // Options: video and picture, video, picture
      val mode = TruvideoSdkCameraMode.VIDEO_AND_PICTURE

      // TruvideoSdkCameraMode mode = TruvideoSdkCameraMode.VIDEO;
      // TruvideoSdkCameraMode mode = TruvideoSdkCameraMode.PICTURE;
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
      Log.d("TAG", "openCamera: $result")
      TruVideoReactCameraSdkModule.promise2!!.resolve(result.toString())
      finish()
    }

}
