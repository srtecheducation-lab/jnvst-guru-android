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
- **Dynamic Content:** Fetches up to 20 questions from `GET /api/v1/student/arithmetic-questions` or `/mat-questions`.
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

### 6. Mental Ability (MAT) Integration
- **Image-based Questions:** Integrated **Coil** for loading MAT figures from public Supabase Storage buckets.
- **Dedicated Layouts:** Implemented a specific 2x2 grid layout for the `ODD_ONE_OUT` topic to improve visual clarity for Class 6 students.
- **Public URL Builder:** Centralized utility `MatImageUrlBuilder` to handle path-to-URL conversion for Supabase assets without extra auth overhead.

### 7. Core Stability & State Management
- **State Reset:** Implemented synchronous session resets in `PracticeViewModel` to prevent data leaking between different practice sets or subjects.
- **Navigation Safety:** Updated the nav graph with robust `popUpTo` logic and query param filtering to prevent "stale" results from appearing after new submissions.
- **Case Sensitivity:** Unified subject identification (e.g., "mat" vs "MAT") to ensure consistent API behavior across all practice modes.

---

## Verification Results

### Build & Run
- Successfully built the application (Version 1.1.0-dev).
- Verified MAT image loading on physical POCO device.

### Key Flows Tested
1. **Login:** Successfully redirected from Login to Home on fresh install.
2. **MAT Topic Selection:** Fetched 10 topics from `/mat-topics` and displayed them with correct sort order.
3. **Submission:** Arithmetic and MAT sessions both navigate correctly to Result screen upon completion.
4. **Review:** "Review Answers" correctly resets to Question 1 with correct/wrong visual indicators.
