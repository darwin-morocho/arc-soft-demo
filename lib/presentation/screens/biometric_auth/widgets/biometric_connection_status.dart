import 'package:flutter/material.dart';

import '../biometric_auth_controller.dart';

class BiometricConnectionStatusView extends StatelessWidget {
  const BiometricConnectionStatusView({super.key, required this.controller});
  final BiometricAuthController controller;

  @override
  Widget build(BuildContext context) {
    return StreamBuilder(
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
    );
  }
}
