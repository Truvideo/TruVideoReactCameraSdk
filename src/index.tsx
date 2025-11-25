import { DeviceEventEmitter, NativeModules, Platform, type EmitterSubscription } from 'react-native';
//import type { CameraConfiguration } from './cameraConfigInterface';
//import { LensFacing, FlashMode, Orientation, Mode } from './cameraConfigEnums';

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


//export { LensFacing, FlashMode, Orientation, Mode };

interface Configuration {
  lensFacing: LensFacing;
  flashMode: FlashMode;
  orientation: Orientation;
  outputPath: string;
  frontResolutions: Resolution[] | string;
  frontResolution: Resolution | string;
  backResolutions: Resolution[] | string;
  backResolution: Resolution | string;
  mode: string;
  imageFormat: String;
}
interface ARConfiguration {
  outputPath: string;
  orientation: Orientation;
  mode: string;
}

export enum CameraMediaType {
  image = 'IMAGE',
  video = 'VIDEO',
}

export interface CameraResult {
  id: string;
  createdAt: number;
  filePath: string;
  type: CameraMediaType;
  lensFacing: LensFacing;
  orientation: Orientation;
  resolution: Resolution;
  duration: number;
}

export async function initCameraScreen(
  configuration: CameraConfiguration
): Promise<CameraResult[] | null> {
  let data = {
    mode: configuration.mode.mode,
    videoLimit: configuration.mode.videoLimit,
    imageLimit: configuration.mode.imageLimit,
    mediaLimit: configuration.mode.mediaLimit,
    videoDurationLimit: configuration.mode.videoDurationLimit,
    autoClose: configuration.mode.autoClose,
  };
  var cameraConfiguration: Configuration = {
    lensFacing: configuration.lensFacing,
    flashMode: configuration.flashMode,
    orientation: configuration.orientation,
    outputPath: configuration.outputPath,
    frontResolutions: configuration.frontResolutions != null ? configuration.frontResolutions : "",
    frontResolution: configuration.frontResolution != null ? configuration.frontResolution : "",
    backResolutions: configuration.backResolutions != null ? configuration.backResolutions : "",
    backResolution: configuration.backResolution != null ? configuration.backResolution : "",
    mode: JSON.stringify(data),
    imageFormat: configuration.imageFormat != null ? configuration.imageFormat : ImageFormat.JPEG,
  }
  return TruVideoReactCameraSdk.initCameraScreen(
    JSON.stringify(cameraConfiguration)
  ).then((response: string) => {
    try {
      const parsed: CameraResult[] = JSON.parse(response);
      return parsed;
    } catch (e) {
      console.error("Failed to parse MediaData JSON:", e);
      return null;
    }
  });
}

export function initARCameraScreen(
  configuration: ARCameraConfiguration
): Promise<string> {
  let data = {
    mode: configuration.mode.mode,
    videoLimit: configuration.mode.videoLimit,
    imageLimit: configuration.mode.imageLimit,
    mediaLimit: configuration.mode.mediaLimit,
    videoDurationLimit: configuration.mode.videoDurationLimit,
    autoClose: configuration.mode.autoClose,
  };
  var cameraConfiguration: ARConfiguration = {
    outputPath: configuration.outputPath,
    orientation: configuration.orientation,
    mode: JSON.stringify(data),
  }
  return TruVideoReactCameraSdk.initARCameraScreen(
    JSON.stringify(cameraConfiguration)
  );
}
export function initScanerScreen(): Promise<string> {
  return TruVideoReactCameraSdk.initScanerScreen(
    JSON.stringify("")
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

export enum LensFacing {
  Back = 'BACK',
  Front = 'FRONT',
}

export enum FlashMode {
  Off = 'off',
  On = 'on',
}

export enum Orientation {
  Portrait = 'PORTRAIT',
  LandscapeLeft = 'LANDSCAPE_LEFT',
  LandscapeRight = 'LANDSCAPE_RIGHT',
  PortraitReverse = 'PORTRAIT_REVERSE',
}

export interface Resolution {
  width: number;
  height: number;
}
export enum ImageFormat {
  JPEG = 'jpeg',
  PNG = 'png'
}

export interface CameraConfiguration {
  lensFacing: LensFacing;
  flashMode: FlashMode;
  orientation: Orientation;
  outputPath: string;
  frontResolutions: Resolution[] | null;
  frontResolution: Resolution | null;
  backResolutions: Resolution[] | null;
  backResolution: Resolution | null;
  mode: CameraMode;
  imageFormat?: ImageFormat;
}
export interface ARCameraConfiguration {
  outputPath: string;
  orientation: Orientation;
  mode: CameraMode;
}

export class CameraMode {
  videoLimit: string = "";
  imageLimit: string = "";
  mediaLimit: string = "";
  mode: string = 'videoAndImage';
  videoDurationLimit: string = "";
  autoClose: boolean = false;
  private constructor(
    mode: string,
    videoLimit: number | null,
    imageLimit: number | null,
    mediaLimit: number | null,
    videoDurationLimit: number | null,
    autoClose: boolean
  ) {
    this.mode = mode;
    this.videoLimit = videoLimit != null ? videoLimit.toString() : "";
    this.imageLimit = imageLimit != null ? imageLimit.toString() : "";
    this.mediaLimit = mediaLimit != null ? mediaLimit.toString() : "";
    this.videoDurationLimit = videoDurationLimit != null ? videoDurationLimit.toString() : "";
    this.autoClose = autoClose;
  }
  static singleMedia(mediaCount: number): CameraMode;
  static singleMedia(mediaCount: number, durationLimit?: number): CameraMode;
  static singleMedia(
    mediaCount: number,
    durationLimit?: number
  ): CameraMode {
    return new CameraMode(
      'videoAndImage',
      null,
      null,
      mediaCount ?? null,
      durationLimit ?? null,
      false
    );
  }

  static videoAndImage(): CameraMode;
  static videoAndImage(
    videoMaxCount?: number,
    imageMaxCount?: number,
    durationLimit?: number,
  ): CameraMode;

  static videoAndImage(
    videoMaxCount?: number,
    imageMaxCount?: number,
    durationLimit?: number
  ): CameraMode {
    return new CameraMode(
      'videoAndImage',
      videoMaxCount ?? null,
      imageMaxCount ?? null,
      null,
      durationLimit ?? null,
      false
    );
  }

  getJson(): string {
    var data = {
      mode: this.mode,
      videoLimit: this.videoLimit,
      imageLimit: this.imageLimit,
      mediaLimit: this.mediaLimit,
      videoDurationLimit: this.videoDurationLimit,
      autoClose: this.autoClose,
    };
    return JSON.stringify(data);
  }

  static singleVideo(): CameraMode;
  static singleVideo(durationLimit?: number): CameraMode;

  static singleVideo(durationLimit?: number): CameraMode {
    return new CameraMode(
      'singleVideo',
      1,
      0,
      null,
      durationLimit ?? null,
      true
    );
  }

  static singleImage(): CameraMode {
    return new CameraMode('singleImage', 0, 1, null, null, true);
  }
  static singleVideoOrImage(): CameraMode;
  static singleVideoOrImage(durationLimit?: number): CameraMode;
  static singleVideoOrImage(durationLimit?: number): CameraMode {
    return new CameraMode(
      'singleVideoOrImage',
      null,
      null,
      1,
      durationLimit ?? null,
      true
    );
  }

  static video(): CameraMode;
  static video(videoMaxCount?: number): CameraMode;
  static video(videoMaxCount?: number, durationLimit?: number): CameraMode;

  static video(videoMaxCount?: number, durationLimit?: number): CameraMode {
    return new CameraMode(
      'video',
      videoMaxCount ?? null,
      0,
      null,
      durationLimit ?? null,
      false
    );
  }

  static image(): CameraMode;
  static image(imageMaxCount?: number): CameraMode;
  static image(imageMaxCount?: number): CameraMode {
    return new CameraMode('image', 0, imageMaxCount ?? null, null, null, false);
  }
}

export interface CameraEventCallbacks {
  event?: (event: string) => void;
}

export class CameraEvents {
  private listener: EmitterSubscription | null = null;

  getEvent(callbacks: CameraEventCallbacks) {
    // Remove old listener if already exists
    this.removeEventListeners();

    if (callbacks?.event) {
      this.listener = DeviceEventEmitter.addListener(
        'event',
        (eventJson: string) => {
          try {
            const event = JSON.parse(eventJson);
            callbacks.event?.(event);
          } catch (e) {
            console.warn('Invalid event JSON:', eventJson);
          }
        }
      );
    }
  }

  removeEventListeners(): void {
    if (this.listener) {
      this.listener.remove();
      this.listener = null;
    }
  }
}

export * from './cameraConfigInterface';
