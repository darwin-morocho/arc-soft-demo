import 'package:flutter/material.dart';

class BiometricAuthView extends StatelessWidget {
  const BiometricAuthView({super.key});

  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: 1,
      child: ClipOval(
        child: Container(
          color: Colors.grey[800],
        ),
      ),
    );
  }
}
