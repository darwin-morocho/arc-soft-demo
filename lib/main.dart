import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

import 'presentation/screens/biometric_auth/biometric_auth_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Permission requests must implement better handling in a production app
  // this is just for demo purposes, you should check the permission status
  // before interact with camera, phone or storage
  await Permission.camera.request();
  await Permission.phone.request();
  await Permission.storage.request();

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
