import 'package:flutter/material.dart';

import 'presentation/screens/biometric_auth/biometric_auth_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      themeMode: ThemeMode.dark,
      darkTheme: ThemeData.dark(),
      home: const BiometricAuthScreen(),
    );
  }
}
