package com.qwerty.morningstarfitness.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.qwerty.morningstarfitness.models.ProductModel
import com.qwerty.morningstarfitness.ui.components.BrandMark
import com.qwerty.morningstarfitness.ui.components.GhostButton
import com.qwerty.morningstarfitness.ui.components.Heading
import com.qwerty.morningstarfitness.ui.components.PrimaryButton
import com.qwerty.morningstarfitness.ui.components.RemoteImageLoader
import com.qwerty.morningstarfitness.ui.theme.PulseColors
import com.qwerty.morningstarfitness.viewmodels.MemberViewModel
import com.qwerty.morningstarfitness.viewmodels.MpesaPaymentViewModel
import com.qwerty.morningstarfitness.viewmodels.ShopViewModel

@Composable
fun ShopScreen(shopViewModel: ShopViewModel, memberViewModel: MemberViewModel, paymentViewModel: MpesaPaymentViewModel, onBack: () -> Unit, onOrderSuccess: (String) -> Unit) {
    var showingCheckout by remember { mutableStateOf(false) }
    val cart = shopViewModel.cartItems
    val isProcessing = shopViewModel.isProcessingOrder || paymentViewModel.isProcessing
    val cartTotal = shopViewModel.getCartTotal()
    val itemCount = shopViewModel.getItemCount()
    val memberForm = memberViewModel.memberForm
    val context = LocalContext.current
    val imageLoader = remember(context) { RemoteImageLoader.create(context) }

    // Warm the shared Coil disk/memory cache while the shop screen is opening.
    LaunchedEffect(imageLoader) {
        RemoteImageLoader.preload(context, imageLoader, shopViewModel.products.mapNotNull { it.imageUrl })
    }

    Box(Modifier.fillMaxSize().background(PulseColors.Background).padding(20.dp), contentAlignment = Alignment.TopCenter) {
        Column(Modifier.fillMaxWidth().widthIn(max = 420.dp).verticalScroll(rememberScrollState()).background(PulseColors.Surface, RoundedCornerShape(20.dp)).padding(24.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { if (showingCheckout) showingCheckout = false else onBack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = PulseColors.TextPrimary) } }
            BrandMark()
            Heading(if (showingCheckout) "Checkout" else "Gym shop")
            Spacer(Modifier.height(4.dp))
            if (!showingCheckout) {
                Text("Useful gym essentials available for you.", color = PulseColors.TextMuted, fontSize = 14.sp)
                Spacer(Modifier.height(20.dp))
                shopViewModel.products.forEach { product ->
                    ProductRow(product, cart[product.id] ?: 0, imageLoader) { newQty -> if (!isProcessing) shopViewModel.updateQuantity(product.id, newQty) }
                    Spacer(Modifier.height(10.dp))
                }
                Spacer(Modifier.height(12.dp))
                CartSummary(itemCount, cartTotal)
                if (shopViewModel.orderError != null) { Spacer(Modifier.height(10.dp)); Text(shopViewModel.orderError!!, color = PulseColors.Error, fontSize = 12.sp) }
                Spacer(Modifier.height(16.dp))
                PrimaryButton(text = "Proceed to Checkout", onClick = { shopViewModel.initiateCheckout(); showingCheckout = true }, enabled = itemCount > 0 && !isProcessing)
            } else {
                CheckoutContent(shopViewModel, memberForm, paymentViewModel, cartTotal, { orderId -> shopViewModel.finalizeOrder(); onOrderSuccess(orderId) }) { showingCheckout = false }
            }
        }
    }
}

@Composable
fun CartSummary(itemCount: Int, cartTotal: Int) {
    Row(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(if (itemCount == 0) "Cart is empty" else "$itemCount item(s)", color = PulseColors.TextPrimary, fontSize = 14.sp)
        Text("KSh $cartTotal", color = PulseColors.Accent, fontFamily = FontFamily.Monospace, fontSize = 14.sp)
    }
}

@Composable
fun CheckoutContent(shopViewModel: ShopViewModel, memberForm: com.qwerty.morningstarfitness.ui.screens.registration.MemberFormState?, paymentViewModel: MpesaPaymentViewModel, cartTotal: Int, onOrderSuccess: (String) -> Unit, onCancel: () -> Unit) {
    Column {
        Text("ORDER SUMMARY", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseColors.AccentLime)
        Spacer(Modifier.height(12.dp))
        SummaryField("Customer", memberForm?.fullName ?: "Unknown")
        SummaryField("Phone", memberForm?.phone ?: "Unknown")
        Spacer(Modifier.height(16.dp))
        Text("Pickup", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = PulseColors.TextPrimary)
        Spacer(Modifier.height(8.dp))
        FulfilmentOption(true, "Front Desk Pickup", Icons.Default.Store) { shopViewModel.fulfilmentMethod = "pickup" }
        Spacer(Modifier.height(8.dp))
        Text("Please collect your order at the Morning Star Fitness Centre front desk after payment is confirmed.", color = PulseColors.TextMuted, fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = PulseColors.SurfaceAlt)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", color = PulseColors.TextPrimary, fontWeight = FontWeight.Bold); Text("KSh $cartTotal", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace) }
        Spacer(Modifier.height(24.dp))
        Text("PAYMENT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PulseColors.AccentLime)
        Spacer(Modifier.height(12.dp))
        Column(Modifier.fillMaxWidth().background(PulseColors.SurfaceAlt, RoundedCornerShape(12.dp)).padding(16.dp)) {
            Text("M-PESA DARAjA SANDBOX", color = PulseColors.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Amount: KSh $cartTotal", color = PulseColors.TextPrimary, fontSize = 13.sp)
            Text("Number: ${memberForm?.phone ?: "Not available"}", color = PulseColors.TextPrimary, fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("This presentation build uses a mock STK flow and will not charge money.", color = PulseColors.TextMuted, fontSize = 11.sp)
        }
        if (paymentViewModel.paymentStatus != null) { Spacer(Modifier.height(10.dp)); Text("Payment: ${paymentViewModel.paymentStatus!!.uppercase()}", color = PulseColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
        if (paymentViewModel.mpesaReceipt != null) { Spacer(Modifier.height(6.dp)); Text("Receipt: ${paymentViewModel.mpesaReceipt}", color = PulseColors.TextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace) }
        if (paymentViewModel.errorMessage != null) { Spacer(Modifier.height(12.dp)); Text(paymentViewModel.errorMessage!!, color = PulseColors.Error, fontSize = 12.sp) }
        Spacer(Modifier.height(24.dp))
        PrimaryButton(text = if (paymentViewModel.isProcessing || shopViewModel.isProcessingOrder) "Processing..." else "PAY WITH M-PESA", onClick = {
            shopViewModel.createPendingOrder { success, orderId ->
                if (success && orderId != null) {
                    paymentViewModel.startStkPayment(phone = memberForm?.phone.orEmpty(), amount = cartTotal, purpose = "shop_order", referenceId = orderId) { paid ->
                        if (paid) onOrderSuccess(orderId)
                    }
                }
            }
        }, enabled = !(paymentViewModel.isProcessing || shopViewModel.isProcessingOrder) && memberForm?.phone?.isNotBlank() == true)
        Spacer(Modifier.height(8.dp))
        GhostButton(text = "CANCEL", onClick = { onCancel() }, enabled = !(paymentViewModel.isProcessing || shopViewModel.isProcessingOrder))
    }
}

@Composable
fun SummaryField(label: String, value: String) { Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text(label, color = PulseColors.TextMuted, fontSize = 11.sp); Text(value, color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium) } }

@Composable
fun FulfilmentOption(selected: Boolean, label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(PulseColors.AccentLime.copy(alpha = 0.1f)).clickable { onClick() }.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = PulseColors.AccentLime, unselectedColor = PulseColors.TextMuted))
        Icon(icon, null, tint = PulseColors.AccentLime, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, color = PulseColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProductRow(product: ProductModel, quantity: Int, imageLoader: ImageLoader, onQuantityChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PulseColors.Surface, RoundedCornerShape(16.dp))
            .border(1.dp, PulseColors.Border, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProductImage(product, imageLoader)
        
        Spacer(Modifier.width(16.dp))
        
        Column(Modifier.weight(1f)) {
            Text(
                text = product.name,
                color = PulseColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = product.category.uppercase(),
                color = PulseColors.Accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
            product.description?.let {
                Text(
                    text = it,
                    color = PulseColors.TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }
            Text(
                text = "KSh ${product.priceKsh}",
                color = PulseColors.TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = { onQuantityChange(quantity + 1) },
                modifier = Modifier.size(32.dp).background(PulseColors.Accent.copy(alpha = 0.15f), CircleShape)
            ) {
                Text("+", color = PulseColors.Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Text(
                text = quantity.toString(),
                color = PulseColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            IconButton(
                onClick = { if (quantity > 0) onQuantityChange(quantity - 1) },
                enabled = quantity > 0,
                modifier = Modifier.size(32.dp).background(if (quantity > 0) PulseColors.SurfaceAlt else PulseColors.SurfaceAlt.copy(alpha = 0.5f), CircleShape)
            ) {
                Text("–", color = if (quantity > 0) PulseColors.TextPrimary else PulseColors.TextMuted, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProductImage(product: ProductModel, imageLoader: ImageLoader) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(product.imageUrl)
            .crossfade(true)
            .memoryCacheKey(product.imageUrl)
            .diskCacheKey(product.imageUrl)
            .build(),
        imageLoader = imageLoader,
        contentDescription = product.name,
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.fillMaxSize().background(PulseColors.SurfaceAlt), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PulseColors.Accent, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
            }
        },
        error = {
            Box(Modifier.fillMaxSize().background(PulseColors.SurfaceAlt), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Store, contentDescription = null, tint = PulseColors.TextMuted, modifier = Modifier.size(24.dp))
            }
        },
        modifier = Modifier.size(85.dp).clip(RoundedCornerShape(12.dp)).background(PulseColors.SurfaceAlt)
    )
}
