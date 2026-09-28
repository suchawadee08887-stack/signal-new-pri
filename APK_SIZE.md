# Release APK size optimization

## Why the APK is large

Signal’s release APK is dominated by:

| Contributor | Why it is large | What we did |
| --- | --- | --- |
| Native libraries (`libsignal`, RingRTC, SQLCipher, Conscrypt, etc.) × 4 ABIs | Play/default packaging leaves `.so` files **uncompressed** (minSdk 23+) | Compress `.so` in **all** release APKs via `useLegacyPackaging` |
| Unused resources | `isMinifyEnabled` was on, but **resource shrinking was off** | Enable `isShrinkResources` + PNG crunch |
| License / META-INF / proto / debug `.so` junk | Copied from dependencies into the APK | Broader `packaging.resources` / `jniLibs` excludes |
| Four ABIs + universal APK | Native libs ×4 plus a fat APK | Default `signal.slimApk=true`: ARM only, no universal. Restore x86/universal with `-Psignal.slimApk=false` |
| 80+ language packs | Essential product behavior | **Not** stripped |
| R8 `-dontoptimize` / `-dontobfuscate` / keep-all app classes | Reproducible builds + crash traces | **Not** changed (would risk behavior and Signal’s release policy) |

Existing `apng-release.apk` in `app/release/` is a **demo** artifact (~67 MB), not the full messenger.

## Changes (build config only)

### 1. `app/build.gradle.kts` — `release` build type

- `isShrinkResources = true` — unused layouts/drawables/values are omitted.
- `isCrunchPngs = true` — lossless PNG compression at package time.

**Risk:** dynamically loaded resources. Mitigated by `res/raw/keep.xml` (`tools:keep="@raw/*"`) so trust stores, sounds, and Lottie JSON stay.

### 2. `app/build.gradle.kts` — `androidComponents`

- `jniLibs.useLegacyPackaging = true` for **every** `release` variant (not only website/github).

**Risk:** none for functionality. Install is slightly slower because the OS extracts `.so` files; Play’s on-device size is similar. APK **file** size drops a lot (native code is often 50–70% of a fat APK).

### 3. `app/build.gradle.kts` — `packaging`

Excluded non-runtime files: extra licenses, `META-INF/*.version`, `DebugProbesKt.bin`, `kotlin-tooling-metadata.json`, `*.so.debug`, `libsignal_jni_testing.so`.

**Risk:** none; these are not loaded by the app.

### 4. `gradle.properties`

- `android.enableResourceOptimizations=true`

### 5. `app/src/main/res/raw/keep.xml`

Shrinker keep rules only. No runtime effect.

## What we deliberately did **not** do

- Remove `armeabi-v7a`, `x86`, or `x86_64` — would break older phones, Chromebooks, and emulators.
- Disable `isUniversalApk` — sideload still needs a fat APK; per-ABI splits remain for smaller downloads.
- Drop locales / enable language splits — changing language must keep working offline.
- Turn on R8 optimize/obfuscate or drop `-keep class org.thoughtcrime.securesms.**` — high crash/behavior risk.
- Delete features, maps, calling, or backups code.

## Expected size impact

On a typical **universal** Signal-class APK:

- Compressing native libs: **~30–50%** file-size reduction.
- Resource shrinking + packaging excludes: **a few MB**.

**Per-ABI** APKs (already produced by `splits.abi`) remain the smallest install units (~¼ of native weight). Prefer `*-arm64-v8a-*.apk` for modern phones.

## Verification

```bash
./gradlew :app:assemblePlayProdRelease :app:assembleWebsiteProdRelease
```

Compare:

```text
app/build/outputs/apk/playProd/release/          # per-ABI + universal
app/build/outputs/apk/websiteProd/release/
```

Checks: assemble succeeds; install an ABI-matched APK; launch, register/restore, send a message, place a call, open settings. No app source behavior was changed.

A full release assemble was not run in this environment (no Android SDK/NDK). After you sync the branch, run the commands above and record before/after bytes in this table:

| Artifact | Before | After | Δ |
| --- | --- | --- | --- |
| playProd universal | (measure locally) | (measure locally) | |
| playProd arm64-v8a | | | |
| websiteProd universal | | | |

## Success criterion

Smaller **release APK file** with the same features, ABIs, languages, and R8 keep policy.
