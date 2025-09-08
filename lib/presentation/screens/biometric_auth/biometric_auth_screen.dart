import 'package:flutter/material.dart';
import 'package:flutter_hooks/flutter_hooks.dart';

import 'biometric_auth_controller.dart';
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
              BiometricAuthView(),
              SizedBox(height: 20),
            ],
          ),
        ),
      ),
    );
  }
}
