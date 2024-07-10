import * as React from 'react';
import { StyleSheet, View, Button, Text } from 'react-native';
import {
  multiply,
  initCameraScreen,
  LensFacing,
  FlashMode,
  Orientation,
  Mode,
} from 'truvideo-react-camera-sdk';


export default function App() {
  const [result, setResult] = React.useState<number | undefined>();
  const [configuration, setConfiguration] = React.useState<any>();

  React.useEffect(() => {
    multiply(3, 7).then(setResult);

    setConfiguration({
      lensFacing: LensFacing.Back,
      flashMode: FlashMode.On,
      orientation: Orientation.Portrait,
      outputPath: '',
      frontResolutions: [],
      frontResolution: 'nil',
      backResolutions: [],
      backResolution: 'nil',
      mode: Mode.Picture,
    });
  }, []);

  const inItCamera = () => {
    initCameraScreen(configuration).then((setResult) => {
      console.log('setResult Ayushgit', setResult);
    });
  };

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>

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
