# Implementation Plan - Phase 2: Login & Arithmetic API Integration

Implement Supabase Login and connect the Arithmetic Questions API to the existing practice flow.

## User Review Required
> [!IMPORTANT]
> - I will add a `LoginScreen` as the new start destination if no session exists.
> - I will implement a Retrofit `Interceptor` to automatically attach the Supabase JWT to all backend requests.
> - I will update the `PracticeViewModel` to fetch real questions from the backend when starting an Arithmetic session.

## Proposed Changes

### 1. Authentication Layer
#### [NEW] [AuthRepository.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/domain/repository/AuthRepository.kt)
- Interface for Login and Session management.

#### [NEW] [AuthRepositoryImpl.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/data/repository/AuthRepositoryImpl.kt)
- Implementation using `SupabaseClient`.

#### [NEW] [LoginUiState.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/auth/LoginUiState.kt)
- State for the Login screen (email, password, loading, error).

#### [NEW] [LoginViewModel.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/auth/LoginViewModel.kt)
- Handle login logic and session checking.

#### [NEW] [LoginScreen.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/auth/LoginScreen.kt)
- Compose UI for authentication.

---

### 2. Networking Layer
#### [NEW] [ArithmeticApiService.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/data/network/ArithmeticApiService.kt)
- Retrofit interface for `GET /api/v1/student/arithmetic-questions`.

#### [NEW] [NetworkModule.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/data/network/NetworkModule.kt)
- Singleton provider for Retrofit with `AuthInterceptor`.

#### [NEW] [Resource.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/domain/util/Resource.kt)
- Wrapper for API states (Success, Error, Loading).

---

### 3. Data & Domain Layer
#### [MODIFY] [PracticeRepository.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/domain/repository/PracticeRepository.kt)
- Add `getArithmeticQuestions(type, difficulty, page, size)` method.

#### [MODIFY] [PracticeRepositoryImpl.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/data/repository/PracticeRepositoryImpl.kt)
- Implement API call using `ArithmeticApiService`.

---

### 4. Practice Flow Integration
#### [MODIFY] [PracticeViewModel.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/practice/PracticeViewModel.kt)
- Add state and logic to fetch and hold questions.

#### [MODIFY] [JnvstNavGraph.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/navigation/JnvstNavGraph.kt)
- Add `Login` route.
- Implement conditional `startDestination` based on auth state.

#### [MODIFY] [TopicDetailScreen.kt](file:///C:/JNVST_GURU/Code/App/app/src/main/java/com/jnvst/guru/ui/practice/TopicDetailScreen.kt)
- Update "Start Practice" to trigger question fetching.

## Verification Plan
- Build and run the app.
- **Login:** Verify successful authentication redirects to Home.
- **Practice:** Select Arithmetic -> Topic -> Start Practice -> Verify real questions are fetched from the local backend (using `adb reverse`).
- Verify JWT header presence in logs (if logging enabled) or by successful 200 OK responses.
