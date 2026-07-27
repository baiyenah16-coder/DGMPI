# Dhaka Gov. Mohila Polytechnic Institute — Android App (Firebase Edition)

## 🚀 Get a free, installable APK — zero commands, zero cost

You don't need Android Studio, a terminal, or a Play Store account for this.
GitHub will build the APK for you automatically. All done by clicking in a
browser:

1. **Create a free GitHub account** at github.com (skip if you have one).
2. Click **New repository** → name it e.g. `dgmpi-app` → **Create repository**.
3. On the new repo's page, click **"uploading an existing file"** (or
   **Add file → Upload files**) → drag this entire project folder into the
   browser window → **Commit changes**.
   (GitHub's uploader accepts dragged folders and keeps the folder structure —
   no zip extraction or git command needed.)
4. Click the **Actions** tab at the top of the repo. A workflow called
   **"Build DGMPI APK"** starts automatically within a minute or two
   (you can also click **Run workflow** to start it manually).
5. Wait for the green checkmark (usually 3-6 minutes the first time).
6. Click into the finished run → scroll to **Artifacts** → download
   **`DGMPI-debug-apk`** → unzip it → you now have `app-debug.apk`.
7. Share that `.apk` file however you like — your own website, Google Drive,
   WhatsApp/Telegram, a QR code, anywhere. Anyone can install it by opening
   the file on their Android phone and allowing "install from this source"
   when prompted (a normal one-time Android setting, not a bug).

That's it — **completely free, no Play Store account, no payment, no
command line.** This uses the included `.github/workflows/build-apk.yml`,
which builds automatically on every upload.

### Want it signed for a "real" release later (optional, still no Play Store fee required)?
The same workflow can also produce a properly signed release APK/AAB if you
ever add 4 secrets to the repo (**Settings → Secrets and variables →
Actions**) — see the "Build a signed release APK/AAB" section further down.
This is optional; the plain debug APK above is fully installable and shareable
as-is.

---



Production Android app built with **Kotlin + Jetpack Compose (Material 3) + MVVM +
Hilt + Repository Pattern**, fully wired to **Firebase Authentication, Cloud
Firestore, Firebase Storage, and Firebase Cloud Messaging**. All content is
dynamic — nothing about notices, results, routines, ebooks, gallery, students,
teachers, etc. is hardcoded; it all comes from Firestore and updates in the
app automatically the instant Admin changes it.

A companion **Web Admin Panel** (separate delivery, plain HTML/CSS/Bootstrap 5/JS)
manages every collection this app reads from.

---

## 1. Prerequisites

- Android Studio (Koala 2024.1.1 or newer)
- A Firebase project (this build is pre-configured for project **`dgmpi-app`**)
- Internet connection for the first Gradle sync (downloads Gradle 8.7 + all
  AndroidX/Firebase/Hilt dependencies)

## 2. Open & Run

1. Extract this ZIP and open the folder in Android Studio.
2. Let Gradle sync complete.
3. `google-services.json` is already included at `app/google-services.json`
   with the real config for the `dgmpi-app` Firebase project. If you ever
   rotate/replace the Firebase project, download a fresh copy from
   **Firebase Console → Project settings → Your apps → google-services.json**
   and drop it in the same location.
4. Run on an emulator or device with **minSdk 24+**.

## 3. Firebase services used

| Service | Purpose |
|---|---|
| **Authentication** (Email/Password) | Student login. Admin creates each student's login in Firebase Console → Authentication, then links it to their Firestore `students/{id}` document via the `authUid` field (done from the Admin Panel's Student form). |
| **Cloud Firestore** | Every collection listed in `data/remote/FirestorePaths.kt` — notices, results, routines, ebooks, departments, gallery, applications, attendance, evaluations, transcripts, tasks, bookmarks, leaderboard, home content, campus info, events, admission info. |
| **Firebase Storage** | Student/teacher photos, notice attachments, gallery images, event images, ebook covers/PDFs, slider banners, result/transcript PDFs. |
| **Firebase Cloud Messaging** | Push notifications for notices, results, routine changes, events, and application status updates. See `notification/DgmpiMessagingService.kt`. |

## 3.5 Build a signed release APK / AAB (for publishing)

Google Play now requires the **AAB (Android App Bundle)** format for new
apps — `.apk` still works for direct distribution (your own website, other
app stores, sideloading) but not for a new Play Store listing. Steps for both:

### Step 1 — Create your signing key (one-time, do this yourself in a terminal)
```bash
keytool -genkey -v -keystore dgmpi-release.keystore -alias dgmpi -keyalg RSA -keysize 2048 -validity 10000
```
Answer the prompts (name, organization, etc.) and choose two passwords.
**Save this keystore file and its passwords somewhere safe and permanent** —
Google Play requires the exact same keystore for every future update of this
app, forever; losing it means you can never update the app again under the
same listing.

Place the generated `dgmpi-release.keystore` file at the **root of this
project** (next to `settings.gradle.kts`).

### Step 2 — Fill in keystore.properties (for local/Android Studio builds)
```bash
cd app
cp keystore.properties.template keystore.properties
```
Open `app/keystore.properties` and replace `CHANGE_ME` with the real
passwords/alias you chose above. This file is already git-ignored and is
picked up automatically by `app/build.gradle.kts` — no other Gradle changes
needed.

### Step 2b — OR: sign automatically via GitHub Actions (no local build tools needed)
If you're using the zero-command GitHub Actions flow above and want a signed
release APK/AAB instead of (or in addition to) the debug APK:
1. Convert your keystore file to base64 text (this one step still needs a
   command — either run it once on any computer with the keystore file, or
   use GitHub Codespaces' free browser terminal so you never touch your own machine):
   ```bash
   base64 -w0 dgmpi-release.keystore > keystore-base64.txt   # Linux/macOS
   # Windows PowerShell: [Convert]::ToBase64String([IO.File]::ReadAllBytes("dgmpi-release.keystore")) | Out-File keystore-base64.txt
   ```
2. In your GitHub repo: **Settings → Secrets and variables → Actions → New
   repository secret**, add these 4 secrets:
   - `KEYSTORE_BASE64` — paste the contents of `keystore-base64.txt`
   - `KEYSTORE_PASSWORD` — your keystore password
   - `KEY_ALIAS` — e.g. `dgmpi`
   - `KEY_PASSWORD` — your key password
3. Push/upload again (or re-run the workflow from the Actions tab) — it will
   now also produce `DGMPI-release-apk-signed` and `DGMPI-release-aab-signed`
   artifacts alongside the debug one.

### Step 3 — Build from Android Studio (if you'd rather build locally)

**Via the UI (easiest):**
`Build → Generate Signed App Bundle / APK...` → choose **APK** or **Android
App Bundle** → it will detect `keystore.properties` automatically if present,
or let you point at the keystore manually → Release → Finish.

**From the terminal:**
```bash
./gradlew bundleRelease   # -> app/build/outputs/bundle/release/app-release.aab  (for Play Store)
./gradlew assembleRelease # -> app/build/outputs/apk/release/app-release.apk     (for direct install)
```
Both commands need internet access on first run (Gradle downloads
dependencies) and a JDK 17+ installed.

### Publishing checklist
- `applicationId` is already set to `com.dgmpi.app` in `app/build.gradle.kts` —
  this is permanent once published; changing it later means a brand-new listing.
- Bump `versionCode` (integer, must increase every release) and `versionName`
  in `app/build.gradle.kts` before each new upload.
- Replace the placeholder launcher icon (`res/drawable/ic_launcher_foreground.xml`)
  with your final brand mark before your first public release — see the
  "Known limitations" note further down.
- Real `google-services.json` is already in place — nothing else Firebase-side
  is needed for the release build itself.



```
app/src/main/java/com/dgmpi/app/
├── data/
│   ├── model/Models.kt          → Firestore-serializable data classes (@DocumentId, all-default fields)
│   ├── remote/                  → FirestorePaths.kt (collection names) + FirestoreFlowExt.kt (Query -> Flow<Resource<T>> helpers)
│   └── repository/               → one repository interface+impl per feature area, all Firestore/Storage/Auth backed
├── di/                          → FirebaseModule (Firebase singletons), RepositoryModule (Hilt @Binds)
├── notification/                → NotificationHelper + DgmpiMessagingService (FCM)
├── navigation/                  → Routes.kt, DgmpiNavGraph.kt, SearchIndex.kt (global search)
├── ui/
│   ├── components/              → shared cards, gradient header, drawer, icon mapper
│   ├── screens/                 → one package per feature screen (all now Firestore-driven)
│   └── theme/                   → Material 3 navy/white/gold theme (unchanged)
├── util/                        → Resource.kt (Loading/Success/Error wrapper), QrCodeGenerator.kt (real QR for ID card)
└── viewmodel/                   → one @HiltViewModel per screen/feature, exposing StateFlow<Resource<T>>
```

Every screen follows the same pattern: a Hilt ViewModel exposes `StateFlow<Resource<T>>`,
the Composable `collectAsState()`s it and renders Loading / Error / Empty / Success —
see `ui/screens/notice/NoticeBoardScreen.kt` for the clearest example.

## 5. Firestore schema

See `firestore.rules` and `data/remote/FirestorePaths.kt` for the authoritative list.
Key collections and the field shape Admin must write (matches `data/model/Models.kt`
exactly since Firestore's POJO mapper deserializes by field name):

- `students` — one doc per student; **must include `authUid`** matching their
  Firebase Auth account, or login will fail with "No student profile linked".
- `teachers`, `departments`, `facilities`, `gallery`, `events`
- `notices` (fields: title, date, timestampMillis, description, category,
  attachmentUrl, imageUrl, isPinned)
- `results` (searchable by `roll` or `registration`; only returned to the app
  when `published: true`)
- `class_routine`, `exam_routine`, `seat_plan`
- `ebooks`, `ebook_categories`, `bookmarks` (doc id = `{studentId}_{ebookId}`)
- `quiz_categories`, `quiz_questions`, `leaderboard`
- `applications` (student-writable on create only; status/remarks admin-only)
- `attendance`, `case_history`, `evaluations`, `transcripts` — all filtered by `studentId`
- `tasks` — fully owned/CRUD'd by the student from the Diary & Task screen
- `campus_info`, `admission_info`, `home_content` — single documents at id `"current"`
- `admins` — allow-list read by the Admin Panel; doc id = the admin's Auth UID

## 6. Security Rules

`firestore.rules` and `storage.rules` are included and enforce:
- Any signed-in student can **read** shared/institutional collections.
- Only a UID listed in `admins/{uid}` can **write** to shared collections
  (the Admin Panel itself should sign in with an admin account, or — for a
  fully production setup — write through Cloud Functions using the Admin SDK,
  which bypasses these rules entirely).
- Students can only read/write their **own** `tasks`, `bookmarks`, and
  `applications` (create only), and can only update a few whitelisted fields
  on their own `students/{id}` document (photoUrl, phone, address, fcmToken).

Deploy them with the Firebase CLI:
```bash
firebase deploy --only firestore:rules,firestore:indexes,storage
```

## 7. Push Notifications

The app subscribes every device to the `dgmpi_all_students` FCM topic (toggle
in Settings → Push Notifications). **Sending** a push from the Admin Panel
requires a server-side trigger (Admin SDK) since client-side JS cannot hold a
server key securely — a ready-to-deploy Cloud Functions package for this is
included in the **Admin Panel** delivery at `functions/index.js` (auto-sends on
new notice, published result, and application status change).

## 8. Demo / first login

There is no hardcoded demo login anymore — create a real student in:
1. Firebase Console → Authentication → Add user (email + password)
2. Admin Panel → Students → Add Student → paste that user's UID into "Auth UID"

## 9. Known scope notes

- Admit Card upload/storage is already provisioned in Firestore (`admit_cards`)
  and the Admin Panel, ready for a dedicated in-app screen — the original menu
  list didn't include one, so it wasn't added to avoid scope creep beyond what
  was asked; the data layer is ready whenever you want to wire it up.
- `google-services.json`'s Storage bucket format (`dgmpi-app.firebasestorage.app`)
  is the newer Firebase bucket naming — already matched in `FirebaseModule.kt`.
