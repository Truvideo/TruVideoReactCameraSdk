import { NativeModules, Platform } from 'react-native';
import type { CameraConfiguration } from './cameraConfigInterface';
import { LensFacing, FlashMode, Orientation, Mode } from './cameraConfigEnums';

const LINKING_ERROR =
  `The package 'truvideo-react-camera-sdk' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const TruVideoReactCameraSdk = NativeModules.TruVideoReactCameraSdk
  ? NativeModules.TruVideoReactCameraSdk
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );
/**
 * Initializes the camera screen with the given configuration.
 *
 * @param {CameraConfiguration} configuration - The configuration for the camera screen.
 * @return {Promise<string>} A promise that resolves to a string representing the result of the initialization.
 */
export function initCameraScreen(
  configuration: CameraConfiguration
): Promise<string> {
  return TruVideoReactCameraSdk.initCameraScreen(JSON.stringify(configuration));
}

export { LensFacing, FlashMode, Orientation, Mode };

export * from './cameraConfigInterface';
