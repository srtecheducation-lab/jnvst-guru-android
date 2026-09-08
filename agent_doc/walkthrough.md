# Walkthrough - Development Progress

This document tracks the incremental features implemented in the JNVST GURU app.

## Completed Milestones

### 1. Auth & Networking Foundation
- **Supabase Auth:** Integrated Kotlin SDK for email/password login.
- **Persistent Sessions:** App automatically starts on Home if a valid token exists.
- **Auth Interceptor:** Retrofit automatically attaches `Bearer <JWT>` to all student-facing endpoints.
- **Local Dev:** Configured `usesCleartextTraffic` for communication with the Spring Boot backend on localhost (127.0.0.1).

### 2. Practice Selection Flow
- **Sequential UX:** Implemented a student-friendly picker: Subject -> Topic (Optional) -> Difficulty -> Set.
- **Set Mapping:** "Set X" maps directly to backend pagination (Set 1 = page 0).
- **Status Indicators:** `SetSelectionScreen` fetches data from the backend to mark sets as "Attempted".

### 3. Question Engine & Session UI
- **Dynamic Content:** Fetches up to 20 questions from `GET /api/v1/student/arithmetic-questions`.
- **Interaction:** Custom `OptionItem` for MCQ selection with local persistence during "Previous/Next" navigation.
- **Review Mode:** Special UI decorator that highlights correct answers in Green and wrong selections in Red based on backend attempt data.

### 4. Submission & Results
- **API POST:** `PracticeViewModel` collects all selected indices and posts to `/api/v1/student/practice-attempts`.
- **Truth Source:** The Result screen displays counts (`score`, `correctCount`, etc.) exclusively from the backend response.
- **Actions:** Integrated "Review Answers" (jump to first question in Review Mode) and "Re-attempt" (fresh session reset).

### 5. Multilingual & Profile Integration
- **Profile Fetching:** App calls `/student-profiles/me` on startup to determine student preferences.
- **Language Mapping:** Automatically translates profile codes (`bn`) to API parameters (`BENGALI`).
- **Defaulting:** Safely defaults to `ENGLISH` if profile language is missing or invalid.

---

## Verification Results

### Build & Run
- Successfully built the application (Version 1.0.0-dev).
- Verified API connectivity via `adb reverse`.

### Key Flows Tested
1. **Login:** Successfully redirected from Login to Home on fresh install.
2. **Fresh Attempt:** Arithmetic -> Easy -> Set 1 -> Submit. Result counts correctly totaled to question size.
3. **Review:** Tapped Review from Result. Navigated to Question 1 with correct/wrong icons visible.
4. **Language:** Changed profile to `bn` in DB -> App correctly requested questions with `language=BENGALI`.
