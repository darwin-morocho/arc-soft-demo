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
              'appID': '57hpVQwbVUzBwqLDgANvQ9q984ztKgzsmcRTMmk6RQuQ',
              'sdkKey': '6ojLuCp38B2j9pwx8N7twdDAdBbKFbcoqapmP3NggDC',
              'activeKey': '085T-11YM-JL45-8K6Y',
              'rgbCameraId': '0',
              'irCameraId': '1',
            },
          ),
        ),
      ),
    );
  }
}
