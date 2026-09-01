# Water Tracker NFC App

## Quick Start
1. **Build the app** – open the project in Android Studio and click **Run**.
2. **Enable NFC** on your phone (Settings → Connected devices → NFC).
3. **Register a bottle**:
   - Scan a new NFC tag on your water bottle.
   - When the dialog appears, enter the bottle’s capacity in litres and press **Save**.
4. **Track water** – each time you tap the bottle with the phone, the app adds the bottle’s capacity to today’s total.
5. **View stats** – open the **Stats** screen to see a daily line chart (last 7 days) and a weekly bar chart (last 4 weeks).
6. **Reminders** – the app will send periodic push notifications if you haven’t reached your daily goal.

## Development Notes
- The code follows an MVVM architecture with Jetpack Compose, Hilt for DI, Room for persistence, and WorkManager for reminders.
- All core logic is covered by unit tests; run `./gradlew test` to verify.
- To add more bottles, simply scan a new NFC tag; the app will prompt you to register its capacity.
