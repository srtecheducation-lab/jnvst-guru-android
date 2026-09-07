# Walkthrough - Login & Arithmetic API Integration

I have successfully implemented the Login flow using Supabase Auth and connected the Arithmetic Questions API to the practice session flow.

## Changes Made

### 1. Authentication Layer
- **AuthRepository:** Created `AuthRepository` and `AuthRepositoryImpl` to handle Supabase Login, Session checking, and Logout.
- **Login UI:** Built a modern `LoginScreen` with email/password inputs, loading indicators, and error handling.
- **Session Management:** Updated the navigation graph to automatically redirect to `LoginScreen` if no session is active.

### 2. Networking Layer
- **ArithmeticApiService:** Defined the Retrofit interface for fetching questions from the Spring Boot backend.
- **Auth Interceptor:** Implemented a global `authInterceptor` in `NetworkModule` that automatically attaches the Supabase JWT (`Authorization: Bearer <token>`) to every outgoing request.
- **Error Handling:** Introduced a `Resource` wrapper for clean API state management (Loading, Success, Error).

### 3. Practice Flow Integration
- **Question Engine:** Updated `PracticeViewModel` to fetch a real 20-question set from the `/student/arithmetic-questions` endpoint.
- **Mapping:** Implemented DTO-to-Domain mapping to transform backend data into the UI-safe `Question` model.
- **Navigation Wiring:** Connected the "Start Practice" CTA to trigger real data fetching based on the selected topic/subject.

### 4. Question Engine Implementation
- **PracticeSessionScreen:** Implemented a new student-friendly practice UI that renders real questions from the backend.
- **Dynamic Options:** The screen dynamically renders four text options (A, B, C, D) based on the backend response.
- **Selection Persistence:** Selecting an option updates the domain model in the ViewModel, preserving choices during navigation.
- **State Handling:** Integrated visual feedback for `Loading`, `Error`, and `Empty` question sets.
- **Navigation:** Implemented "Previous" and "Next" logic with boundary checks (e.g., hiding Previous on the first question).

### 5. Practice Submission Integration (Phase 5)
- **Submission Logic:** Implemented `submitAttempt()` in `PracticeViewModel` which collects all selections (including `null` for unanswered) and posts to the backend.
- **Backend Sync:** Attached the real selection metadata (mode, subject, difficulty, page, topic) to the `POST /api/v1/student/practice-attempts` request.
- **Result Screen:** Created `PracticeResultScreen` which acts as the source of truth by displaying stats directly from the backend response (`score`, `correctCount`, etc.).
- **UI Flow:** The "Submit" button dynamically appears on the last question, triggering a loading state before navigating to the results.

## Verification Results

### Build & Run
- Successfully built the application.
- Verified that the `NetworkModule` correctly uses `10.0.2.2` for emulator-to-localhost communication.

### Login Flow
1. App starts -> Checks Supabase session.
2. If no session -> Show `LoginScreen`.
3. Successful login -> Redirects to Home.

### API Integration
1. Navigate to Arithmetic -> Fractions -> Start Practice.
2. The app calls `GET /api/v1/student/arithmetic-questions?questionType=FRACTION&difficulty=EASY&page=0&size=20`.
3. The request includes the valid Supabase JWT in the headers.

> [!TIP]
> To test on a physical device, ensure you run `adb reverse tcp:8080 tcp:8080` and update `NetworkModule.BASE_URL` to `http://127.0.0.1:8080/`.
