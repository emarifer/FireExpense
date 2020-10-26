package com.marin.fireexpense.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.marin.fireexpense.MainActivity
import com.marin.fireexpense.R
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.vo.Result
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Enrique Marín on 02-10-2020.
 */

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

inline fun <T : View> T.showIf(condition: (T) -> Boolean) {
    if (condition(this)) {
        show()
    } else {
        hide()
    }
}

/*inline fun <T : View> T.hideIf(condition: (T) -> Boolean) {
    if (condition(this)) {
        hide()
    } else {
        show()
    }
}*/

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.getCapturedImage(selectedPhotoUri: Uri?): Bitmap {
    return if (Build.VERSION.SDK_INT < 28) {
        @Suppress("DEPRECATION") MediaStore.Images.Media
            .getBitmap(contentResolver, selectedPhotoUri)
    } else {
        val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri!!)
        ImageDecoder.decodeBitmap(source)
    }
}

fun Context.compressImages(selectedPhotoUri: Uri?): ByteArray {
    val baos = ByteArrayOutputStream()
    getCapturedImage(selectedPhotoUri).compress(Bitmap.CompressFormat.JPEG, 20, baos)
    return baos.toByteArray()
}

fun AppCompatActivity.updateLoginScreen(liveData: LiveData<Result<Boolean>>, progressBar: ProgressBar) {
    liveData.observe(this, { result ->
        progressBar.showIf { result is Result.Loading }
        when (result) {
            is Result.Success -> {
                progressBar.hide()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            is Result.Failure -> {
                progressBar.hide()
                showToast(result.exception.message!!)
            }
        }
    })
}

fun Context.showImage(userPhoto: String, imageView: CircleImageView) {
    Glide.with(this)
        .load(userPhoto)
        .centerCrop()
        .into(imageView)
}

fun currentDateTime(): String {
    val c = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateList = dateFormat.format(c.time).split(" ")
    return "${dateList[0]} • ${dateList[1]}"
}

fun currentMonth(): String {
    val c = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MM", Locale.getDefault())
    return dateFormat.format(c.time)
}

fun currentYear(): String {
    val c = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy", Locale.getDefault())
    return dateFormat.format(c.time)
}

fun setMonthFormat(month: String): String = if (month.length == 1) "0$month" else month

fun checkYear(year: String): Boolean = year.toIntOrNull() != null && year.toInt() in 2000..2100

fun totalAmount(expenseList: List<Expense>): String {
    val res = DecimalFormat("#.##") // Formateo con 2 decimales.
    res.roundingMode = RoundingMode.HALF_UP // Redondeo hacia valor más cercano/arriba.
    var sum = 0.0
    expenseList.forEach { sum += it.amount }
    return "Suma total: ${res.format(sum)}€"
}

fun List<Expense>.filterBy(type: String, year: String, month: String): List<Expense> =
    if (month.toIntOrNull() != null && month.toInt() in 1..12) {
        filter { it.type == type && it.year == year && it.month == month }
    } else {
        filter { it.type == type && it.year == year }
    }

fun EditText.addDecimalLimiter(maxLimit: Int = 2) {

    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            val str = this@addDecimalLimiter.text!!.toString()
            if (str.isEmpty()) return
            val str2 = decimalLimiter(str, maxLimit)

            if (str2 != str) {
                this@addDecimalLimiter.setText(str2)
                val pos = this@addDecimalLimiter.text!!.length
                this@addDecimalLimiter.setSelection(pos)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

    })
}

fun decimalLimiter(string: String, MAX_DECIMAL: Int): String {

    var str = string
    if (str[0] == '.') str = "0$str"
    val max = str.length

    var rFinal = ""
    var after = false
    var i = 0
    var up = 0
    var decimal = 0
    var t: Char

    val decimalCount = str.count{ ".".contains(it) }

    if (decimalCount > 1)
        return str.dropLast(1)

    while (i < max) {
        t = str[i]
        if (t != '.' && !after) {
            up++
        } else if (t == '.') {
            after = true
        } else {
            decimal++
            if (decimal > MAX_DECIMAL)
                return rFinal
        }
        rFinal += t
        i++
    }
    return rFinal
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun Context.savePassword(password: String) { // SE REQUIERE PASSWORD DE 16 CARACTERES (16 BYTES; ALGORITMO AES)
    val sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
        Context.MODE_PRIVATE) ?: return
    with (sharedPref.edit()) {
        putString(getString(R.string.password), password)
        apply()
    }
}

fun Context.getPassword(): String? {
    val sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
        Context.MODE_PRIVATE) ?: return ""
    return sharedPref.getString(getString(R.string.password), "")
}

fun Context.removePassword() { // SE REQUIERE PASSWORD DE 16 CARACTERES (16 BYTES; ALGORITMO AES)
    val sharedPref = getSharedPreferences(getString(R.string.preference_file_key),
        Context.MODE_PRIVATE) ?: return
    with (sharedPref.edit()) {
        clear()
        apply()
    }
}

fun String.cipherEncrypt(encryptionKey: String): String? {
    try {
        val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val iv = encryptionKey.toByteArray()
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

        val encryptedValue = cipher.doFinal(this.toByteArray())
        return Base64.encodeToString(encryptedValue, Base64.DEFAULT)
    } catch (e: Exception) {
        e.message?.let{ Log.e("encryptor", it) }
    }
    return null
}

fun String.cipherDecrypt(encryptionKey: String): String? {
    try {
        val secretKeySpec = SecretKeySpec(encryptionKey.toByteArray(), "AES")
        val iv = encryptionKey.toByteArray()
        val ivParameterSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        val decodedValue = Base64.decode(this, Base64.DEFAULT)
        val decryptedValue = cipher.doFinal(decodedValue)
        return String(decryptedValue)
    } catch (e: Exception) {
        e.message?.let{ Log.e("decryptor", it) }
    }
    return null
}