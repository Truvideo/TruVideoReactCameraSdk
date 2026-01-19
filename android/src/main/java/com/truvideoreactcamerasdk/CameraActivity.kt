package com.truvideoreactcamerasdk

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.gson.Gson
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.truvideo.sdk.camera.TruvideoSdkCamera
import com.truvideo.sdk.camera.model.external.TruvideoSdkCameraConfiguration
import com.truvideo.sdk.camera.model.external.TruvideoSdkCameraEvent
import com.truvideo.sdk.camera.model.external.TruvideoSdkCameraMode
import com.truvideo.sdk.camera.model.TruvideoSdkCameraFlashMode
import com.truvideo.sdk.camera.model.TruvideoSdkCameraImageFormat
import com.truvideo.sdk.camera.model.TruvideoSdkCameraLensFacing
import com.truvideo.sdk.camera.model.TruvideoSdkCameraOrientation
import com.truvideo.sdk.camera.model.TruvideoSdkCameraResolution
import com.truvideo.sdk.camera.ui.activities.camera.TruvideoSdkCameraContract
import org.json.JSONArray
import org.json.JSONObject



class CameraActivity : ComponentActivity() {



  var configuration = ""
  var lensFacing = TruvideoSdkCameraLensFacing.BACK
  var flashMode = TruvideoSdkCameraFlashMode.OFF
  var imageFormat = TruvideoSdkCameraImageFormat.JPEG
  var videoStabilizationEnabled = true
  var orientation: TruvideoSdkCameraOrientation? = null
  var mode: TruvideoSdkCameraMode = TruvideoSdkCameraMode.VideoAndImage()
  var frontResolutions : List<TruvideoSdkCameraResolution> = listOf()
  var frontResolution : TruvideoSdkCameraResolution? = null
  var backResolutions : List<TruvideoSdkCameraResolution> = listOf()
  var backResolution : TruvideoSdkCameraResolution? = null


//  var mode = TruvideoSdkCameraMode.videoAndPicture()
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//        val cameraScreen = registerForActivityResult(TruvideoSdkCameraContract()){
//          // result
//          val gson = Gson()
//          val jsonResult = gson.toJson(it)
//          TruVideoReactCameraSdkModule.promise2!!.resolve(jsonResult)
//          finish()
//        }
        getEvent()
        getIntentData()
        startCamera()
        //openCamera(this@CameraActivity,cameraScreen)
    }



  fun startCamera(){
    val cameraScreen = registerForActivityResult(TruvideoSdkCameraContract()){
      // result
      //val jsonArray = Json.encodeToString(ListSerializer(TruvideoSdkCameraMedia.serializer()),it)
      val jsonArray = JSONArray()
      it.forEach { media ->
        val resolutionObj = JSONObject().apply {
          put("width", media.resolution.width)
          put("height", media.resolution.height)
        }
        val obj = JSONObject().apply {
          put("id", media.id)
          put("createdAt", media.createdAt)
          put("filePath", media.filePath)
          put("type", media.type.name)          // enum as string
          put("lensFacing", media.lensFacing.name)
          put("orientation",media.orientation.name)
          put("resolution", resolutionObj)
          put("duration", media.duration)
        }
        jsonArray.put(obj)
      }
      TruVideoReactCameraSdkModule.promise2!!.resolve(jsonArray.toString())
      finish()
    }
    try{
      openCamera(this@CameraActivity,cameraScreen)
    }catch (e : Exception){
      TruVideoReactCameraSdkModule.promise2!!.reject("Exception",e.message)
      finish()
    }
  }
  fun getEvent(){
    TruvideoSdkCamera.events.onEach { event: TruvideoSdkCameraEvent ->
      val obj = JSONObject().apply {
        put("data", event.data.toString())
        put("type", event.eventType.name)
      }
      sendEvent(reactContext = TruVideoReactCameraSdkModule.reactContext, eventName = "cameraEvent", event = obj.toString())
    }.launchIn(lifecycleScope)
  }
  fun sendEvent(reactContext: ReactApplicationContext, eventName: String, event: String) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, event)
  }
  fun getIntentData(){
    configuration = intent.getStringExtra("configuration")!!
  }
  private fun openCamera(context: Context, cameraScreen: ActivityResultLauncher<TruvideoSdkCameraConfiguration>?) {
    // Start camera with configuration
    // if camera is not available, it will return null
    if (cameraScreen == null) return
    // Get camera information
    var outputPath = context.filesDir.path + "/camera"
    val jsonConfiguration = JSONObject(configuration)
    if(jsonConfiguration.has("outputPath")){
      val newOutputPath = jsonConfiguration.getString("outputPath")
      if(newOutputPath.isNotEmpty()){
        outputPath = context.filesDir.path + newOutputPath
      }
    }
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
      mode = mode,
      imageFormat = imageFormat,
      videoStabilizationEnabled = videoStabilizationEnabled
    )

    cameraScreen.launch(configuration)

  }

  // Single Resolution Parser
  fun parseResolution(obj: JSONObject): TruvideoSdkCameraResolution {
    val width = obj.optInt("width", 0)
    val height = obj.optInt("height", 0)
    return TruvideoSdkCameraResolution(width, height) // Assume Resolution(width, height) is your model
  }

  // Array of Resolutions
  fun parseResolutions(array: JSONArray): List<TruvideoSdkCameraResolution> {
    val list = mutableListOf<TruvideoSdkCameraResolution>()
    for (i in 0 until array.length()) {
      val resObj = array.getJSONObject(i)
      list.add(parseResolution(resObj))
    }
    return list
  }

  private fun checkConfigure() {
    val jsonConfiguration = JSONObject(configuration)
    if (jsonConfiguration.has("lensFacing")) {
      when (jsonConfiguration.getString("lensFacing")) {
        "BACK" -> lensFacing = TruvideoSdkCameraLensFacing.BACK
        "FRONT" -> lensFacing = TruvideoSdkCameraLensFacing.FRONT
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
        "PORTRAIT" -> orientation = TruvideoSdkCameraOrientation.PORTRAIT
        "LANDSCAPE_LEFT" -> orientation = TruvideoSdkCameraOrientation.LANDSCAPE_LEFT
        "LANDSCAPE_RIGHT" -> orientation = TruvideoSdkCameraOrientation.LANDSCAPE_RIGHT
        "PORTRAIT_REVERSE" -> orientation = TruvideoSdkCameraOrientation.PORTRAIT_REVERSE
      }
    }

    if(jsonConfiguration.has("imageFormat")) {
      when(jsonConfiguration.getString("imageFormat")){
        "jpeg" -> imageFormat = TruvideoSdkCameraImageFormat.JPEG
        "png" -> imageFormat = TruvideoSdkCameraImageFormat.PNG
      }
    }

    if(jsonConfiguration.has("videoStabilizationEnabled")) {
      when(jsonConfiguration.getString("videoStabilizationEnabled")){
        "true" -> videoStabilizationEnabled = true
        "false" -> videoStabilizationEnabled = false
      }
    }

    // Front Resolutions
    if (jsonConfiguration.has("frontResolutions") && jsonConfiguration.getString("frontResolutions") != "") {
      frontResolutions = parseResolutions(jsonConfiguration.getJSONArray("frontResolutions"))
    }
    if (jsonConfiguration.has("frontResolution") && jsonConfiguration.getString("frontResolution") != "") {
      frontResolution = parseResolution(jsonConfiguration.getJSONObject("frontResolution"))
    }

    // Back Resolutions
    if (jsonConfiguration.has("backResolutions") && jsonConfiguration.getString("backResolutions") != "") {
      backResolutions = parseResolutions(jsonConfiguration.getJSONArray("backResolutions"))
    }
    if (jsonConfiguration.has("backResolution") && jsonConfiguration.getString("backResolution") != "") {
      backResolution = parseResolution(jsonConfiguration.getJSONObject("backResolution"))
    }


    if(jsonConfiguration.has("mode")){
      val jsonMode = JSONObject(jsonConfiguration.getString("mode"))
      val videoDurationLimit : String? = if(jsonMode.getString("videoDurationLimit") != "" ) jsonMode.getString("videoDurationLimit") else null
      val mediaLimit : String? = if(jsonMode.getString("mediaLimit") != "" ) jsonMode.getString("mediaLimit") else null
      val videoLimit : String? = if(jsonMode.getString("videoLimit") != "" ) jsonMode.getString("videoLimit") else null
      val imageLimit : String? = if(jsonMode.getString("imageLimit") != "" ) jsonMode.getString("imageLimit") else null
      when(jsonMode.getString("mode")) {
        "videoAndImage" -> {
          val limit = when {
            imageLimit != null || videoLimit != null -> {
              TruvideoSdkCameraMode.VideoAndImage.Limit.ByType(
                maxVideoCount = videoLimit?.toInt(),
                maxImageCount = imageLimit?.toInt()
              )
            }
            mediaLimit != null -> {
              TruvideoSdkCameraMode.VideoAndImage.Limit.ByTotal(
                maxMediaCount = mediaLimit.toInt()
              )
            }
            else -> null
          }
          mode = TruvideoSdkCameraMode.VideoAndImage(
            limit = limit,
            videoDurationLimit = videoDurationLimit?.toLong()
          )
        }
        "video" -> {
          mode = TruvideoSdkCameraMode.Video(
            maxCount = videoLimit?.toInt(),
            durationLimit = videoDurationLimit?.toLong()
          )
        }
        "image" -> {
          mode = TruvideoSdkCameraMode.Image(
            maxCount = imageLimit?.toInt()
          )
        }
        "singleImage" ->{
          mode = TruvideoSdkCameraMode.SingleImage()
        }
        "singleVideo" ->{
          mode = TruvideoSdkCameraMode.SingleVideo(
            durationLimit = videoDurationLimit?.toLong()
          )
        }
        "singleVideoOrImage" -> {
          mode = TruvideoSdkCameraMode.SingleVideoOrImage(
            videoDurationLimit = videoDurationLimit?.toLong()
          )
        }
      }
    }
  }



}
