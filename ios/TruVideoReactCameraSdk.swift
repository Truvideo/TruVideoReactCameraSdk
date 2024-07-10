import TruvideoSdk
import TruvideoSdkCamera
import Foundation
import CommonCrypto

@objc(TruVideoReactCameraSdk)
class TruVideoReactCameraSdk: NSObject {
    
//    let ffgdd: TruvideoSdkCameraResolution = .
    
    @objc(multiply:withB:withResolver:withRejecter:)
    func multiply(a: Float, b: Float, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) -> Void {
        resolve(a * b)
    }
    
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
               // self.authenticate()
                self.cameraInitiate(configuration: configuration) { cameraResult in
                    do {
                        
                        let cameraResultDict = cameraResult.toDictionary()
                        let mediaData = cameraResultDict["media"] as? [[String : Any]]
                        resolve(mediaData)
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
    
    func authenticate(){
           /// Verify if the user is already authenticated or if the session is active or expired..
           if(!Constant.isAuthenticated || Constant.isAuthenticationExpired){
               
               let payload = TruvideoSdk.generatePayload()
               /// The payload string is transformed into an encrypted string using the SHA256 algorithm.
               let signature = payload.toSha256String(using: Constant.secretKey)
               
               Task(operation: {
                   do{
                       ///   Initialize a session when the user is not authenticated.
                       /// - Parameters:
                       ///     - API_Key : Provided by TruVideo team
                       ///     - Payload : generated by sdk TruvideoSdk.generatePayload() every time you have to create new payload
                       ///     - Signature: encrypted string payload using the SHA256 algorithm with "secret key"
                       ///     - Secret_Key: secret key is also provided by TruVideo team
                       try await TruvideoSdk.authenticate(apiKey: Constant.apiKey, payload: payload, signature: signature)
                       try await TruvideoSdk.initAuthentication()
                   }catch {
                   }
               })
           }else{
               Task(operation: {
                   do{
                       /// Initialize a session when the user already authenticated.
                       try await TruvideoSdk.initAuthentication()
                   }catch {
                   }
               })
           }
       }
}

extension String {
    /// Calculates the HMAC-SHA256 value for a given message using a key.
    ///
    /// - Parameters:
    ///    - msg: The message for which the HMAC will be calculated.
    ///    - key: The secret key used to calculate the HMAC.
    /// - Returns: The calculated HMAC-SHA256 value in hexadecimal format.
    func toSha256String(using key: String) -> String {
        let hmac256 = CCHmacAlgorithm(kCCHmacAlgSHA256)
        var macData = Data(count: Int(CC_SHA256_DIGEST_LENGTH))
        
        key.withCString { keyCString in
            withCString { msgCString in
                macData.withUnsafeMutableBytes { macDataBytes in
                    guard let keyBytes = UnsafeRawPointer(keyCString)?.assumingMemoryBound(to: UInt8.self),
                          let msgBytes = UnsafeRawPointer(msgCString)?.assumingMemoryBound(to: UInt8.self) else {
                        return
                    }
                    
                    CCHmac(
                        hmac256,
                        keyBytes, Int(strlen(keyCString)),
                        msgBytes, Int(strlen(msgCString)),
                        macDataBytes.bindMemory(to: UInt8.self).baseAddress
                    )
                }
            }
        }
        
        return macData.map { String(format: "%02x", $0) }
            .joined()
    }
}
class Constant: NSObject{
   static let isAuthenticated = TruvideoSdk.isAuthenticated
   static let isAuthenticationExpired = TruvideoSdk.isAuthenticationExpired
   static let apiKey = "EPhPPsbv7e"
   static let secretKey = "9lHCnkfeLl"
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
            "resolution": resolution.resulDict(),
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
