# Water Tracker NFC Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task‑by‑task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add NFC‑based water intake tracking, bottle registration, reminder notifications, and a stats page with daily line chart and weekly bar chart to the Android native app.

**Architecture:** Android app using MVVM with Jetpack Compose; Room for persistence; WorkManager for reminders; NFC foreground service for tag reads; MPAndroidChart for visualisation.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt (DI), Room, DataStore, WorkManager, MPAndroidChart, Coroutines, JUnit 5, Espresso, Robolectric.

---

### Task 1: Project Setup & Dependencies

**Files:**
- Modify: `app/build.gradle.kts`
- Create: `gradle/libs.versions.toml` (if using version catalog) – optional.

- [ ] **Step 1: Add required libraries**
```kotlin
dependencies {
    // Jetpack Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.activity.compose)
    // Hilt DI
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // Room
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
    // DataStore
    implementation(libs.datastore)
    // WorkManager
    implementation(libs.workmanager)
    // MPAndroidChart
    implementation(libs.mpandroidchart)
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.compose.ui.test)
    androidTestImplementation(libs.compose.ui.test.junit4)
}
```
- [ ] **Step 2: Sync Gradle**
Run `./gradlew build` and confirm no compilation errors.
- [ ] **Step 3: Commit**
```bash
git add build.gradle.kts
git commit -m "chore: add app dependencies for NFC water tracker"
```

---

### Task 2: Data Layer – Room Entities & DAO

**Files:**
- Create: `src/main/java/com/example/watertracker/data/local/WaterIntakeEntity.kt`
- Create: `src/main/java/com/example/watertracker/data/local/TagInfoEntity.kt`
- Create: `src/main/java/com/example/watertracker/data/local/AppDatabase.kt`
- Create: `src/main/java/com/example/watertracker/data/local/WaterIntakeDao.kt`
- Create: `src/main/java/com/example/watertracker/data/local/TagInfoDao.kt`
- Create: `src/main/java/com/example/watertracker/data/local/UserPrefsRepository.kt`

- [ ] **Step 1: Write failing test for DAO creation**
```kotlin
class DatabaseCreationTest {
    @Test
    fun `database can be built`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        assertNotNull(db)
    }
}
```
- [ ] **Step 2: Run test – expect failure (no AppDatabase class yet).**
```bash
./gradlew testDebugUnitTest --tests DatabaseCreationTest
```
- [ ] **Step 3: Implement minimal Room entities & database**
```kotlin
@Entity(tableName = "water_intake")
data class WaterIntakeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val liters: Float
)

@Entity(tableName = "tag_info")
data class TagInfoEntity(
    @PrimaryKey val uid: String,
    val capacityLiters: Float
)

@Database(entities = [WaterIntakeEntity::class, TagInfoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun tagInfoDao(): TagInfoDao
}
```
- [ ] **Step 4: Re‑run test – should pass.**
- [ ] **Step 5: Commit database changes**
```bash
git add src/main/java/com/example/watertracker/data/local/*.kt
git commit -m "feat(db): add Room entities and AppDatabase"
```

---

### Task 3: Repository Layer (Domain → Data)

**Files:**
- Create: `src/main/java/com/example/watertracker/domain/repository/WaterIntakeRepository.kt`
- Create: `src/main/java/com/example/watertracker/domain/repository/TagRepository.kt`

- [ ] **Step 1: Write failing test for adding intake**
```kotlin
class WaterIntakeRepositoryTest {
    private lateinit var repository: WaterIntakeRepository
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = WaterIntakeRepository(db.waterIntakeDao())
    }

    @Test
    fun `add intake stores a row`() = runBlocking {
        repository.addIntake(0L, 0.5f)
        val all = repository.getAllIntakes()
        assertEquals(1, all.size)
        assertEquals(0.5f, all.first().liters)
    }
}
```
- [ ] **Step 2: Run test – fails (no repository).**
- [ ] **Step 3: Implement repository**
```kotlin
class WaterIntakeRepository(private val dao: WaterIntakeDao) {
    suspend fun addIntake(timestamp: Long, liters: Float) {
        dao.insert(WaterIntakeEntity(timestamp = timestamp, liters = liters))
    }
    suspend fun getAllIntakes(): List<WaterIntakeEntity> = dao.getAll()
}
```
- [ ] **Step 4: Run test – passes.**
- [ ] **Step 5: Commit repository**
```bash
git add src/main/java/com/example/watertracker/domain/repository/*.kt
git commit -m "feat(repo): water intake repo with add/get functions"
```

---

### Task 4: NFC Reader Service

**Files:**
- Create: `src/main/java/com/example/watertracker/infra/nfc/NfcReaderService.kt`
- Create: `src/main/java/com/example/watertracker/infra/nfc/NfcUtils.kt`

- [ ] **Step 1: Write failing test for UID extraction**
```kotlin
class NfcUtilsTest {
    @Test
    fun `extract uid from Tag returns hex string`() {
        val mockTag = mock<Tag> {
            on { id } doReturn byteArrayOf(0x01, 0xAB.toByte(), 0x03)
        }
        val uid = NfcUtils.uidFromTag(mockTag)
        assertEquals("01ab03", uid)
    }
}
```
- [ ] **Step 2: Run test – fails (no NfcUtils).**
- [ ] **Step 3: Implement utility**
```kotlin
object NfcUtils {
    fun uidFromTag(tag: Tag): String = tag.id.joinToString(separator = "") { "%02x".format(it) }
}
```
- [ ] **Step 4: Run test – passes.**
- [ ] **Step 5: Implement foreground service skeleton**
```kotlin
@AndroidEntryPoint
class NfcReaderService : Service() {
    private val nfcAdapter: NfcAdapter? by lazy { NfcAdapter.getDefaultAdapter(this) }
    private val repository: TagRepository by inject()
    private val intakeRepo: WaterIntakeRepository by inject()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Enable foreground dispatch in the activity; this service just receives intents.
        return START_STICKY
    }

    fun handleTag(tag: Tag) {
        val uid = NfcUtils.uidFromTag(tag)
        val tagInfo = repository.getTagInfo(uid)
        if (tagInfo != null) {
            // known bottle → add intake
            intakeRepo.addIntake(System.currentTimeMillis(), tagInfo.capacityLiters)
        } else {
            // unknown – broadcast to UI to show registration dialog
            val broadcast = Intent(ACTION_UNKNOWN_TAG).apply { putExtra(EXTRA_UID, uid) }
            sendBroadcast(broadcast)
        }
    }
}
```
- [ ] **Step 6: Add constant definitions** (ACTION_UNKNOWN_TAG, EXTRA_UID) in the same file or companion object.
- [ ] **Step 7: Write a UI‑side test that the broadcast is received (optional – can be covered later).**
- [ ] **Step 8: Commit NFC service**
```bash
git add src/main/java/com/example/watertracker/infra/nfc/*.kt
git commit -m "feat(nfc): uid extraction and foreground service skeleton"
```

---

### Task 5: UI – Home Screen & Tag Registration Dialog

**Files:**
- Modify: `src/main/java/com/example/watertracker/ui/HomeScreen.kt`
- Create: `src/main/java/com/example/watertracker/ui/RegisterTagDialog.kt`
- Create: `src/main/java/com/example/watertracker/ui/ReminderSwitch.kt` (optional UI component).

- [ ] **Step 1: Write failing Compose test for HomeScreen showing 0 L initially**
```kotlin
@get:Rule val composeRule = createComposeRule()

@Test fun `home shows zero intake at start`() {
    composeRule.setContent { HomeScreen(viewModel = FakeHomeViewModel()) }
    composeRule.onNodeWithText("0 L today").assertExists()
}
```
- [ ] **Step 2: Run test – fails (no HomeScreen).**
- [ ] **Step 3: Implement minimal HomeScreen**
```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val totalToday by viewModel.todaysIntake.collectAsState(initial = 0f)
    Scaffold(
        topBar = { TopAppBar(title = { Text("Water Tracker") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.startNfcScan() }) {
                Icon(Icons.Default.Add, contentDescription = "Tap Bottle")
            }
        }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(text = "${totalToday} L today", style = MaterialTheme.typography.h4)
        }
    }
}
```
- [ ] **Step 4: Run test – passes.**
- [ ] **Step 5: Implement RegisterTagDialog** – Compose Dialog with a `TextField` for capacity, OK/Cancel buttons; on OK, call `TagRepository.registerTag(uid, capacity)` and close.
- [ ] **Step 6: Add BroadcastReceiver in HomeViewModel to listen for ACTION_UNKNOWN_TAG and expose a `MutableStateFlow<String?>` for `pendingUid`. When non‑null, UI shows `RegisterTagDialog`.
- [ ] **Step 7: Write a UI test that when broadcast received, dialog appears (optional, can be added later).**
- [ ] **Step 8: Commit UI files**
```bash
git add src/main/java/com/example/watertracker/ui/*.kt
git commit -m "feat(ui): home screen, NFC start button, unknown‑tag dialog"
```

---

### Task 6: Stats Screen with Charts

**Files:**
- Create: `src/main/java/com/example/watertracker/ui/StatsScreen.kt`
- Create: `src/main/java/com/example/watertracker/ui/ChartHelper.kt` (wrapper for MPAndroidChart setup).

- [ ] **Step 1: Write failing test for line chart data count (7 days)**
```kotlin
class StatsViewModelTest {
    @Test
    fun `daily data for last 7 days returns 7 entries`() = runBlocking {
        val repo = FakeIntakeRepo() // pre‑populate with 7 days of data
        val vm = StatsViewModel(repo)
        val daily = vm.dailyIntakeFlow.first()
        assertEquals(7, daily.size)
    }
}
```
- [ ] **Step 2: Run test – fails (no ViewModel).**
- [ ] **Step 3: Implement `StatsViewModel`** – aggregates `WaterIntakeEntity` by day and week using Kotlin’s `java.time` API.
- [ ] **Step 4: Implement `StatsScreen`** – uses `AndroidView` to host `LineChart` and `BarChart` from MPAndroidChart, feeds data from ViewModel.
- [ ] **Step 5: Run UI test (optional) to verify charts render (can be manual).**
- [ ] **Step 6: Commit stats files**
```bash
git add src/main/java/com/example/watertracker/ui/StatsScreen.kt src/main/java/com/example/watertracker/ui/ChartHelper.kt
git commit -m "feat(stats): daily line chart and weekly bar chart"
```

---

### Task 7: Reminder WorkManager

**Files:**
- Create: `src/main/java/com/example/watertracker/infra/reminder/WaterReminderWorker.kt`
- Modify: `src/main/java/com/example/watertracker/infra/reminder/ReminderScheduler.kt` (optional helper).

- [ ] **Step 1: Write failing test that worker schedules notification when intake < goal**
```kotlin
class WaterReminderWorkerTest {
    @Test
    fun `worker sends notification when not met`() = runBlockingTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = FakeUserPrefs(goal = 2.0f)
        val repo = FakeIntakeRepo(totalToday = 1.0f)
        val worker = TestWorkerBuilder<WaterReminderWorker>(context).setInputData(workDataOf()).build()
        val result = worker.doWork()
        assertEquals(ListenableWorker.Result.success(), result)
        // Verify NotificationManager received a notification (use a fake or shadow).
    }
}
```
- [ ] **Step 2: Run test – fails (no worker).**
- [ ] **Step 3: Implement `WaterReminderWorker`** – reads today’s total from repository, compares to goal, uses `NotificationCompat.Builder` to post a reminder.
- [ ] **Step 4: Schedule periodic work in Application class (or a small helper)** – `WorkManager.enqueuePeriodicWorkRequest<WaterReminderWorker>(repeatInterval = 2.hours)`.
- [ ] **Step 5: Run test – passes.**
- [ ] **Step 6: Commit reminder files**
```bash
git add src/main/java/com/example/watertracker/infra/reminder/*.kt
git commit -m "feat(reminder): periodic WorkManager reminder worker"
```

---

### Task 8: End‑to‑End Manual Test & Documentation

**Files:**
- Modify: `README.md` – add a *Quick Start* section describing how to enable NFC, register a tag, and view stats.
- Create: `docs/USER_GUIDE.md` (optional) – detailed steps for end‑users.

- [ ] **Step 1: Write manual test checklist** (not automated, just documentation).
- [ ] **Step 2: Update README with usage instructions and screenshots placeholders.
- [ ] **Step 3: Commit documentation**
```bash
git add README.md docs/USER_GUIDE.md
git commit -m "docs: add quick‑start guide for NFC water tracker"
```

---

### Task 9: Continuous Integration (optional but recommended)

**Files:**
- Modify: `.github/workflows/android.yml` – ensure `./gradlew test` runs on each PR.

- [ ] **Step 1: Add a job that runs unit & instrumentation tests on a fresh emulator.
- [ ] **Step 2: Verify CI passes locally with `./gradlew testDebugUnitTest`.
- [ ] **Step 3: Commit CI workflow**
```bash
git add .github/workflows/android.yml
git commit -m "ci: add Android test workflow for water‑tracker"
```

---

### Task 10: Final Verification & Tag Overwrite Note

- [ ] **Step 1: Run the full app on a device/emulator**
  - Verify NFC tap adds intake.
  - Verify unknown tag triggers the registration dialog.
  - Verify reminders appear when below goal.
  - Verify both charts display correct data.
- [ ] **Step 2: Run `./gradlew connectedAndroidTest` (optional).**
- [ ] **Step 3: Tag overwrite clarification** – the app lets the user edit a tag’s stored capacity via the registration dialog (re‑open dialog from settings). No hardware change.
- [ ] **Step 4: Commit final state**
```bash
git push origin HEAD
```

---

**Implementation notes**
- Every code change is done in its own short‑lived commit (as shown above) to keep history clean.
- All tests are written **before** the corresponding implementation (TDD).
- The UI uses Compose’s `StateFlow` pattern; ViewModels are annotated with `@HiltViewModel` for DI.
- The NFC service runs only while the app is in the foreground for privacy/security.
- Reminder notifications respect the user’s opt‑out choice stored in `UserPrefs`.

---

**Execution hand‑off**

Plan complete and saved to `docs/superpowers/plans/2026-09-01-water-tracker-implementation.md`. Two execution options:

1. **Subagent‑Driven (recommended)** – I dispatch a fresh subagent per task, review between tasks, fast iteration.
2. **Inline Execution** – Execute tasks in this session using `superpowers:executing-plans`, batch execution with checkpoints for review.

Which approach would you like to use?