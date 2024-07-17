import { NativeModules } from 'react-native';
import { initCameraScreen } from '../index';
import { LensFacing, FlashMode, Orientation, Mode } from '../cameraConfigEnums';

jest.mock('react-native', () => ({
  NativeModules: {
    TruVideoReactCameraSdk: {
      initCameraScreen: jest.fn(),
    },
  },
  Platform: {
    select: jest.fn().mockImplementation((objs) => objs.default),
  },
}));

describe('initCameraScreen', () => {
  const mockConfiguration = {
    lensFacing: LensFacing.Back,
    flashMode: FlashMode.On,
    orientation: Orientation.Portrait,
    outputPath: '/path/to/output',
    frontResolutions: ['1280x720', '1920x1080'],
    frontResolution: '1920x1080',
    backResolutions: ['1280x720', '1920x1080'],
    backResolution: '1920x1080',
    mode: Mode.Picture,
  };
  const mockResponse = 'mockInitResponse';

  beforeEach(() => {
    (
      NativeModules.TruVideoReactCameraSdk.initCameraScreen as jest.Mock
    ).mockClear();
  });

  it('calls TruVideoReactCameraSdk.initCameraScreen with correct arguments and returns response', async () => {
    (
      NativeModules.TruVideoReactCameraSdk.initCameraScreen as jest.Mock
    ).mockResolvedValue(mockResponse);

    const result = await initCameraScreen(mockConfiguration);

    expect(
      NativeModules.TruVideoReactCameraSdk.initCameraScreen
    ).toHaveBeenCalledWith(JSON.stringify(mockConfiguration));
    expect(result).toBe(mockResponse);
  });

  it('handles errors correctly', async () => {
    const mockError = new Error('mock error');
    (
      NativeModules.TruVideoReactCameraSdk.initCameraScreen as jest.Mock
    ).mockRejectedValue(mockError);

    await expect(initCameraScreen(mockConfiguration)).rejects.toThrow(
      'mock error'
    );

    expect(
      NativeModules.TruVideoReactCameraSdk.initCameraScreen
    ).toHaveBeenCalledWith(JSON.stringify(mockConfiguration));
  });
});
