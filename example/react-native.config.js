const path = require('node:path');

module.exports = {
  project: {
    ios: {
      automaticPodsInstallation: true,
    },
  },
  dependencies: {
    // Configure @trunpm/truvideo-react-camera-sdk to point to the npm package
    // Since this is a yarn workspace, the package is hoisted to root node_modules
    '@trunpm/truvideo-react-camera-sdk': {
      root: path.resolve(
        __dirname,
        '..',
        'node_modules',
        '@trunpm',
        'truvideo-react-camera-sdk'
      ),
      platforms: {
        android: {
          sourceDir: path.resolve(
            __dirname,
            '..',
            'node_modules',
            '@trunpm',
            'truvideo-react-camera-sdk',
            'android'
          ),
          packageImportPath:
            'import com.truvideoreactcamerasdk.TruVideoReactCameraSdkPackage;',
        },
      },
    },
  },
};
