package com.marin.fireexpense.data.model

import android.os.Parcelable
import com.marin.fireexpense.utils.*
import com.marin.fireexpense.utils.cipherEncrypt
import kotlinx.android.parcel.Parcelize
import java.lang.System.currentTimeMillis
import java.util.*

/**
 * Created by Enrique Marín on 06-10-2020.
 */

@Parcelize
data class Expense(
    val expenseId: String = UUID.randomUUID().toString(),
    val timestamp: Long = currentTimeMillis(),
    val type: String = "",
    val year: String = currentYear(),
    val month: String = currentMonth(),
    var concept: String = "",
    var amount: Double = 0.0,
    val date: String = currentDateTime()
) : Parcelable

data class EncryptedExpense(
    val expenseId: String = "",
    val timestamp: String = "",
    val type: String = "",
    val year: String = "",
    val month: String = "",
    var concept: String = "",
    var amount: String = "",
    val date: String = ""
)

fun Expense.asEncrypted(password: String): EncryptedExpense = EncryptedExpense(
    this.expenseId,
    this.timestamp.toString().cipherEncrypt(password)!!,
    this.type.cipherEncrypt(password)!!,
    this.year.cipherEncrypt(password)!!,
    this.month.cipherEncrypt(password)!!,
    this.concept.cipherEncrypt(password)!!,
    this.amount.toString().cipherEncrypt(password)!!,
    this.date.cipherEncrypt(password)!!
)

fun List<EncryptedExpense>.asDecrypted(password: String): List<Expense> = this.map {
    Expense(
        it.expenseId,
        it.timestamp.cipherDecrypt(password)!!.toLong(),
        it.type.cipherDecrypt(password)!!,
        it.year.cipherDecrypt(password)!!,
        it.month.cipherDecrypt(password)!!,
        it.concept.cipherDecrypt(password)!!,
        it.amount.cipherDecrypt(password)!!.toDouble(),
        it.date.cipherDecrypt(password)!!
    )
}

data class UpdateExpense(
    val concept: String,
    val amount: String,
    val expenseId: String
)