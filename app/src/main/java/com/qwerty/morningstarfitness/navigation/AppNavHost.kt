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
import com.qwerty.morningstarfitness.ui.screens.gym.GymStatusScreen
import com.qwerty.morningstarfitness.ui.screens.home.HomeScreen
import com.qwerty.morningstarfitness.ui.screens.launch.OnboardingScreen
import com.qwerty.morningstarfitness.ui.screens.launch.SplashScreen
import com.qwerty.morningstarfitness.ui.screens.member.MemberCardScreen
import com.qwerty.morningstarfitness.ui.screens.notifications.NotificationItem
import com.qwerty.morningstarfitness.ui.screens.notifications.NotificationsScreen
import com.qwerty.morningstarfitness.ui.screens.payment.PaymentHistoryScreen
import com.qwerty.morningstarfitness.ui.screens.payment.PaymentScreen
import com.qwerty.morningstarfitness.ui.screens.plan.PlanScreen
import com.qwerty.morningstarfitness.ui.screens.profile.ProfileScreen
import com.qwerty.morningstarfitness.ui.screens.registration.RegistrationScreen
import com.qwerty.morningstarfitness.ui.screens.shop.OrderSuccessScreen
import com.qwerty.morningstarfitness.ui.screens.shop.ShopScreen
import com.qwerty.morningstarfitness.ui.screens.success.SuccessScreen
import com.qwerty.morningstarfitness.ui.screens.trainers.TrainerDetailScreen
import com.qwerty.morningstarfitness.ui.screens.trainers.TrainerSummary
import com.qwerty.morningstarfitness.ui.screens.trainers.TrainersScreen
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
    val paymentHistoryViewModel: PaymentHistoryViewModel = viewModel()

    fun firstName(): String = memberViewModel.memberForm?.fullName?.split(" ")?.firstOrNull()?.ifBlank { "there" } ?: "there"
    fun goHome(scope: kotlinx.coroutines.CoroutineScope) {
        scope.launch {
            memberViewModel.refreshFromFirebase()
            attendanceViewModel.fetchAttendanceHistory()
            paymentHistoryViewModel.refresh()
            navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(ROUTE_ENTRY) {
            EntryScreen(onCreateAccount = { navController.navigate(ROUTE_REGISTRATION) }, onLogin = { navController.navigate(ROUTE_LOGIN) }, onEnterGym = { navController.navigate(ROUTE_SCAN_ENTRY) })
        }
        composable(ROUTE_SPLASH) { SplashScreen(onFinished = { val destination = if (authViewModel.currentUser() != null) ROUTE_HOME else ROUTE_ONBOARDING; navController.navigate(destination) { popUpTo(ROUTE_SPLASH) { inclusive = true } } }) }
        composable(ROUTE_ONBOARDING) { OnboardingScreen(onFinished = { navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_ONBOARDING) { inclusive = true } } }) }

        composable(ROUTE_LOGIN) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            ManualEntryScreen(onBack = { navController.popBackStack() }, onSuccess = { goHome(scope) }, onForgotPassword = { email -> authViewModel.sendPasswordReset(email) { _, message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() } }, onPasswordSubmitted = { email, password -> authViewModel.signIn(email, password, onSuccess = { goHome(scope) }, onError = { message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() }) })
        }

        composable(ROUTE_SCAN_ENTRY) {
            val scope = rememberCoroutineScope()
            var isRecording by remember { mutableStateOf(false) }
            var checkInMessage by remember { mutableStateOf<String?>(null) }
            var isMemberDataLoading by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                // Always restore the remembered member first so Enter the Gym works even after logout.
                memberViewModel.loadLocalMember()
                // Then refresh from Firebase when a session is available, keeping the displayed data current.
                if (authViewModel.currentUser() != null) {
                    memberViewModel.refreshFromFirebase()
                    attendanceViewModel.fetchAttendanceHistory()
                }
                isMemberDataLoading = false
            }
            QrEntryScreen(qrCodeValue = memberViewModel.ensureMembershipQr(), fullName = memberViewModel.memberForm?.fullName, memberId = memberViewModel.memberId, status = memberViewModel.getMembershipStatus(), membershipExpiry = memberViewModel.membershipExpiry, isLoading = isMemberDataLoading, isRecording = isRecording, checkInMessage = checkInMessage, onBack = { navController.popBackStack() }, onPasswordEntry = { navController.navigate(ROUTE_PASSWORD_ENTRY) }, onRecordCheckIn = {
                if (!isRecording) scope.launch {
                    if (authViewModel.currentUser() == null) { checkInMessage = "Show this QR to the front-desk scanner to record your visit."; return@launch }
                    isRecording = true
                    val recorded = attendanceViewModel.recordCheckIn()
                    checkInMessage = when { recorded -> { attendanceViewModel.fetchAttendanceHistory(); "Check-in recorded for today." }; attendanceViewModel.lastCheckInError != null -> attendanceViewModel.lastCheckInError ?: "Could not save the visit."; else -> "You are already checked in today." }
                    isRecording = false
                }
            })
        }

        composable(ROUTE_PASSWORD_ENTRY) {
            val scope = rememberCoroutineScope(); var isVerifying by remember { mutableStateOf(false) }; var errorMessage by remember { mutableStateOf<String?>(null) }
            PasswordEntryScreen(memberName = memberViewModel.memberForm?.fullName, isVerifying = isVerifying, errorMessage = errorMessage, onBack = { navController.popBackStack() }, onVerify = { password ->
                scope.launch {
                    isVerifying = true; errorMessage = null
                    val currentUser = authViewModel.currentUser()
                    val authenticated = if (currentUser != null) authViewModel.verifyCurrentUserPassword(password) else authViewModel.signInForGymEntry(memberViewModel.memberForm?.email.orEmpty(), password)
                    if (authenticated) {
                        memberViewModel.refreshFromFirebase(); val recorded = attendanceViewModel.recordCheckIn()
                        if (recorded) { attendanceViewModel.fetchAttendanceHistory(); navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_PASSWORD_ENTRY) { inclusive = true } } } else errorMessage = attendanceViewModel.lastCheckInError ?: "You are already checked in today."
                    } else errorMessage = "Incorrect password. Please try again."
                    isVerifying = false
                }
            })
        }

        composable(ROUTE_REGISTRATION) { RegistrationScreen(onContinue = { form -> memberViewModel.updateMemberForm(form); navController.navigate(ROUTE_PLAN) }) }
        composable(ROUTE_PLAN) { PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_PAYMENT) }) }
        composable(ROUTE_PAYMENT) {
            val scope = rememberCoroutineScope()
            PaymentScreen(plan = memberViewModel.selectedPlan, paymentMethod = memberViewModel.paymentMethod, isProcessing = paymentViewModel.isProcessing || authViewModel.isProcessing, errorMessage = paymentViewModel.errorMessage ?: authViewModel.registrationError, onMethodChange = { memberViewModel.updatePaymentMethod(it) }, onBack = { navController.popBackStack() }, onSimulateSuccess = {
                val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                paymentViewModel.simulateSuccessfulPayment(amount = plan.priceKsh, purpose = "membership_registration", referenceId = "REG-${UUID.randomUUID()}") { success -> if (success) scope.launch { val form = memberViewModel.memberForm ?: return@launch; memberViewModel.prepareMembership(plan); val qr = memberViewModel.generateQrCode(forceNew = true); if (authViewModel.createUser(form, plan, qr, memberViewModel.memberId, memberViewModel.membershipStart, memberViewModel.membershipExpiry)) { memberViewModel.completePaymentLocally(plan); paymentHistoryViewModel.refresh(); navController.navigate(ROUTE_SUCCESS) } } }
            }, onSimulateCancel = { paymentViewModel.simulateCancelledPayment { } })
        }
        composable(ROUTE_RENEW) { PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_RENEW_PAYMENT) }) }
        composable(ROUTE_RENEW_PAYMENT) {
            PaymentScreen(plan = memberViewModel.selectedPlan, paymentMethod = memberViewModel.paymentMethod, isProcessing = paymentViewModel.isProcessing || memberViewModel.isRenewing, errorMessage = paymentViewModel.errorMessage ?: memberViewModel.renewalError, onMethodChange = { memberViewModel.updatePaymentMethod(it) }, onBack = { navController.popBackStack() }, onSimulateSuccess = {
                val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                paymentViewModel.simulateSuccessfulPayment(amount = plan.priceKsh, purpose = "membership_renewal", referenceId = "RENEW-${UUID.randomUUID()}", planId = plan.id, planLabel = plan.label, planDuration = plan.durationMonths) { success -> if (success) memberViewModel.renewMembership(plan) { renewed -> if (renewed) { paymentHistoryViewModel.refresh(); navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } } } } }
            }, onSimulateCancel = { paymentViewModel.simulateCancelledPayment { } })
        }
        composable(ROUTE_SUCCESS) { SuccessScreen(firstName = firstName(), plan = memberViewModel.selectedPlan, qrCodeValue = memberViewModel.qrCodeValue ?: "", onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } } }) }

        composable(ROUTE_HOME) {
            LaunchedEffect(Unit) { memberViewModel.syncWithFirebase(); attendanceViewModel.fetchAttendanceHistory(); paymentHistoryViewModel.refresh() }
            HomeScreen(firstName = firstName(), plan = memberViewModel.selectedPlan, onLogout = { authViewModel.signOut(); memberViewModel.clearLocalData(); attendanceViewModel.clearHistory(); navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_HOME) { inclusive = true } } }, onOpenShop = { navController.navigate(ROUTE_SHOP) }, onOpenProfile = { navController.navigate(ROUTE_PROFILE) }, onOpenAttendance = { navController.navigate(ROUTE_ATTENDANCE) }, onOpenTrainers = { navController.navigate(ROUTE_TRAINERS) }, onOpenGymStatus = { navController.navigate(ROUTE_GYM_STATUS) }, onOpenNotifications = { navController.navigate(ROUTE_NOTIFICATIONS) }, onOpenPaymentHistory = { navController.navigate(ROUTE_PAYMENT_HISTORY) }, onOpenMemberCard = { navController.navigate(ROUTE_MEMBER_CARD) }, onScanEntry = { navController.navigate(ROUTE_SCAN_ENTRY) }, onGetStarted = { navController.navigate(ROUTE_REGISTRATION) }, onRenewMembership = { navController.navigate(ROUTE_RENEW) }, membershipStart = memberViewModel.membershipStart, membershipExpiry = memberViewModel.membershipExpiry, attendanceCount = attendanceViewModel.attendanceHistory.size, trainerCount = 4, notificationCount = 2, isLoaded = memberViewModel.isLoaded)
        }

        composable(ROUTE_ATTENDANCE) { AttendanceScreen(entries = attendanceViewModel.attendanceHistory, onBack = { navController.popBackStack() }) }
        composable(ROUTE_TRAINERS) { TrainersScreen(onBack = { navController.popBackStack() }, onTrainerSelected = { trainer: TrainerSummary -> navController.navigate(ROUTE_TRAINER_DETAIL + "?name=${trainer.name}&specialty=${trainer.specialty}&schedule=${trainer.schedule}") }) }
        composable(ROUTE_TRAINER_DETAIL + "?name={name}&specialty={specialty}&schedule={schedule}") { backStackEntry -> TrainerDetailScreen(name = backStackEntry.arguments?.getString("name") ?: "Trainer", specialty = backStackEntry.arguments?.getString("specialty") ?: "Fitness", schedule = backStackEntry.arguments?.getString("schedule") ?: "Today", onBack = { navController.popBackStack() }) }
        composable(ROUTE_GYM_STATUS) { GymStatusScreen(trainerCount = 4, onBack = { navController.popBackStack() }) }
        composable(ROUTE_PAYMENT_HISTORY) { PaymentHistoryScreen(entries = paymentHistoryViewModel.entries, onBack = { navController.popBackStack() }) }
        composable(ROUTE_MEMBER_CARD) { MemberCardScreen(fullName = memberViewModel.memberForm?.fullName ?: "Member", memberId = memberViewModel.memberId, plan = memberViewModel.selectedPlan?.label, status = memberViewModel.getMembershipStatus(), expiry = memberViewModel.membershipExpiry, qrCode = memberViewModel.qrCodeValue, onBack = { navController.popBackStack() }) }
        composable(ROUTE_NOTIFICATIONS) {
            val daysLeft = memberViewModel.membershipExpiry?.let { expiry -> try { val d = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).parse(expiry); if (d != null) java.util.concurrent.TimeUnit.MILLISECONDS.toDays(d.time - System.currentTimeMillis()).toInt().coerceAtLeast(0) else 0 } catch (_: Exception) { 0 } } ?: 0
            NotificationsScreen(messages = listOf(NotificationItem("Membership status", if (memberViewModel.getMembershipStatus() == "Expired") "Your membership has expired. Renew to restore access." else "$daysLeft day${if (daysLeft == 1) "" else "s"} remaining on your membership.", "Today"), NotificationItem("Trainers on duty", "Four trainers are scheduled on the gym floor today.", "Today"), NotificationItem("Gym status", "Morning Star is open from 6:00 AM to 10:00 PM.", "Today")), onBack = { navController.popBackStack() })
        }
        composable(ROUTE_SHOP) { ShopScreen(shopViewModel = shopViewModel, memberViewModel = memberViewModel, paymentViewModel = paymentViewModel, onBack = { navController.popBackStack() }, onOrderSuccess = { orderId -> navController.navigate("${ROUTE_ORDER_SUCCESS}/$orderId") { popUpTo(ROUTE_SHOP) { inclusive = true } } }) }
        composable("${ROUTE_ORDER_SUCCESS}/{orderId}") { backStackEntry -> OrderSuccessScreen(orderId = backStackEntry.arguments?.getString("orderId") ?: "", viewModel = shopViewModel, onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } } }) }
        composable(ROUTE_PROFILE) { val context = LocalContext.current; ProfileScreen(memberForm = memberViewModel.memberForm, plan = memberViewModel.selectedPlan, onBack = { navController.popBackStack() }, onSave = { updated -> memberViewModel.updateProfile(updated) { _, message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() } }) }
    }
}
