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
- **Dependency Injection:** Hilt (Recommended for modern Android apps).
- **Persistence:** Room Database for offline-first capability.
- **Networking:** Retrofit + OkHttp.
- **Auth:** Supabase Auth (Kotlin Client).
- **Navigation:** Jetpack Compose Navigation (Type-safe).

## 5. Backend Tech Stack (Future)
- **Language:** Java
- **Framework:** Spring Boot
- **API Style:** RESTful with well-defined contracts.

## 6. Database
- **Local:** Room (SQLite) for caching and offline practice.
- **Remote (Future):** PostgreSQL.

## 7. UI/UX Principles
- **Material 3:** Strict adherence to Material Design 3 guidelines.
- **Clean UX:** Minimalist, distraction-free interface suitable for young students.
- **Usability:** Large touch targets, clear typography, and intuitive navigation.
- **Accessibility:** Support for screen readers, high contrast, and scalable text.

## 8. Multilingual Strategy
- **Resource Bundling:** Use standard Android `res/values/strings.xml` for all UI text.
- **No Hard-coding:** Absolute prohibition on hard-coded user-facing strings in Kotlin files.
- **L10n Ready:** Architecture must support right-to-left (RTL) and different character sets from Day 1.

## 9. Performance Principles
- **Offline-First:** Critical features (Practice, PYQs) should work without an active internet connection once downloaded.
- **Smooth UI:** Maintain 60/120 FPS for all animations and transitions.
- **Efficient Data:** Optimize Room queries and use Flow for efficient UI updates.

## 10. Scalability Principles
- **Exam Agnostic Core:** The core logic for testing, practice, and progress should be reusable for Class 9 and beyond.
- **Modular Build:** Ability to split features into dynamic delivery modules if the app size grows significantly.

## 11. Navigation
- **Structure:** Bottom Navigation for primary destinations (Home, Practice, Mock Tests, Progress).
- **Flows:** Deep-linking support and nested navigation for complex sections like Mock Tests.

## 12. Screen Specifications
- **Home:** High-fidelity dashboard featuring:
    - **Header:** App name, slogan, day streak indicator, and notification badge.
    - **Selectors:** Quick switchers for Class level and App Language.
    - **Hero Banner:** Motivational card with a direct "Let's Practice" action.
    - **Continue Practice:** Large horizontal card showing current subject/topic progress.
    - **Your Progress:** Grid of metrics (Solved, Accuracy, Study Time, Streak) with weekly filtering.
    - **Action Cards:** Colorful horizontal entries for Subject/Topic practice, PYQs, and Mock Tests.
    - **Navigation:** Persistent bottom bar for Home, Practice, Tests, Progress, and Profile.

## 13. Data Models
- **Subject:** `id`, `name`, `icon`, `description`.
- **Topic:** `id`, `subjectId`, `name`, `order`.
- **Question:** `id`, `topicId`, `content`, `options`, `correctOption`, `explanation`, `type` (Text/Image).
- **Test:** `id`, `title`, `duration`, `questionCount`, `type` (Practice/Mock/PYQ).
- **Result:** `id`, `testId`, `score`, `timestamp`, `questionAnalysis`.

## 14. Coding Rules
- **Separation of Concerns:** No business logic in Composables; no UI dependencies in Repositories.
- **Naming:** Consistent naming for ViewModels (`FeatureViewModel`), Repositories (`FeatureRepository`), and Use Cases (`GetFeatureUseCase`).
- **Testing:** Unit tests for Domain and Data layers; UI tests for critical user flows.
- **KDoc:** Document complex logic and public APIs.

## 15. Dependencies
*(Currently kept minimal)*
- `androidx.compose.*`
- `androidx.room.*`
- `androidx.navigation.*`
- `androidx.hilt.*`
- `kotlinx.coroutines.*`

## 16. Development Roadmap
- **Phase 1:** Core Architecture, Navigation, and Local Data Setup.
- **Phase 2:** Home and Subject-wise Practice (Class 6).
- **Phase 3:** PYQs and Mock Test Engine.
- **Phase 4:** Progress Tracking and Analytics.
- **Phase 5:** Multi-exam support (Class 9).

## 17. Decisions
- **[2026-08-30]:** Project initialized with Clean Architecture and Compose.
- **[2026-08-30]:** Decision to remain backend-agnostic initially, using Room for local data.
- **[2026-08-30]:** All assistant-generated markdown documentation (Plans, Task Lists, Walkthroughs) must be stored exclusively in `agent_doc/` at the project root.

## 18. Documentation Strategy
- **Location:** All `.md` files related to project context, implementation plans, and progress tracking must reside in `agent_doc/` at the project root.
- **Purpose:** Centralize knowledge for the coding assistant to ensure consistency across sessions and features.
- **Updates:** Documents should be updated iteratively as new features are implemented.
