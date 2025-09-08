# ArcSoft Demo

A Flutter project demonstrating biometric face recognition using ArcSoft Face SDK on Android.

## Prerequisites

This project uses Git LFS (Large File Storage) to manage large native library files (.so files). Make sure you have Git LFS installed before cloning the repository.

### Installing Git LFS

**macOS:**
```bash
brew install git-lfs
```

**Ubuntu/Debian:**
```bash
sudo apt install git-lfs
```

**Windows:**
Download from [Git LFS releases](https://github.com/git-lfs/git-lfs/releases) or use:
```bash
winget install GitHub.GitLFS
```

### Initialize Git LFS
After installing Git LFS, initialize it:
```bash
git lfs install
```

## Getting Started

### Cloning the Repository

To properly clone this repository with all the native libraries:

```bash
git clone https://github.com/darwin-morocho/arc-soft-demo.git
cd arc_soft_demo
```

Git LFS will automatically download the large .so files during the clone process. If for some reason the LFS files weren't downloaded, you can manually pull them:

```bash
git lfs pull
```

### Verifying LFS Files

To verify that the large files were downloaded correctly:

```bash
git lfs ls-files
```

You should see the native library files listed, including:
- `android/app/libs/arm64-v8a/libarcsoft_face.so` (~108MB)
- `android/app/libs/arm64-v8a/libarcsoft_face_engine.so`
- Other ArcSoft and RKNN library files

### Flutter Setup

Once you have cloned the repository and ensured the LFS files are downloaded:

1. **Get Flutter dependencies:**
   ```bash
   flutter pub get
   ```

2. **Run the app:**
   ```bash
   flutter run
   ```

## Project Structure

This project includes:
- **Biometric face recognition** using ArcSoft Face SDK
- **Native Android libraries** managed with Git LFS
- **Method channels** for Flutter-Android communication

## Important Notes

- The native .so libraries are essential for the face recognition functionality
- These files are managed with Git LFS due to their large size (>100MB)
- Make sure Git LFS is properly set up before working with this repository
