import { LensFacing, FlashMode, Orientation } from './cameraConfigEnums';

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
}


export interface Resolution {
  width: number;
  height: number;
}

export interface CameraMode {
  mode: string;
  videoLimit: string;
  imageLimit: string;
  mediaLimit: string;
  videoDurationLimit: string;
  autoClose: boolean;
}

