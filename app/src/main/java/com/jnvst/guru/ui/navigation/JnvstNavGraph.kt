package com.jnvst.guru.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jnvst.guru.ui.practice.DifficultySelectionScreen
import com.jnvst.guru.ui.practice.SetSelectionScreen
import com.jnvst.guru.ui.home.HomeScreen
import com.jnvst.guru.ui.auth.LoginScreen
import com.jnvst.guru.ui.auth.LoginViewModel
import com.jnvst.guru.ui.auth.SignupScreen
import com.jnvst.guru.ui.auth.SignupViewModel
import com.jnvst.guru.ui.practice.*
import com.jnvst.guru.ui.tests.*
import com.jnvst.guru.ui.progress.*
import com.jnvst.guru.ui.profile.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Practice : Screen("practice")
    object SubjectSelection : Screen("subject_selection/{mode}") {
        fun createRoute(mode: String) = "subject_selection/$mode"
    }
    object TopicSelection : Screen("topic_selection/{subjectId}") {
        fun createRoute(subjectId: String) = "topic_selection/$subjectId"
    }
    object DifficultySelection : Screen("difficulty_selection/{mode}/{subjectId}?topicId={topicId}") {
        fun createRoute(mode: String, subjectId: String, topicId: String? = null) = 
            "difficulty_selection/$mode/$subjectId" + (if (topicId != null) "?topicId=$topicId" else "")
    }
    object SetSelection : Screen("set_selection/{mode}/{subjectId}/{difficulty}?topicId={topicId}") {
        fun createRoute(mode: String, subjectId: String, difficulty: String, topicId: String? = null) = 
            "set_selection/$mode/$subjectId/$difficulty" + (if (topicId != null) "?topicId=$topicId" else "")
    }
    object PracticeSession : Screen("practice_session/{mode}/{subjectId}/{difficulty}/{page}?topicId={topicId}") {
        fun createRoute(mode: String, subjectId: String, difficulty: String, page: Int, topicId: String? = null) = 
            "practice_session/$mode/$subjectId/$difficulty/$page" + (if (topicId != null) "?topicId=$topicId" else "")
    }
    object PracticeResult : Screen("practice_result")

    object TopicDetail : Screen("topic_detail/{topicId}") {
        fun createRoute(topicId: String) = "topic_detail/$topicId"
    }
    
    // Tests Flow
    object Tests : Screen("tests")
    object MockTestList : Screen("mock_test_list")
    object TestInstructions : Screen("test_instructions/{testId}") {
        fun createRoute(testId: String) = "test_instructions/$testId"
    }
    object TestInProgress : Screen("test_in_progress/{testId}") {
        fun createRoute(testId: String) = "test_in_progress/$testId"
    }
    object TestResult : Screen("test_result/{testId}") {
        fun createRoute(testId: String) = "test_result/$testId"
    }
    
    // Progress Flow
    object Progress : Screen("progress")
    object SubjectProgress : Screen("subject_progress/{subjectId}") {
        fun createRoute(subjectId: String) = "subject_progress/$subjectId"
    }
    object TopicProgress : Screen("topic_progress/{subjectId}") {
        fun createRoute(subjectId: String) = "topic_progress/$subjectId"
    }
    
    // Profile
    object Profile : Screen("profile")
    object Login : Screen("login")
    object Signup : Screen("signup")
}

@Composable
fun JnvstNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val practiceViewModel: PracticeViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()
    val uiState by loginViewModel.uiState.collectAsState()

    if (uiState.isInitializing) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (uiState.isLoginSuccessful) Screen.Home.route else Screen.Login.route,
            modifier = modifier
        ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    practiceViewModel.loadStudentProfile()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Signup.route)
                },
                viewModel = loginViewModel
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onSubjectWisePracticeClick = { navController.navigate(Screen.SubjectSelection.createRoute("subject")) },
                onTopicWisePracticeClick = { navController.navigate(Screen.Practice.route) }
            )
        }

        composable(Screen.Practice.route) {
            PracticeScreen(
                onSubjectWiseClick = { navController.navigate(Screen.SubjectSelection.createRoute("subject")) },
                onTopicWiseClick = { navController.navigate(Screen.SubjectSelection.createRoute("topic")) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SubjectSelection.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "subject"
            SubjectSelectionScreen(
                mode = mode,
                viewModel = practiceViewModel,
                onSubjectSelected = { subjectId ->
                    if (mode == "topic") {
                        navController.navigate(Screen.TopicSelection.createRoute(subjectId))
                    } else {
                        navController.navigate(Screen.DifficultySelection.createRoute(mode, subjectId))
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TopicSelection.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            TopicSelectionScreen(
                subjectId = subjectId,
                viewModel = practiceViewModel,
                onTopicSelected = { topicId ->
                    navController.navigate(Screen.DifficultySelection.createRoute("topic", subjectId, topicId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DifficultySelection.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("topicId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: ""
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val topicId = backStackEntry.arguments?.getString("topicId")
            DifficultySelectionScreen(
                onDifficultySelected = { difficulty ->
                    navController.navigate(Screen.SetSelection.createRoute(mode, subjectId, difficulty, topicId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SetSelection.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("topicId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: ""
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            val topicId = backStackEntry.arguments?.getString("topicId")
            SetSelectionScreen(
                mode = mode,
                subjectId = subjectId,
                topicId = topicId,
                difficulty = difficulty,
                viewModel = practiceViewModel,
                onSetSelected = { page, completed ->
                    if (completed) {
                        navController.navigate(Screen.PracticeResult.route + "?mode=$mode&subjectId=$subjectId&difficulty=$difficulty&page=$page" + (if (topicId != null) "&topicId=$topicId" else ""))
                    } else {
                        navController.navigate(Screen.PracticeSession.createRoute(mode, subjectId, difficulty, page, topicId))
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PracticeSession.route,
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType },
                navArgument("subjectId") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType },
                navArgument("page") { type = NavType.IntType },
                navArgument("topicId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: ""
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            val difficulty = backStackEntry.arguments?.getString("difficulty") ?: ""
            val page = backStackEntry.arguments?.getInt("page") ?: 0
            val topicId = backStackEntry.arguments?.getString("topicId")
            
            androidx.compose.runtime.LaunchedEffect(subjectId, topicId, difficulty, page) {
                if (!practiceViewModel.isReviewMode.value) {
                    practiceViewModel.startPractice(mode, subjectId, topicId, difficulty, page)
                }
            }

            PracticeSessionScreen(
                viewModel = practiceViewModel,
                onBackClick = { navController.popBackStack() },
                onFinish = { 
                    // Use the current session's metadata to ensure navigation uses correct params
                    val route = Screen.PracticeResult.route + 
                        "?mode=$mode&subjectId=$subjectId&difficulty=$difficulty&page=$page" + 
                        (if (topicId != null && topicId != "null") "&topicId=$topicId" else "")
                    navController.navigate(route) {
                        popUpTo(Screen.PracticeSession.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.PracticeResult.route + "?mode={mode}&subjectId={subjectId}&difficulty={difficulty}&page={page}&topicId={topicId}",
            arguments = listOf(
                navArgument("mode") { type = NavType.StringType; nullable = true },
                navArgument("subjectId") { type = NavType.StringType; nullable = true },
                navArgument("difficulty") { type = NavType.StringType; nullable = true },
                navArgument("page") { type = NavType.IntType; defaultValue = -1 },
                navArgument("topicId") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode")
            val subjectId = backStackEntry.arguments?.getString("subjectId")
            val difficulty = backStackEntry.arguments?.getString("difficulty")
            val page = backStackEntry.arguments?.getInt("page") ?: -1
            val topicId = backStackEntry.arguments?.getString("topicId")

            androidx.compose.runtime.LaunchedEffect(mode, subjectId, difficulty, page) {
                if (practiceViewModel.submissionResult.value == null &&
                    mode != null && subjectId != null && difficulty != null && page != -1) {
                    practiceViewModel.loadLatestAttempt(mode, subjectId, topicId, difficulty, page)
                }
            }

            PracticeResultScreen(
                viewModel = practiceViewModel,
                onReviewClick = {
                    if (mode != null && subjectId != null && difficulty != null && page != -1) {
                        practiceViewModel.enterReviewMode(mode, subjectId, topicId, difficulty, page)
                        navController.navigate(Screen.PracticeSession.createRoute(mode, subjectId, difficulty, page, topicId))
                    }
                },
                onReattemptClick = {
                    if (mode != null && subjectId != null && difficulty != null && page != -1) {
                        practiceViewModel.startPractice(mode, subjectId, topicId, difficulty, page)
                        navController.navigate(Screen.PracticeSession.createRoute(mode, subjectId, difficulty, page, topicId))
                    }
                },
                onHomeClick = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TopicDetail.route,
            arguments = listOf(navArgument("topicId") { type = NavType.StringType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getString("topicId") ?: ""
            TopicDetailScreen(
                topicId = topicId,
                viewModel = practiceViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Tests Flow
        composable(Screen.Tests.route) {
            TestsScreen(
                onMockTestsClick = { navController.navigate(Screen.MockTestList.route) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.MockTestList.route) {
            MockTestListScreen(
                viewModel = practiceViewModel,
                onTestClick = { testId -> navController.navigate(Screen.TestInstructions.createRoute(testId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TestInstructions.route,
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId") ?: ""
            TestInstructionsScreen(
                testId = testId,
                viewModel = practiceViewModel,
                onStartTestClick = { navController.navigate(Screen.TestInProgress.createRoute(testId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TestInProgress.route,
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getString("testId") ?: ""
            TestInProgressScreen(
                testId = testId,
                onSubmitClick = { navController.navigate(Screen.TestResult.createRoute(testId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TestResult.route,
            arguments = listOf(navArgument("testId") { type = NavType.StringType })
        ) {
            TestResultScreen(
                onHomeClick = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Progress Flow
        composable(Screen.Progress.route) {
            ProgressScreen(
                onSubjectClick = { subjectId -> navController.navigate(Screen.SubjectProgress.createRoute(subjectId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.SubjectProgress.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getString("subjectId") ?: ""
            SubjectProgressScreen(
                subjectId = subjectId,
                onTopicClick = { navController.navigate(Screen.TopicProgress.createRoute(subjectId)) },
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.TopicProgress.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
        ) {
            TopicProgressScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    loginViewModel.logout()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
}
