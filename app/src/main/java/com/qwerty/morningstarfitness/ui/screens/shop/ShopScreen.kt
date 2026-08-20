package com.qwerty.morningstarfitness.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.qwerty.morningstarfitness.models.ProductModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.FormField
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import com.qwerty.morningstarfitness.viewmodels.MemberViewModel
import com.qwerty.morningstarfitness.viewmodels.MpesaPaymentViewModel
import com.qwerty.morningstarfitness.viewmodels.ShopViewModel

@Composable
fun ShopScreen(
    shopViewModel: ShopViewModel,
    memberViewModel: MemberViewModel,
    paymentViewModel: MpesaPaymentViewModel,
    onBack: () -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    var showingCheckout by remember { mutableStateOf(false) }
    val cart = shopViewModel.cartItems
    val isProcessing = shopViewModel.isProcessingOrder || paymentViewModel.isProcessing
    val cartTotal = shopViewModel.getCartTotal()
    val itemCount = shopViewModel.getItemCount()
    val memberForm = memberViewModel.memberForm

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PulseColors.Background)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .verticalScroll(rememberScrollState())
                .background(PulseColors.Surface, RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (showingCheckout) showingCheckout = false else onBack()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PulseColors.TextPrimary)
                }
            }

            BrandMark()
            Heading(if (showingCheckout) "Checkout" else "Gym shop")

            Spacer(modifier = Modifier.height(4.dp))

            if (!showingCheckout) {
                Text(
                    text = "Useful gym essentials available for you.",
                    color = PulseColors.TextMuted,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                shopViewModel.products.forEach { product ->
                    ProductRow(
                        product = product,
                        quantity = cart[product.id] ?: 0,
                        onQuantityChange = { newQty ->
                            if (!isProcessing) shopViewModel.updateQuantity(product.id, newQty)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                CartSummary(itemCount, cartTotal)

                if (shopViewModel.orderError != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = shopViewModel.orderError!!, color = PulseColors.Error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Proceed to Checkout",
                    onClick = { 
                        shopViewModel.initiateCheckout()
                        showingCheckout = true 
                    },
                    enabled = itemCount > 0 && !isProcessing
                )
            } else {
                // Checkout Flow
                CheckoutContent(
                    shopViewModel = shopViewModel,
                    memberForm = memberForm,
                    paymentViewModel = paymentViewModel,
                    cartTotal = cartTotal,
                    onOrderSuccess = { orderId ->
                        shopViewModel.finalizeOrder()
                        onOrderSuccess(orderId)
                    },
                    onCancel = {
                        showingCheckout = false
                    }
                )
            }
        }
    }
}

@Composable
fun CartSummary(itemCount: Int, cartTotal: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = if (itemCount == 0) "Cart is empty" else "$itemCount item(s)",
            color = PulseColors.TextPrimary,
            fontSize = 14.sp
        )
        Text(
            text = "KSh $cartTotal",
            color = PulseColors.Accent,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CheckoutContent(
    shopViewModel: ShopViewModel,
    memberForm: com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState?,
    paymentViewModel: MpesaPaymentViewModel,
    cartTotal: Int,
    onOrderSuccess: (String) -> Unit,
    onCancel: () -> Unit
) {
    Column {
        Text(text = "ORDER SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseColors.AccentLime)
        Spacer(modifier = Modifier.height(12.dp))

        SummaryField("Customer", memberForm?.fullName ?: "Unknown")
        SummaryField("Phone", memberForm?.phone ?: "Unknown")
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Fulfilment", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PulseColors.TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))

        FulfilmentOption(
            selected = shopViewModel.fulfilmentMethod == "pickup",
            label = "Front Desk Pickup",
            icon = Icons.Default.Store,
            onClick = { shopViewModel.fulfilmentMethod = "pickup" }
        )
        Spacer(modifier = Modifier.height(8.dp))
        FulfilmentOption(
            selected = shopViewModel.fulfilmentMethod == "delivery",
            label = "Delivery",
            icon = Icons.Default.LocalShipping,
            onClick = { shopViewModel.fulfilmentMethod = "delivery" }
        )

        if (shopViewModel.fulfilmentMethod == "delivery") {
            Spacer(modifier = Modifier.height(12.dp))
            FormField(
                value = shopViewModel.deliveryLocation,
                onValueChange = { shopViewModel.deliveryLocation = it },
                label = "Delivery Location (e.g. Ruiru, Estate Name)"
            )
            FormField(
                value = shopViewModel.deliveryAddress,
                onValueChange = { shopViewModel.deliveryAddress = it },
                label = "Address / Landmark / House No."
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = PulseColors.SurfaceAlt)
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Total", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold)
            Text(text = "KSh $cartTotal", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "PAYMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseColors.AccentLime)
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(text = "M-PESA SANDBOX — DEMO", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "PayBill: 123456", color = PulseColors.TextPrimary, fontSize = 13.sp)
            Text(text = "Account: MORNINGSTAR-DEMO", color = PulseColors.TextPrimary, fontSize = 13.sp)
            Text(text = "Amount: KSh $cartTotal", color = PulseColors.TextPrimary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No real money is transferred in this presentation build.",
                color = PulseColors.TextMuted,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        if (paymentViewModel.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = paymentViewModel.errorMessage!!, color = PulseColors.Error, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = if (paymentViewModel.isProcessing || shopViewModel.isProcessingOrder) "Processing..." else "SIMULATE PAYMENT",
            onClick = {
                shopViewModel.createPendingOrder { success, orderId ->
                    if (success && orderId != null) {
                        paymentViewModel.simulateSuccessfulPayment(
                            amount = cartTotal,
                            purpose = "shop_order",
                            referenceId = orderId,
                            onComplete = { paid ->
                                if (paid) onOrderSuccess(orderId)
                            }
                        )
                    }
                }
            },
            enabled = !(paymentViewModel.isProcessing || shopViewModel.isProcessingOrder)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        GhostButton(
            text = "CANCEL PAYMENT",
            onClick = {
                paymentViewModel.simulateCancelledPayment { 
                    onCancel()
                }
            },
            enabled = !(paymentViewModel.isProcessing || shopViewModel.isProcessingOrder)
        )
    }
}

@Composable
fun SummaryField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, color = PulseColors.TextMuted, fontSize = 11.sp)
        Text(text = value, color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun FulfilmentOption(selected: Boolean, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PulseColors.AccentLime.copy(alpha = 0.1f) else PulseColors.SurfaceAlt)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = PulseColors.AccentLime, unselectedColor = PulseColors.TextMuted)
        )
        Icon(icon, contentDescription = null, tint = if (selected) PulseColors.AccentLime else PulseColors.TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, color = if (selected) PulseColors.TextPrimary else PulseColors.TextMuted, fontSize = 14.sp, fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal)
    }
}

@Composable
private fun ProductRow(
    product: ProductModel,
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrl,
            contentDescription = product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PulseColors.Background)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name, color = PulseColors.TextPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(text = product.category, color = PulseColors.TextMuted, fontSize = 11.sp)
            Text(text = "KSh ${product.priceKsh}", color = PulseColors.Accent, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { if (quantity > 0) onQuantityChange(quantity - 1) }) {
                Text(text = "–", color = PulseColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = quantity.toString(),
                color = PulseColors.TextPrimary,
                fontSize = 14.sp,
                modifier = Modifier.width(20.dp),
            )
            IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                Text(text = "+", color = PulseColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
