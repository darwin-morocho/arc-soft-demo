import 'package:equatable/equatable.dart';

sealed class BiometricStatus extends Equatable {}

class BiometricNotInitialized extends BiometricStatus {
  @override
  List<Object?> get props => [];
}

class BiometricAuthenticating extends BiometricStatus {
  @override
  List<Object?> get props => [];
}

class BiometricAuthenticated extends BiometricStatus {
  BiometricAuthenticated({required this.userId});
  final String userId;

  @override
  List<Object?> get props => [userId];
}

class BiometricEnrolling extends BiometricStatus {
  BiometricEnrolling({required this.userId});
  final String userId;

  @override
  List<Object?> get props => [userId];
}

class BiometricEnrolled extends BiometricStatus {
  BiometricEnrolled({required this.userId});
  final String userId;

  @override
  List<Object?> get props => [userId];
}

class BiometricError extends BiometricStatus {
  @override
  List<Object?> get props => [];
}
