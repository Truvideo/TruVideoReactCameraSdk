// @ts-ignore - React is required for JSX but TypeScript doesn't see it as used
import React from 'react';
import { StyleSheet, View, Button } from 'react-native';
import {
  initCameraScreen,
  LensFacing,
  FlashMode,
  Orientation,
  type CameraConfiguration,
  CameraMode,
} from 'truvideo-react-camera-sdk';

export default function App() {
  const configuration: CameraConfiguration = {
    lensFacing: LensFacing.Front,
    flashMode: FlashMode.Off,
    orientation: Orientation.Portrait,
    outputPath: '',
    frontResolutions: [],
    frontResolution: { width: 1920, height: 1080 },
    backResolutions: [],
    backResolution: { width: 1920, height: 1080 },
    mode: CameraMode.image(),
  };
  const inItCamera = () => {
    initCameraScreen(configuration)
      .then((res) => {
        console.log('typeOf res', typeof res); // 'object'
        console.log('res', res);

        if (res && res.length > 0) {
          console.log('filePath', res[0]!.filePath);
        } else {
          console.warn('No camera result returned');
        }
      })
      .catch((e) => console.warn(e));
  };

  return (
    <View style={styles.container}>
      <Button
        onPress={() => inItCamera()}
        title="Press to initialize camera"
        color="#eb4034"
        accessibilityLabel="Learn more about this purple button"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
