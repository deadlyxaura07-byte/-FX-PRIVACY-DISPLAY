# FX Privacy Display

Native Android privacy-overlay utility by FAKEEXX / FX GROUP.

## Current foundation
- Kotlin + Jetpack Compose
- Premium dark interface with animated ambient lighting, rotating shield ring, breathing motion and state transitions
- Adjustable full-screen dimming overlay
- Foreground service while active
- Runtime overlay-permission flow
- Phone and tablet-friendly scrolling layout

## Build
Open this repository in Android Studio, sync Gradle, then run the `app` configuration on an Android device.

## Important limitation
This is a software dimming overlay. It cannot reproduce a physical directional privacy filter or Samsung's hardware-level side-angle restriction. Android also restricts system-wide app detection and controls; those features require separate, user-granted APIs and device-specific testing.

The app requests "Display over other apps" special access when the user enables the overlay. A persistent notification is shown while protection runs.