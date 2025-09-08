import 'package:flutter/material.dart';
import 'package:flutter_hooks/flutter_hooks.dart';

import 'biometric_auth_controller.dart';
import 'biometric_status.dart';
import 'widgets/biometric_auth_view.dart';

class BiometricAuthScreen extends HookWidget {
  const BiometricAuthScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final controller = useMemoized(() => BiometricAuthController());

    useEffect(() {
      return controller.dispose;
    }, []);

    return Scaffold(
      body: SafeArea(
        child: Padding(
          padding: EdgeInsets.all(20),
          child: Column(
            children: [
              SizedBox(height: 20),
              Stack(
                children: [
                  BiometricAuthView(
                    onPlatformViewCreated: (_) => controller.initialize(),
                  ),

                  StreamBuilder(
                    stream: controller.connectionStatus,
                    initialData: controller.currentConnectionStatus,
                    builder: (context, snapshot) {
                      return switch (snapshot.data!) {
                        BiometricConnectionStatus.initializing => Positioned.fill(
                          child: Container(
                            color: Colors.black,
                            child: Center(
                              child: CircularProgressIndicator(),
                            ),
                          ),
                        ),
                        BiometricConnectionStatus.failed => Positioned.fill(
                          child: Container(
                            color: Colors.red,
                            child: Center(
                              child: Icon(Icons.error, color: Colors.white, size: 50),
                            ),
                          ),
                        ),
                        BiometricConnectionStatus.ready => SizedBox.shrink(),
                      };
                    },
                  ),
                ],
              ),
              SizedBox(height: 20),
              StreamBuilder(
                stream: controller.biometricStatus,
                initialData: controller.currentBiometricStatus,
                builder: (context, snapshot) {
                  return switch (snapshot.data!) {
                    BiometricNotInitialized() => SizedBox.shrink(),
                    BiometricAuthenticating() => ElevatedButton(
                      onPressed: () {
                        controller.enroll('new_user_${DateTime.now().millisecondsSinceEpoch}');
                      },
                      child: Text('Enroll new user'),
                    ),
                    BiometricAuthenticated() => Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Text('User authenticated successfully!'),
                        SizedBox(height: 20),
                        ElevatedButton(
                          onPressed: () {
                            controller.resumeRecognition();
                          },
                          child: Text('Resume Recognition'),
                        ),
                      ],
                    ),

                    BiometricEnrolling() => Text('Enrolling new user...'),

                    BiometricEnrolled() => Column(
                      children: [
                        Text('User enrolled successfully!'),
                        SizedBox(height: 20),
                        ElevatedButton(
                          onPressed: () {
                            controller.resumeRecognition();
                          },
                          child: Text('Resume Recognition'),
                        ),
                      ],
                    ),
                    BiometricError() => Text('Biometric authentication error occurred.'),
                  };
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}
