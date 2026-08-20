package com.qwerty.morningstarfitness.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qwerty.morningstarfitness.ui.screens.attendance.AttendanceScreen
import com.qwerty.morningstarfitness.ui.screens.entry.EntryScreen
import com.qwerty.morningstarfitness.ui.screens.entry.ManualEntryScreen
import com.qwerty.morningstarfitness.ui.screens.entry.PasswordEntryScreen
import com.qwerty.morningstarfitness.ui.screens.entry.QrEntryScreen
import com.qwerty.morningstarfitness.ui.screens.home.HomeScreen
import com.qwerty.morningstarfitness.ui.screens.launch.OnboardingScreen
import com.qwerty.morningstarfitness.ui.screens.launch.SplashScreen
import com.qwerty.morningstarfitness.ui.screens.payment.PaymentScreen
import com.qwerty.morningstarfitness.ui.screens.plan.PlanScreen
import com.qwerty.morningstarfitness.ui.screens.profile.ProfileScreen
import com.qwerty.morningstarfitness.ui.screens.registration.RegistrationScreen
import com.qwerty.morningstarfitness.ui.screens.shop.OrderSuccessScreen
import com.qwerty.morningstarfitness.ui.screens.shop.ShopScreen
import com.qwerty.morningstarfitness.ui.screens.success.SuccessScreen
import com.qwerty.morningstarfitness.viewmodels.*
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AppNavHost(modifier: Modifier = Modifier, navController: NavHostController = rememberNavController(), startDestination: String = ROUTE_SPLASH) {
    val authViewModel: AuthViewModel = viewModel()
    val memberViewModel: MemberViewModel = viewModel()
    val attendanceViewModel: AttendanceViewModel = viewModel()
    val shopViewModel: ShopViewModel = viewModel()
    val paymentViewModel: MpesaPaymentViewModel = viewModel()

    fun firstName(): String = memberViewModel.memberForm?.fullName?.split(" ")?.firstOrNull()?.ifBlank { "there" } ?: "there"

    fun goHome(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            memberViewModel.refreshFromFirebase()
            attendanceViewModel.fetchAttendanceHistory()
            navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(ROUTE_ENTRY) {
            EntryScreen(
                onCreateAccount = { navController.navigate(ROUTE_REGISTRATION) },
                onLogin = { navController.navigate(ROUTE_LOGIN) },
                onEnterGym = {
                    // Gym entry is always the QR fast-lane. It never opens dashboard login.
                    navController.navigate(ROUTE_SCAN_ENTRY)
                }
            )
        }

        composable(ROUTE_SPLASH) {
            SplashScreen(onFinished = {
                val destination = if (authViewModel.currentUser() != null) ROUTE_HOME else ROUTE_ONBOARDING
                navController.navigate(destination) { popUpTo(ROUTE_SPLASH) { inclusive = true } }
            })
        }

        composable(ROUTE_ONBOARDING) {
            OnboardingScreen(onFinished = {
                navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_ONBOARDING) { inclusive = true } }
            })
        }

        composable(ROUTE_LOGIN) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            ManualEntryScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { goHome(scope) },
                onForgotPassword = { email -> authViewModel.sendPasswordReset(email) { _, message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() } },
                onPasswordSubmitted = { email, password ->
                    authViewModel.signIn(
                        email,
                        password,
                        onSuccess = { goHome(scope) },
                        onError = { message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
                    )
                }
            )
        }

        composable(ROUTE_SCAN_ENTRY) {
            val scope = rememberCoroutineScope()
            var isRecording by remember { mutableStateOf(false) }
            var checkInMessage by remember { mutableStateOf<String?>(null) }
            var isMemberDataLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                // First load the locally saved member. This works for both freshly registered
                // members and returning members and does not require normal dashboard login.
                memberViewModel.loadLocalMember()

                // When a Firebase session exists, refresh from RTDB so the display is current.
                if (authViewModel.currentUser() != null) {
                    memberViewModel.refreshFromFirebase()
                    attendanceViewModel.fetchAttendanceHistory()
                }
                isMemberDataLoading = false
            }

            QrEntryScreen(
                qrCodeValue = memberViewModel.ensureMembershipQr(),
                fullName = memberViewModel.memberForm?.fullName,
                memberId = memberViewModel.memberId,
                status = memberViewModel.getMembershipStatus(),
                membershipExpiry = memberViewModel.membershipExpiry,
                isLoading = isMemberDataLoading,
                isRecording = isRecording,
                checkInMessage = checkInMessage,
                onBack = { navController.popBackStack() },
                onPasswordEntry = { navController.navigate(ROUTE_PASSWORD_ENTRY) },
                onRecordCheckIn = {
                    if (!isRecording) {
                        scope.launch {
                            if (authViewModel.currentUser() == null) {
                                checkInMessage = "Show this QR to the front-desk scanner to record your visit."
                                return@launch
                            }
                            isRecording = true
                            val recorded = attendanceViewModel.recordCheckIn()
                            checkInMessage = when {
                                recorded -> {
                                    attendanceViewModel.fetchAttendanceHistory()
                                    "Check-in recorded for today."
                                }
                                attendanceViewModel.lastCheckInError != null -> attendanceViewModel.lastCheckInError ?: "Could not save the visit."
                                else -> "You are already checked in today."
                            }
                            isRecording = false
                        }
                    }
                }
            )
        }

        composable(ROUTE_PASSWORD_ENTRY) {
            val scope = rememberCoroutineScope()
            var isVerifying by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            PasswordEntryScreen(
                memberName = memberViewModel.memberForm?.fullName,
                isVerifying = isVerifying,
                errorMessage = errorMessage,
                onBack = { navController.popBackStack() },
                onVerify = { password ->
                    scope.launch {
                        isVerifying = true
                        errorMessage = null
                        val currentUser = authViewModel.currentUser()
                        val authenticated = if (currentUser != null) {
                            authViewModel.verifyCurrentUserPassword(password)
                        } else {
                            val email = memberViewModel.memberForm?.email.orEmpty()
                            authViewModel.signInForGymEntry(email, password)
                        }

                        if (authenticated) {
                            memberViewModel.refreshFromFirebase()
                            val recorded = attendanceViewModel.recordCheckIn()
                            if (recorded) {
                                attendanceViewModel.fetchAttendanceHistory()
                                navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_PASSWORD_ENTRY) { inclusive = true } }
                            } else {
                                errorMessage = attendanceViewModel.lastCheckInError ?: "You are already checked in today."
                            }
                        } else {
                            errorMessage = "Incorrect password. Please try again."
                        }
                        isVerifying = false
                    }
                }
            )
        }

        composable(ROUTE_REGISTRATION) { RegistrationScreen(onContinue = { form -> memberViewModel.updateMemberForm(form); navController.navigate(ROUTE_PLAN) }) }

        composable(ROUTE_PLAN) {
            PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_PAYMENT) })
        }

        composable(ROUTE_PAYMENT) {
            val scope = rememberCoroutineScope()
            PaymentScreen(
                plan = memberViewModel.selectedPlan,
                paymentMethod = memberViewModel.paymentMethod,
                isProcessing = paymentViewModel.isProcessing || authViewModel.isProcessing,
                errorMessage = paymentViewModel.errorMessage ?: authViewModel.registrationError,
                onMethodChange = { memberViewModel.updatePaymentMethod(it) },
                onBack = { navController.popBackStack() },
                onSimulateSuccess = {
                    val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                    paymentViewModel.simulateSuccessfulPayment(amount = plan.priceKsh, purpose = "membership_registration", referenceId = "REG-${UUID.randomUUID()}") { success ->
                        if (success) scope.launch {
                            val form = memberViewModel.memberForm ?: return@launch
                            memberViewModel.prepareMembership(plan)
                            val qr = memberViewModel.generateQrCode()
                            if (authViewModel.createUser(form, plan, qr, memberViewModel.memberId, memberViewModel.membershipStart, memberViewModel.membershipExpiry)) {
                                memberViewModel.completePaymentLocally(plan)
                                navController.navigate(ROUTE_SUCCESS)
                            }
                        }
                    }
                },
                onSimulateCancel = { paymentViewModel.simulateCancelledPayment { } }
            )
        }

        composable(ROUTE_RENEW) { PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_RENEW_PAYMENT) }) }

        composable(ROUTE_RENEW_PAYMENT) {
            PaymentScreen(
                plan = memberViewModel.selectedPlan,
                paymentMethod = memberViewModel.paymentMethod,
                isProcessing = paymentViewModel.isProcessing || memberViewModel.isRenewing,
                errorMessage = paymentViewModel.errorMessage ?: memberViewModel.renewalError,
                onMethodChange = { memberViewModel.updatePaymentMethod(it) },
                onBack = { navController.popBackStack() },
                onSimulateSuccess = {
                    val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                    paymentViewModel.simulateSuccessfulPayment(amount = plan.priceKsh, purpose = "membership_renewal", referenceId = "RENEW-${UUID.randomUUID()}", planId = plan.id, planLabel = plan.label, planDuration = plan.durationMonths) { success ->
                        if (success) memberViewModel.renewMembership(plan) { renewed ->
                            if (renewed) navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } }
                        }
                    }
                },
                onSimulateCancel = { paymentViewModel.simulateCancelledPayment { } }
            )
        }

        composable(ROUTE_SUCCESS) {
            SuccessScreen(firstName = firstName(), plan = memberViewModel.selectedPlan, qrCodeValue = memberViewModel.qrCodeValue ?: "", onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } } })
        }

        composable(ROUTE_HOME) {
            LaunchedEffect(Unit) { memberViewModel.syncWithFirebase(); attendanceViewModel.fetchAttendanceHistory() }
            HomeScreen(
                firstName = firstName(), plan = memberViewModel.selectedPlan,
                onLogout = {
                    authViewModel.signOut(); memberViewModel.clearLocalData(); attendanceViewModel.clearHistory()
                    navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_HOME) { inclusive = true } }
                },
                onOpenShop = { navController.navigate(ROUTE_SHOP) },
                onOpenProfile = { navController.navigate(ROUTE_PROFILE) },
                onOpenAttendance = { navController.navigate(ROUTE_ATTENDANCE) },
                onScanEntry = { navController.navigate(ROUTE_SCAN_ENTRY) },
                onGetStarted = { navController.navigate(ROUTE_REGISTRATION) },
                onRenewMembership = { navController.navigate(ROUTE_RENEW) },
                membershipStart = memberViewModel.membershipStart,
                membershipExpiry = memberViewModel.membershipExpiry,
                attendanceCount = attendanceViewModel.attendanceHistory.size,
                isLoaded = memberViewModel.isLoaded
            )
        }

        composable(ROUTE_ATTENDANCE) { AttendanceScreen(entries = attendanceViewModel.attendanceHistory, onBack = { navController.popBackStack() }) }
        composable(ROUTE_SHOP) {
            ShopScreen(shopViewModel = shopViewModel, memberViewModel = memberViewModel, paymentViewModel = paymentViewModel,
                onBack = { navController.popBackStack() }, onOrderSuccess = { orderId -> navController.navigate("${ROUTE_ORDER_SUCCESS}/$orderId") { popUpTo(ROUTE_SHOP) { inclusive = true } } })
        }
        composable("${ROUTE_ORDER_SUCCESS}/{orderId}") { backStackEntry ->
            OrderSuccessScreen(orderId = backStackEntry.arguments?.getString("orderId") ?: "", viewModel = shopViewModel,
                onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } } })
        }
        composable(ROUTE_PROFILE) {
            val context = LocalContext.current
            ProfileScreen(memberForm = memberViewModel.memberForm, plan = memberViewModel.selectedPlan, onBack = { navController.popBackStack() },
                onSave = { updated -> memberViewModel.updateProfile(updated) { _, message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() } })
        }
    }
}
