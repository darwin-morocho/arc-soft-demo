import 'dart:async';
import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:rxdart/subjects.dart';

import 'biometric_status.dart';

enum BiometricConnectionStatus { initializing, ready, failed }

final _methodChannel = MethodChannel('biometric_auth_channel');
final _eventChannel = EventChannel('biometric_auth_events');

class BiometricAuthController {
  final _connectionStatus = BehaviorSubject.seeded(BiometricConnectionStatus.initializing);
  final _biometricStatus = BehaviorSubject<BiometricStatus>();

  Stream<BiometricConnectionStatus> get connectionStatus => _connectionStatus.stream;
  BiometricConnectionStatus get currentConnectionStatus => _connectionStatus.value;

  Stream<BiometricStatus> get biometricStatus => _biometricStatus.stream;
  BiometricStatus get currentBiometricStatus => _biometricStatus.value;

  StreamSubscription? _eventSubscription;

  /// Start listening to biometric status updates from the native side
  void _startListening() {
    _eventSubscription = _eventChannel.receiveBroadcastStream().listen((event) {});
  }

  /// Initialize biometric authentication
  Future<void> initialize() async {
    try {
      final result = await _methodChannel.invokeMethod<bool>('initialize');
      if (result == true) {
        _connectionStatus.add(BiometricConnectionStatus.ready);
      } else {
        _connectionStatus.add(BiometricConnectionStatus.failed);
      }
    } on PlatformException {
      _connectionStatus.add(BiometricConnectionStatus.failed);
    }
  }

  Future<void> addTemplates(Map<String, Uint8List> templates) async {
    try {
      await _methodChannel.invokeMethod('addTemplates', {'templates': templates});
    } on PlatformException {
      log('Failed to add templates', level: 900);
    }
  }

  /// Enroll a new biometric template for the given user ID
  Future<Uint8List?> enroll(String userId) async {
    try {
      final result = await _methodChannel.invokeMethod<Uint8List>('enroll', {'userId': userId});
      return result;
    } on PlatformException {
      log('Failed to enroll biometric', level: 900);
      return null;
    }
  }

  Future<void> resumeRecognition() async {
    try {
      await _methodChannel.invokeMethod('resumeRecognition');
    } on PlatformException {
      log('Failed to resume recognition', level: 900);
    }
  }

  /// Release resources when biometric auth is no longer needed
  Future<void> dispose() async {
    await _eventSubscription?.cancel();
    await _connectionStatus.close();
    await _biometricStatus.close();
    try {
      await _methodChannel.invokeMethod('dispose');
    } on PlatformException {
      log('Failed to dispose biometric auth', level: 900);
    }
  }
}
