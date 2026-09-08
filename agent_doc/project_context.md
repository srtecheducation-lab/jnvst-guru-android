# JNVST GURU - Project Documentation & Strategy

This document serves as the comprehensive source of truth for the JNVST GURU Android application. It outlines the vision, architecture, and technical standards to ensure a maintainable and scalable product.

---

## 1. Product Vision
To provide a high-quality, structured, and accessible digital learning environment for students preparing for the Jawahar Navodaya Vidyalaya Selection Test (JNVST). The goal is to democratize quality preparation materials, starting with Class 6 and eventually expanding to Class 9 and other competitive entrance exams.

## 2. Current Scope (Class 6)
- **Home:** Personalized dashboard with quick access to recent activities and progress summaries.
- **Practice:** 
    - **Subject-wise:** Mental Ability, Arithmetic, and Language.
    - **Topic-wise:** Granular practice for specific concepts within subjects.
    - **Selection Flow:** Home -> Subject -> Difficulty (Easy/Med/Hard) -> Set (20 questions each).
- **Previous Year Questions (PYQs):** Authentic question papers from past exams for realistic practice.
- **Mock Tests:** Full-length timed tests to simulate the actual exam environment.
- **Progress:** Detailed analytics and performance tracking to identify strengths and weaknesses.

## 3. Architecture
- **Pattern:** Clean Architecture with MVVM.
- **Layers:**
    - **Presentation Layer:** Jetpack Compose UI, ViewModels, and State Management.
    - **Domain Layer:** Business logic, Use Cases, and Entity models (Pure Kotlin).
    - **Data Layer:** Repository implementations, Room Database, API Client (Remote Data Source), and Auth Foundation.
- **Modularity:** Feature-based modularization to support future exam types (Class 9, etc.) without disrupting core logic.

## 4. Android Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3.
- **Concurrency:** Kotlin Coroutines and Flow for reactive data streams.
- **Dependency Injection:** Hilt (Future).
- **Persistence:** Room Database (Future local caching).
- **Networking:** Retrofit + OkHttp.
- **Auth:** Supabase Auth (Kotlin Client).
- **Navigation:** Jetpack Compose Navigation (Type-safe query params).

## 5. Backend Tech Stack
- **Language:** Java
- **Framework:** Spring Boot
- **Database:** PostgreSQL (via Supabase).
- **API Style:** RESTful with Bearer JWT protection.

## 6. UI/UX Principles
- **Material 3:** Strict adherence to Material Design 3 guidelines.
- **Class 6 Friendly:** Minimalist, high-contrast, large touch targets, and student-centric wording.
- **Dynamic Feedback:** Real-time selection indicators and visual review modes (Green/Red) for answers.

## 7. Multilingual Strategy
- **Resource Bundling:** Use standard Android `res/values/strings.xml` for all UI text.
- **Profile-based Language:** Student's `preferredLanguage` (en/bn) is fetched from the profile API.
- **API Mapping:** App maps `en` -> `ENGLISH` and `bn` -> `BENGALI` for backend question requests.

## 8. Development Roadmap
- **Phase 1:** Networking Foundation & Auth (Supabase).
- **Phase 2:** Navigation & V1 UI Screens.
- **Phase 3:** Question Engine & Practice Flow.
- **Phase 4:** Submission & Result Analytics.
- **Phase 5:** Review Mode & Set Status Persistence.
- **Phase 6:** Language Support & Profile Integration.

## 9. Navigation Structure
- **Root:** `LoginScreen` (if no session) or `HomeScreen`.
- **Practice Session:** `PracticeSessionScreen` handles both fresh attempts and Review Mode.
- **Results:** `PracticeResultScreen` displays backend stats and provides Review/Re-attempt actions.
- **Type-Safety:** Route arguments include `mode`, `subjectId`, `topicId`, `difficulty`, and `page`.
