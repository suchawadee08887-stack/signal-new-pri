# Signal Android version

| Field | Value |
| --- | --- |
| Version name | **8.22.2** |
| Canonical version code | 1733 |
| Upstream tag | [v8.22.2](https://github.com/signalapp/Signal-Android/releases/tag/v8.22.2) |
| Previous project version | 8.15.3 (canonical 1707) |

## Highlights since 8.15.3

- Linked Android tablets and linked Android phones
- Disappearing call events when disappearing messages are enabled
- Improved video quality and longer maximum sent video length
- Admin message deletion, group suggestions, more pinned chats (up to 10)
- Text selection for long “Read more” messages
- Remote mute in group calls
- Scheduled-message notifications in Note to Self
- Background connectivity setting for devices without Play Services
- End-to-end encrypted Signal secure backups

This tree matches official Signal Android **v8.22.2**.

## Android Studio sync: kotlin-dsl 6.4.2 verification error

If sync fails with:

`Dependency verification failed ... org.gradle.kotlin.kotlin-dsl.gradle.plugin-6.4.2.pom`

1. Use the **Gradle wrapper** (9.4.1), not a manually selected 9.3.x.
2. Pull the latest `gradle/verification-metadata.xml` — it trusts official `org.gradle.kotlin` plugin artifacts so IDE sync is not blocked.
3. File → Invalidate Caches / Sync Project with Gradle Files.
