# Smaller Signal APKs

Release and sideload APKs are smaller by default. Native libraries (libsignal, RingRTC, SQLCipher, Conscrypt) dominate the package; shipping four ABIs plus a fat universal APK was the main cost.

## What changed

| Setting | Before | Now (`signal.slimApk=true`) |
| --- | --- | --- |
| Native ABIs | armeabi-v7a, arm64-v8a, x86, x86_64 | armeabi-v7a, arm64-v8a |
| Universal / fat APK | yes | no (per-ABI APKs only) |
| Native lib packaging | uncompressed on Play | compressed (legacy packaging) |
| Resource shrinking | off | on for `release` |
| Test JNI (`libsignal_jni_testing.so`) | in non-release | never packaged |

On a typical phone, install **`*-arm64-v8a-*.apk`**. That is usually well under half the old universal APK.

## Overrides

Need an x86 emulator or a single fat APK:

```properties
# gradle.properties
signal.slimApk=false
```

or:

```bash
./gradlew :app:assemblePlayProdRelease -Psignal.slimApk=false
```

## Recommended build

```bash
./gradlew :app:assemblePlayProdRelease
```

Outputs are under `app/build/outputs/apk/playProd/release/` (one APK per ABI).
