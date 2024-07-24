#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(TruVideoReactCameraSdk, NSObject)

RCT_EXTERN_METHOD(initCameraScreen:(NSString)jsonData
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
                 
+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
