import 'package:flutter/material.dart';

import '../biometric_auth_controller.dart';
import '../biometric_status.dart';

class BiometricStatus extends StatelessWidget {
  const BiometricStatus({super.key, required this.controller});
  final BiometricAuthController controller;

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
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
    );
  }
}
