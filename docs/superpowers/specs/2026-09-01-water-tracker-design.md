---
name: water-tracker-design
description: Design spec for Android-native NFC water‑tracker app
metadata:
  type: reference
---

# Water‑Tracker NFC App – Design Spec (2026‑09‑01)

## 1. Architecture Overview
- **Platform:** Android native (Kotlin) using **MVVM** with **Jetpack Compose**.
- **Layers:**
  - **Presentation:** Single‑activity Compose UI.
  - **ViewModel:** Exposes `StateFlow` for UI state, delegates to use‑cases.
  - **Domain:** Plain Kotlin use‑cases (`AddWaterIntake`, `RegisterTag`, `GetStats`).
  - **Data:** Room DB for `WaterIntake` & `TagInfo`; DataStore for simple prefs (daily goal).
  - **Services:** WorkManager for periodic reminder notifications; Android NFC foreground service for tag reads.

## 2. Data Model
| Entity | Fields | Purpose |
|--------|--------|---------|
| **WaterIntake** | `id: Long` (PK), `timestamp: Long`, `liters: Float` | One record per tap – when and how much water was added. |
| **TagInfo** | `uid: String` (NFC tag UID, PK), `capacityLiters: Float` | Maps each discovered NFC tag to its bottle’s volume. |
| **UserPrefs** (DataStore) | `dailyGoalLiters: Float` (default 2.0 L) | User‑defined daily target. |

> **Tag overwrite:** The NFC tag’s UID is immutable; the app cannot modify the tag itself. To change a bottle’s capacity you simply edit the corresponding `TagInfo` row in the app (or re‑register the tag). The tag can be *overwritten* in the sense of updating the stored capacity, not its hardware UID.

## 3. NFC Handling
1. **Foreground Dispatch** – Register `NfcAdapter.ACTION_NDEF_DISCOVERED` while app is foreground.
2. **UID Extraction** – Read raw UID (`byte[] → hex string`).
3. **Lookup:**
   - **Found:** Insert a `WaterIntake` record using the saved capacity.
   - **Missing:** Show `RegisterTagDialog` → user enters capacity → store in `TagInfo`.
4. All NFC logic lives in a `NfcReader` class injected into the `ViewModel`.

## 4. UI Components
- **HomeScreen:** Current day total, large *Tap Bottle* button, fallback *Add Manually*.
- **RegisterTagDialog:** Prompt for capacity (L) when unknown tag is detected.
- **StatsScreen:**
  - *DailyLineChart* (last 7 days) – MPAndroidChart line chart.
  - *WeeklyBarChart* (last 4 weeks) – MPAndroidChart bar chart.
- **SettingsScreen:** Adjust daily goal, toggle reminders, export data.

## 5. Reminder System
- WorkManager periodic work (e.g., every 2 h) checks day total.
- If below goal, fires push notification: “Stay hydrated – you’ve logged X L so far.”
- Users can enable/disable in Settings.

## 6. Testing Strategy
| Layer | Tests |
|-------|-------|
| **Unit** | JUnit + Coroutines Test for use‑cases, Repository, UID parsing. |
| **Instrumentation** | Espresso + Compose testing for UI flows (tap → add, unknown tag → register). |
| **Integration** | Robolectric for Room CRUD verification. |

## 7. Extensibility
- `TagInfo` supports unlimited bottles; adding a new bottle only requires scanning a new tag.
- NFC handling is isolated – swapping to Bluetooth sensor is a single‑class change.
- Future cloud sync can replace the `WaterIntakeRepository` implementation without touching UI or domain layers.

---

*This spec is ready for review. Once you approve, I will generate a detailed implementation plan.*