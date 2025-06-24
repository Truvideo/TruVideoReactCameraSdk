import { NativeModules, Platform } from 'react-native';
import type { CameraConfiguration, CameraMode } from './cameraConfigInterface';
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
  let data = {
    mode: configuration.mode.mode,
    videoLimit: configuration.mode.videoLimit,
    imageLimit: configuration.mode.imageLimit,
    mediaLimit: configuration.mode.mediaLimit,
    videoDurationLimit: configuration.mode.videoDurationLimit,
    autoClose: configuration.mode.autoClose,
  };
  let cameraConfiguration: CameraConfiguration = {
    lensFacing: configuration.lensFacing,
    flashMode: configuration.flashMode,
    orientation: configuration.orientation,
    outputPath: configuration.outputPath,
    frontResolutions: configuration.frontResolutions,
    frontResolution: configuration.frontResolution,
    backResolutions: configuration.backResolutions,
    backResolution: configuration.backResolution,
    mode: JSON.stringify(data) as any,
  }
  return TruVideoReactCameraSdk.initCameraScreen(
    JSON.stringify(cameraConfiguration)
  );
}

export function initARCameraScreen(
  configuration: CameraConfiguration
): Promise<string> {
  return TruVideoReactCameraSdk.initARCameraScreen(
    JSON.stringify(configuration)
  );
}
export function initScanerScreen(
  configuration: CameraConfiguration
): Promise<string> {
  return TruVideoReactCameraSdk.initScanerScreen(
    JSON.stringify(configuration)
  );
}

export function version(): Promise<string> {
  return TruVideoReactCameraSdk.version();
}

export function environment(): Promise<string> {
  return TruVideoReactCameraSdk.environment();
}
export function isAugmentedRealityInstalled(): Promise<string> {
  return TruVideoReactCameraSdk.isAugmentedRealityInstalled();
}
export function isAugmentedRealitySupported(): Promise<string> {
  return TruVideoReactCameraSdk.isAugmentedRealitySupported();
}

export function requestInstallAugmentedReality(): Promise<string> {
  return TruVideoReactCameraSdk.requestInstallAugmentedReality();
}


function createCameraMode(
  mode: string,
  videoLimit: number | null,
  imageLimit: number | null,
  mediaLimit: number | null,
  videoDurationLimit: number | null,
  autoClose: boolean
): CameraMode {
  return {
    mode,
    videoLimit: videoLimit != null ? videoLimit.toString() : "",
    imageLimit: imageLimit != null ? imageLimit.toString() : "",
    mediaLimit: mediaLimit != null ? mediaLimit.toString() : "",
    videoDurationLimit: videoDurationLimit != null ? videoDurationLimit.toString() : "",
    autoClose,
  };
}

// Factory Functions (replacing static methods)

export function singleMedia(durationLimit?: number, mediaCount?: number): CameraMode {
  return createCameraMode("singleMedia", null, null, mediaCount ?? null, durationLimit ?? null, false);
}

export function videoAndImage(
  durationLimit?: number,
  videoMaxCount?: number,
  imageMaxCount?: number
): CameraMode {
  return createCameraMode("videoAndImage", videoMaxCount ?? null, imageMaxCount ?? null, null, durationLimit ?? null, false);
}

export function singleVideo(durationLimit?: number): CameraMode {
  return createCameraMode("singleVideo", 1, 0, null, durationLimit ?? null, true);
}

export function singleImage(): CameraMode {
  return createCameraMode("singleImage", 0, 1, null, null, true);
}

export function singleVideoOrImage(durationLimit?: number): CameraMode {
  return createCameraMode("singleVideoOrImage", null, null, 1, durationLimit ?? null, true);
}

export function video(videoMaxCount?: number, durationLimit?: number): CameraMode {
  return createCameraMode("video", videoMaxCount ?? null, 0, null, durationLimit ?? null, false);
}

export function image(imageMaxCount?: number): CameraMode {
  return createCameraMode("image", 0, imageMaxCount ?? null, null, null, false);
}

export { LensFacing, FlashMode, Orientation, Mode };

export * from './cameraConfigInterface';
