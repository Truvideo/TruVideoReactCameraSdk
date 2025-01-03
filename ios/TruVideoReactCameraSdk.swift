import TruvideoSdkCamera
import Foundation

@objc(TruVideoReactCameraSdk)
class TruVideoReactCameraSdk: NSObject {
    
    @objc(initCameraScreen:withResolver:withRejecter:)
    func initCameraScreen(jsonData: String, resolve: @escaping RCTPromiseResolveBlock, reject: @escaping RCTPromiseRejectBlock) -> Void {
        print(jsonData)
        guard let data = jsonData.data(using: .utf8) else {
            print("Invalid JSON string")
            reject("json_error", "Invalid JSON string", nil)
            return
        }
        do {
            if let configuration = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any] {
                print(configuration)
                self.cameraInitiate(configuration: configuration) { cameraResult in
                    do {
                        let cameraResultDict = cameraResult.toDictionary()
                        if let mediaData = cameraResultDict["media"] as? [[String: Any]] {
                            var sanitizedMediaData: [[String: Any]] = []
                            
                            for item in mediaData {
                                var sanitizedItem: [String: Any] = [:]
                                for (key, value) in item {
                                    
                                    if key == "type" {
                                        if (value as AnyObject).description == "TruvideoSdkCamera.TruvideoSdkCameraMediaType.photo"  {
                                            sanitizedItem["type"] = (value as AnyObject).description
                                        } else {
                                            sanitizedItem["type"] = (value as AnyObject).description
                                        }
                                    }
                                    if JSONSerialization.isValidJSONObject([key: value]) {
                                        sanitizedItem[key] = value
                                    } else if let value = value as? CustomStringConvertible {
                                        sanitizedItem[key] = value.description
                                    } else {
                                        print("Skipping invalid JSON value for key: \(key)")
                                    }
                                }
                                sanitizedMediaData.append(sanitizedItem)
                            }
                            if let jsonData = try? JSONSerialization.data(withJSONObject: sanitizedMediaData, options: []) {
                                if let jsonString = String(data: jsonData, encoding: .utf8) {
                                    print(jsonString)
                                    resolve(jsonString)
                                }
                            }
                        }
                    }
                }
            } else {
                print("Invalid JSON format")
                reject("json_error", "Invalid JSON format", nil)
            }
        } catch {
            print("Error parsing JSON: \(error.localizedDescription)")
            reject("json_error", "Error parsing JSON", error)
        }
    }
    
    
    private func cameraInitiate(configuration: [String:Any], completion: @escaping (_ cameraResult: TruvideoSdkCameraResult) -> Void) {
        DispatchQueue.main.async {
            guard let rootViewController = UIApplication.shared.keyWindow?.rootViewController else {
                print("E_NO_ROOT_VIEW_CONTROLLER", "No root view controller found")
                return
            }
            guard let lensFacingString = configuration["lensFacing"] as? String,
                  let flashModeString = configuration["flashMode"] as? String,
                  let orientationString = configuration["orientation"] as? String,
                  let outputPath = configuration["outputPath"] as? String,
                  let modeString = configuration["mode"] as? String else {
                print("Error: Missing or invalid configuration values")
                return
            }
            // Retrieving information about the device's camera functionality.
            let cameraInfo: TruvideoSdkCameraInformation = TruvideoSdkCamera.camera.getTruvideoSdkCameraInformation()
            print("Camera Info:", cameraInfo)
            
            let lensType: TruvideoSdkCameraLensFacing = lensFacingString == "back" ? .back: .front
            
            let flashMode: TruvideoSdkCameraFlashMode = flashModeString == "on" ? .on: .off
            
            let orientation: TruvideoSdkCameraOrientation
            switch orientationString {
            case "portrait":
                orientation = .portrait
            case "portraitReverse":
                orientation = .portraitReverse
            case "landscapeLeft":
                orientation = .landscapeLeft
            case "landscapeRight":
                orientation = .landscapeRight
            default:
                print("Unknown orientation:", orientationString)
                return
            }
            
            let mode: TruvideoSdkCameraMode
            switch modeString {
            case "picture":
                mode = .picture
            case "video":
                mode = .video
            case "videoAndPicture":
                mode = .videoAndPicture
            default:
                print("Unknown mode:", modeString)
                return
            }
            
            // Configuring the camera with various parameters based on specific requirements.
            let configuration = TruvideoSdkCameraConfiguration(
                lensFacing: lensType,
                flashMode: flashMode,
                orientation: orientation,
                outputPath: outputPath,
                frontResolutions: [],
                frontResolution: nil,
                backResolutions: [],
                backResolution: nil,
                mode: mode
            )
            
            rootViewController.presentTruvideoSdkCameraView(
                preset: configuration,
                onComplete: { cameraResult in
                    // Handling completion of camera
                    completion(cameraResult)
                }
            )
        }
    }
    
}
extension TruvideoSdkCameraResult {
    func toDictionary() -> [String: Any] {
        return [
            "media": media.map { $0.toDictionary() }
        ]
    }
}

extension TruvideoSdkCamera.TruvideoSdkCameraMedia {
    func toDictionary() -> [String: Any] {
        return [
            "createdAt": createdAt,
            "filePath": filePath,
            "type": type,
            "cameraLensFacing": cameraLensFacing.rawValue,
            "rotation": rotation.rawValue,
            "resolution": resolution,
            "duration": duration
        ]
    }
    
    
}

extension TruvideoSdkCamera.TruvideoSdkCameraResolution {
    func toDictionary() -> [String: Any] {
        return [:]
    }
    
    func resulDict() -> [String: Any] {
        //width: Int32, height: Int32
        return [
            "width": 0,
            "height": 0
        ]
    }
}
