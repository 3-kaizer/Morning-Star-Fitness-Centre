package com.qwerty.morningstarfitness.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
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
    val notificationViewModel: NotificationViewModel = viewModel()

    fun firstName(): String = memberViewModel.memberForm?.fullName?.split(" ")?.firstOrNull()?.ifBlank { "there" } ?: "there"

    fun goHome(scope: kotlinx.coroutines.CoroutineScope, context: android.content.Context) {
        scope.launch {
            when (memberViewModel.refreshFromFirebase()) {
                MemberRefreshStatus.SUCCESS -> {
                    attendanceViewModel.fetchAttendanceHistory()
                    paymentHistoryViewModel.refresh()
                    notificationViewModel.refresh()
                    navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } }
                }
                MemberRefreshStatus.NOT_FOUND -> {
                    Toast.makeText(context, "Member profile not found. Please visit the front desk.", Toast.LENGTH_LONG).show()
                }
                MemberRefreshStatus.FAILURE -> {
                    if (memberViewModel.memberForm != null) {
                        attendanceViewModel.fetchAttendanceHistory()
                        paymentHistoryViewModel.refresh()
                        notificationViewModel.refresh()
                        navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } }
                    } else {
                        Toast.makeText(context, "Could not load your member details. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
        composable(ROUTE_ENTRY) {
            val scope = rememberCoroutineScope()
            val context = LocalContext.current
            val isAuthenticated = authViewModel.currentUser() != null
            EntryScreen(
                isAuthenticated = isAuthenticated,
                onCreateAccount = { navController.navigate(ROUTE_REGISTRATION) },
                onLogin = {
                    if (authViewModel.currentUser() != null) {
                        goHome(scope, context)
                    } else {
                        navController.navigate(ROUTE_LOGIN)
                    }
                },
                onEnterGym = {
                    if (authViewModel.currentUser() == null) {
                        navController.navigate(ROUTE_LOGIN)
                    } else {
                        scope.launch {
                            when (memberViewModel.refreshFromFirebase()) {
                                MemberRefreshStatus.SUCCESS -> {
                                    navController.navigate(ROUTE_SCAN_ENTRY)
                                }
                                MemberRefreshStatus.NOT_FOUND -> {
                                    Toast.makeText(context, "Member profile not found. Please visit the front desk.", Toast.LENGTH_LONG).show()
                                }
                                MemberRefreshStatus.FAILURE -> {
                                    if (memberViewModel.memberForm != null) {
                                        navController.navigate(ROUTE_SCAN_ENTRY)
                                    } else {
                                        Toast.makeText(context, "Could not load your member details. Please try again.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
        composable(ROUTE_SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_SPLASH) { inclusive = true } }
            })
        }
        composable(ROUTE_ONBOARDING) { OnboardingScreen(onFinished = { navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_ONBOARDING) { inclusive = true } } }) }
        composable(ROUTE_LOGIN) {
            val context = LocalContext.current
            ManualEntryScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_LOGIN) { inclusive = true } } },
                onForgotPassword = { email -> authViewModel.sendPasswordReset(email) { _, message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() } },
                onPasswordSubmitted = { email, password ->
                    authViewModel.signIn(
                        email,
                        password,
                        onSuccess = { navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_LOGIN) { inclusive = true } } },
                        onError = { message -> Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
                    )
                }
            )
        }
        composable(ROUTE_SCAN_ENTRY) {
            var isMemberDataLoading by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                memberViewModel.loadLocalMember()
                if (authViewModel.currentUser() != null) {
                    when (memberViewModel.refreshFromFirebase()) {
                        MemberRefreshStatus.SUCCESS, MemberRefreshStatus.FAILURE -> attendanceViewModel.fetchAttendanceHistory()
                        MemberRefreshStatus.NOT_FOUND -> Unit
                    }
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
                onBack = { navController.popBackStack() },
                onPasswordEntry = { navController.navigate(ROUTE_PASSWORD_ENTRY) }
            )
        }
        composable(ROUTE_PASSWORD_ENTRY) {
            val scope = rememberCoroutineScope()
            var isVerifying by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            PasswordEntryScreen(
                memberName = memberViewModel.memberForm?.fullName,
                securityQuestion = null,
                isVerifying = isVerifying,
                errorMessage = errorMessage,
                onBack = { navController.popBackStack() },
                onVerify = { password ->
                    scope.launch {
                        isVerifying = true
                        errorMessage = null
                        val wasSignedIn = authViewModel.currentUser() != null
                        val authenticated = if (wasSignedIn) {
                            authViewModel.verifyCurrentUserPassword(password)
                        } else {
                            authViewModel.signInForGymEntry(memberViewModel.memberForm?.email.orEmpty(), password)
                        }
                        if (!authenticated) {
                            errorMessage = "Incorrect account password. Please try again."
                        } else {
                            val recorded = attendanceViewModel.recordCheckIn()
                            if (recorded) {
                                memberViewModel.refreshFromFirebase()
                                attendanceViewModel.fetchAttendanceHistory()
                                notificationViewModel.refresh()
                                navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } }
                            } else {
                                errorMessage = attendanceViewModel.lastCheckInError ?: "Entry could not be recorded. Please try again."
                            }
                        }
                        isVerifying = false
                    }
                }
            )
        }
        composable(ROUTE_REGISTRATION) { RegistrationScreen(onContinue = { form -> memberViewModel.updateMemberForm(form); navController.navigate(ROUTE_PLAN) }) }
        composable(ROUTE_PLAN) { PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_PAYMENT) }) }
        composable(ROUTE_PAYMENT) {
            val scope = rememberCoroutineScope()
            val form = memberViewModel.memberForm
            PaymentScreen(
                plan = memberViewModel.selectedPlan,
                phone = form?.phone.orEmpty(),
                isProcessing = paymentViewModel.isProcessing || authViewModel.isProcessing,
                paymentStatus = paymentViewModel.paymentStatus,
                receipt = paymentViewModel.mpesaReceipt,
                errorMessage = paymentViewModel.errorMessage ?: authViewModel.registrationError,
                isPresentationSandbox = paymentViewModel.isPresentationSandbox,
                onBack = { navController.popBackStack() },
                onPay = {
                    val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                    val reference = "REG-${UUID.randomUUID().toString().replace("-", "").take(8).uppercase()}"
                    paymentViewModel.startStkPayment(phone = form?.phone.orEmpty(), amount = plan.priceKsh, purpose = "membership_registration", referenceId = reference) { success ->
                        if (success) scope.launch {
                            val currentForm = memberViewModel.memberForm ?: return@launch
                            memberViewModel.prepareMembership(plan)
                            val qr = memberViewModel.generateQrCode(forceNew = true)
                            if (authViewModel.createUser(currentForm, plan, qr, memberId = memberViewModel.memberId, membershipStart = memberViewModel.membershipStart, membershipExpiry = memberViewModel.membershipExpiry, paymentReference = reference, mpesaReceipt = paymentViewModel.mpesaReceipt, profilePictureUrl = currentForm.profilePictureUrl)) {
                                memberViewModel.completePaymentLocally()
                                paymentHistoryViewModel.refresh()
                                notificationViewModel.refresh()
                                navController.navigate(ROUTE_SUCCESS)
                            }
                        }
                    }
                },
                onCancel = { paymentViewModel.reset() }
            )
        }
        composable(ROUTE_RENEW) { PlanScreen(onBack = { navController.popBackStack() }, onContinue = { plan -> memberViewModel.updateSelectedPlan(plan); navController.navigate(ROUTE_RENEW_PAYMENT) }) }
        composable(ROUTE_RENEW_PAYMENT) {
            val scope = rememberCoroutineScope()
            PaymentScreen(
                plan = memberViewModel.selectedPlan,
                phone = memberViewModel.memberForm?.phone.orEmpty(),
                isProcessing = paymentViewModel.isProcessing || memberViewModel.isRenewing,
                paymentStatus = paymentViewModel.paymentStatus,
                receipt = paymentViewModel.mpesaReceipt,
                errorMessage = paymentViewModel.errorMessage ?: memberViewModel.renewalError,
                isPresentationSandbox = paymentViewModel.isPresentationSandbox,
                onBack = { navController.popBackStack() },
                onPay = {
                    val plan = memberViewModel.selectedPlan ?: return@PaymentScreen
                    paymentViewModel.startStkPayment(phone = memberViewModel.memberForm?.phone.orEmpty(), amount = plan.priceKsh, purpose = "membership_renewal", referenceId = "RENEW-${UUID.randomUUID().toString().replace("-", "").take(8).uppercase()}", planId = plan.id, planLabel = plan.label, planDuration = plan.durationMonths) { success ->
                        if (success) scope.launch {
                            memberViewModel.refreshFromFirebase()
                            paymentHistoryViewModel.refresh()
                            notificationViewModel.refresh()
                            paymentViewModel.reset()
                            navController.navigate(ROUTE_SUCCESS) { popUpTo(ROUTE_HOME) { inclusive = true } }
                        }
                    }
                },
                onCancel = { paymentViewModel.reset() }
            )
        }
        composable(ROUTE_SUCCESS) { SuccessScreen(firstName = firstName(), plan = memberViewModel.selectedPlan, qrCodeValue = memberViewModel.qrCodeValue ?: "", onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_ENTRY) { inclusive = true } } }) }
        composable(ROUTE_HOME) {
            val scope = rememberCoroutineScope()
            var isRefreshing by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                memberViewModel.refreshFromFirebase()
                attendanceViewModel.fetchAttendanceHistory()
                paymentHistoryViewModel.refresh()
                notificationViewModel.refresh()
            }
            HomeScreen(
                firstName = firstName(),
                profilePictureUrl = memberViewModel.memberForm?.profilePictureUrl,
                plan = memberViewModel.selectedPlan,
                onLogout = {
                    authViewModel.signOut()
                    memberViewModel.clearLocalData()
                    attendanceViewModel.clearHistory()
                    notificationViewModel.refresh()
                    navController.navigate(ROUTE_ENTRY) { popUpTo(ROUTE_HOME) { inclusive = true } }
                },
                onOpenShop = { navController.navigate(ROUTE_SHOP) },
                onOpenProfile = { navController.navigate(ROUTE_PROFILE) },
                onOpenAttendance = { navController.navigate(ROUTE_ATTENDANCE) },
                onOpenTrainers = { navController.navigate(ROUTE_TRAINERS) },
                onOpenGymStatus = { navController.navigate(ROUTE_GYM_STATUS) },
                onOpenNotifications = { navController.navigate(ROUTE_NOTIFICATIONS) },
                onOpenPaymentHistory = { navController.navigate(ROUTE_PAYMENT_HISTORY) },
                onOpenMemberCard = { navController.navigate(ROUTE_MEMBER_CARD) },
                onScanEntry = { navController.navigate(ROUTE_SCAN_ENTRY) },
                onGetStarted = { navController.navigate(ROUTE_REGISTRATION) },
                onRenewMembership = { navController.navigate(ROUTE_RENEW) },
                membershipStart = memberViewModel.membershipStart,
                membershipExpiry = memberViewModel.membershipExpiry,
                attendanceCount = attendanceViewModel.attendanceHistory.size,
                trainerCount = 4,
                notificationCount = notificationViewModel.unreadCount,
                isLoaded = memberViewModel.isLoaded,
                isRefreshing = isRefreshing,
                onRefresh = {
                    if (!isRefreshing) scope.launch {
                        isRefreshing = true
                        try {
                            memberViewModel.refreshFromFirebase()
                            attendanceViewModel.fetchAttendanceHistory()
                            paymentHistoryViewModel.refresh()
                            notificationViewModel.refresh()
                        } finally {
                            isRefreshing = false
                        }
                    }
                }
            )
        }
        composable(ROUTE_ATTENDANCE) {
            AttendanceScreen(entries = attendanceViewModel.attendanceHistory, monthlyVisits = attendanceViewModel.visitsThisMonth, isLoading = attendanceViewModel.isLoading, loadError = attendanceViewModel.loadError, onRefresh = { attendanceViewModel.fetchAttendanceHistory() }, onBack = { navController.popBackStack() })
        }
        composable(ROUTE_TRAINERS) { TrainersScreen(onBack = { navController.popBackStack() }, onTrainerSelected = { trainer: TrainerSummary -> navController.navigate(ROUTE_TRAINER_DETAIL + "?name=${trainer.name}&specialty=${trainer.specialty}&schedule=${trainer.schedule}&experience=${trainer.experience}&bio=${java.net.URLEncoder.encode(trainer.bio, "UTF-8")}") }) }
        composable(ROUTE_TRAINER_DETAIL + "?name={name}&specialty={specialty}&schedule={schedule}&experience={experience}&bio={bio}") { backStackEntry -> TrainerDetailScreen(name = backStackEntry.arguments?.getString("name") ?: "Trainer", specialty = backStackEntry.arguments?.getString("specialty") ?: "Fitness", schedule = backStackEntry.arguments?.getString("schedule") ?: "Today", experience = backStackEntry.arguments?.getString("experience") ?: "", bio = backStackEntry.arguments?.getString("bio")?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: "", onBack = { navController.popBackStack() }) }
        composable(ROUTE_GYM_STATUS) { GymStatusScreen(trainerCount = 4, onBack = { navController.popBackStack() }) }
        composable(ROUTE_PAYMENT_HISTORY) {
            PaymentHistoryScreen(entries = paymentHistoryViewModel.entries, membershipPlan = memberViewModel.selectedPlan?.label, membershipExpiry = memberViewModel.membershipExpiry, onRenew = { navController.navigate(ROUTE_RENEW) }, onBack = { navController.popBackStack() })
        }
        composable(ROUTE_MEMBER_CARD) { MemberCardScreen(fullName = memberViewModel.memberForm?.fullName ?: "Member", memberId = memberViewModel.memberId, plan = memberViewModel.selectedPlan?.label, status = memberViewModel.getMembershipStatus(), expiry = memberViewModel.membershipExpiry, qrCode = memberViewModel.qrCodeValue, onBack = { navController.popBackStack() }) }
        composable(ROUTE_NOTIFICATIONS) {
            LaunchedEffect(Unit) { notificationViewModel.refresh() }
            NotificationsScreen(messages = notificationViewModel.notifications, onBack = { navController.popBackStack() })
        }
        composable(ROUTE_SHOP) { ShopScreen(shopViewModel = shopViewModel, memberViewModel = memberViewModel, paymentViewModel = paymentViewModel, onBack = { navController.popBackStack() }, onOrderSuccess = { orderId -> notificationViewModel.refresh(); navController.navigate("${ROUTE_ORDER_SUCCESS}/$orderId") { popUpTo(ROUTE_SHOP) { inclusive = true } } }) }
        composable("${ROUTE_ORDER_SUCCESS}/{orderId}") { backStackEntry -> OrderSuccessScreen(orderId = backStackEntry.arguments?.getString("orderId") ?: "", viewModel = shopViewModel, onContinue = { navController.navigate(ROUTE_HOME) { popUpTo(ROUTE_HOME) { inclusive = true } } }) }
        composable(ROUTE_PROFILE) { val context = LocalContext.current; ProfileScreen(memberForm = memberViewModel.memberForm, plan = memberViewModel.selectedPlan, onBack = { navController.popBackStack() }, onSave = { updated -> memberViewModel.updateProfile(updated) { _, message -> Toast.makeText(context, message, Toast.LENGTH_SHORT).show() } }) }
    }
}
