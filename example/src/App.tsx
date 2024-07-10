import * as React from 'react';
import { StyleSheet, View, Button } from 'react-native';
import {
  initCameraScreen,
  LensFacing,
  FlashMode,
  Orientation,
  Mode,
  type CameraConfiguration,
} from 'truvideo-react-camera-sdk';

export default function App() {
  const configuration: CameraConfiguration = {
    lensFacing: LensFacing.Front,
    flashMode: FlashMode.Off,
    orientation: Orientation.Portrait,
    outputPath: '',
    frontResolutions: [],
    frontResolution: 'nil',
    backResolutions: [],
    backResolution: 'nil',
    mode: Mode.Picture,
  };

  const inItCamera = () => {
    initCameraScreen(configuration).then((res) => {
      console.log('typeOf res', typeof res);
      console.log('res', JSON.parse(res));
      let obj = JSON.parse(res);
      console.log('filePath', obj[0].filePath);
    });
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
