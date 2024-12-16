# truvideo-react-camera-sdk

none

## Installation

```sh
"dependencies": {
  // replace token with your personal access token
    "truvideo-react-camera-sdk": "git+https://<token>@github.com/Truvideo/TruVideoReactCameraSdk.git#release-version-76"
}

// or
npm install truvideo-react-camera-sdk
```

## Usage

```js
import {
  initCameraScreen,
  LensFacing,
  FlashMode,
  Orientation,
  Mode,
  type CameraConfiguration,
} from 'truvideo-react-camera-sdk';

// ...
const configuration: CameraConfiguration = {
    lensFacing: LensFacing.Front, //Front and Back option are there
    flashMode: FlashMode.Off,// On and Off option are there
    orientation: Orientation.Portrait, // Portrait, LandscapeLeft,LandscapeRight and PortraitReverse option are there
    outputPath: '',
    frontResolutions: [],
    frontResolution: 'nil',
    backResolutions: [],
    backResolution: 'nil',
    mode: Mode.Picture, // Picture,Video and VideoAndPicture options are there
  };
const inItCamera = () => {
    initCameraScreen(configuration).then((res) => {
      let obj = JSON.parse(res);
    });
  };

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
