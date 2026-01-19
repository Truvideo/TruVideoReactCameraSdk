package com.truvideoreactcamerasdk

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.truvideo.sdk.camera.TruvideoSdkCamera
import com.truvideo.sdk.camera.model.TruvideoSdkArCameraConfiguration
import com.truvideo.sdk.camera.model.external.TruvideoSdkCameraMode
import com.truvideo.sdk.camera.model.TruvideoSdkCameraOrientation
import com.truvideo.sdk.camera.ui.activities.arcamera.TruvideoSdkArCameraContract
import org.json.JSONArray
import org.json.JSONObject

class ArCameraActivity : AppCompatActivity() {

  lateinit var launcher : ActivityResultLauncher<TruvideoSdkArCameraConfiguration>
  var orientation: TruvideoSdkCameraOrientation? = null
  var mode: TruvideoSdkCameraMode = TruvideoSdkCameraMode.VideoAndImage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ar_camera)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

      val configuration = intent.getStringExtra("configuration")!!
      launcher = registerForActivityResult(TruvideoSdkArCameraContract()){
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
      try {
        if(TruvideoSdkCamera.isAugmentedRealityInstalled && TruvideoSdkCamera.isAugmentedRealitySupported){
          openArCamera(configuration)
        }else if(TruvideoSdkCamera.isAugmentedRealitySupported){
          TruVideoReactCameraSdkModule.promise2!!.reject("Exception","Ar Not Supported in Device")
        }else{
          TruVideoReactCameraSdkModule.promise2!!.reject("Exception","Ar Core App not Installed")
        }
      }catch (e : Exception){
        TruVideoReactCameraSdkModule.promise2!!.reject("Exception",e.message)
        finish()
      }
    }

  fun openArCamera(configuration: String){
    val jsonConfiguration = JSONObject(configuration)
    var outputPath = filesDir.path + "/camera"
    if(jsonConfiguration.has("outputPath")){
      val newOutputPath = jsonConfiguration.getString("outputPath")
      if(newOutputPath.isNotEmpty()){
        outputPath = newOutputPath
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
    val configuration = TruvideoSdkArCameraConfiguration(
      orientation = orientation,
      outputPath = outputPath,
      mode = mode
    )
    launcher.launch(configuration)
  }
}
