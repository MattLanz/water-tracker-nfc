# Manual Test Script for NFC Background Service

## Prerequisites
- Build and install the app on a device with NFC support.
- Enable **Background logging** in the (future) Settings screen (or set the shared‑pref manually for now).

## Steps
1. **Enable background logging**
   - Open Settings → toggle **Background logging** ON.
   - Verify a persistent notification appears: *"Water Tracker – Scanning for bottle…"*.
2. **Tap a known NFC bottle**
   - Bring a bottle whose UID is already registered in the app.
   - Observe that the total water intake updates instantly on the Home screen **without** the app UI opening.
3. **Tap an unknown NFC tag**
   - Use a new bottle/tag not previously registered.
   - The app should display a bottom‑sheet prompting for **Capacity (Liters)**.
   - Enter a value (e.g., `0.5`) and press **Save**.
   - Verify the total intake updates accordingly and the bottom‑sheet dismisses.
4. **Disable background logging**
   - Toggle the setting off.
   - Ensure the foreground notification disappears and NFC scanning stops.
5. **Edge Cases**
   - Cancel the registration bottom‑sheet – ensure no intake is logged.
   - Rotate the device while the bottom‑sheet is visible – ensure state persists.

Record any failures and report them to the development team.
