import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class BiometricAuthView extends StatelessWidget {
  const BiometricAuthView({
    super.key,
    required this.onPlatformViewCreated,
  });
  final void Function(int)? onPlatformViewCreated;

  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: 1,
      child: ClipOval(
        child: Container(
          color: Colors.grey[800],
          child: AndroidView(
            viewType: 'biometric_auth_view',
            creationParamsCodec: StandardMessageCodec(),
            onPlatformViewCreated: onPlatformViewCreated,
            creationParams: {
              'appID': 'YOUR-APP-ID',
              'sdkKey': 'YOUR-SDK-KEY',
              'activeKey': 'YOUR-ACTIVE-KEY',
              'rgbCameraId': 'YOUR-RGB-CAMERA-ID',
              'irCameraId': 'YOUR-IR-CAMERA-ID',
            },
          ),
        ),
      ),
    );
  }
}
