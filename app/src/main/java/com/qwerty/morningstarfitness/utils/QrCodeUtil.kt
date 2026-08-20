package com.qwerty.morningstarfitness.utils

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQrCodeBitmap(content: String, sizePx: Int = 512): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx)
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.RGB_565)
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE)
        }
    }
    return bitmap
}
