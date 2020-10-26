package com.marin.fireexpense.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.marin.fireexpense.data.model.EncryptedExpense
import com.marin.fireexpense.data.model.UpdateExpense
import com.marin.fireexpense.data.model.User
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Created by Enrique Marín on 05-10-2020.
 */
 
class MainDataSource {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserData(userId: String): Result<User> {
        val userQuery = db.collection("users").document(userId).get().await()
        val user: User = userQuery.toObject(User::class.java)!!
        return Result.Success(user)
    }

    @ExperimentalCoroutinesApi
    suspend fun getAllExpense(): Flow<Result<List<EncryptedExpense>>> = callbackFlow {
        /*delay(1000)
        return Result.Success(dummyExpenseList.filter {
            it.type == type}.sortedBy { it.timestamp })*/

        if (FirebaseAuth.getInstance().uid == null) {
            offer(Result.Success(emptyList<EncryptedExpense>()))
        } else {
            val expenseList = mutableListOf<EncryptedExpense>()

            val expenseQuery = db.collection("expense-${FirebaseAuth.getInstance().uid}")
                .get().await()

            for (expense in expenseQuery) {
                val expenseId = expense.id
                val expenseTimestamp = expense.toObject(EncryptedExpense::class.java).timestamp
                val expenseType = expense.toObject(EncryptedExpense::class.java).type
                val expenseYear = expense.toObject(EncryptedExpense::class.java).year
                val expenseMonth = expense.toObject(EncryptedExpense::class.java).month
                val expenseConcept = expense.toObject(EncryptedExpense::class.java).concept
                val expenseAmount = expense.toObject(EncryptedExpense::class.java).amount
                val expenseDate = expense.toObject(EncryptedExpense::class.java).date
                expenseList.add(EncryptedExpense(expenseId, expenseTimestamp, expenseType, expenseYear, expenseMonth, expenseConcept, expenseAmount, expenseDate))
            }
            offer(Result.Success(expenseList))
        }

        awaitClose { cancel() }
    }

    suspend fun addExpenseItem(expense: EncryptedExpense) {
        db.collection("expense-${FirebaseAuth.getInstance().uid}")
            .document(expense.expenseId)
            .set(expense)
            .await()
    }

    suspend fun deleteExpenseItem(expenseId: String) {
        db.collection("expense-${FirebaseAuth.getInstance().uid}")
            .document(expenseId)
            .delete()
            .await()
    }

    suspend fun updateExpenseItem(updateExpense: UpdateExpense) {
        val data = mapOf("concept" to updateExpense.concept, "amount" to updateExpense.amount)
        db.collection("expense-${FirebaseAuth.getInstance().uid}")
            .document(updateExpense.expenseId)
            .update(data)
            .await()
    }

    /*private val dummyExpenseList = listOf(
    Expense(type = "Vestuario", year = "2018", month = "02", concept = "Pechuga", amount = 12.65),
    Expense(type = "Alimentación", year = "2018", month = "02", concept = "Aceite", amount = 78.00),
    Expense(type = "Viajes", concept = "Aceite", amount = 12.65),
    Expense(type = "Ocio", concept = "Patatas", amount = 12.65),
    Expense(type = "Alimentación", year = "2018", month = "02", concept = "Aceite", amount = 12.65),
    Expense(type = "Alimentación", concept = "Aceite", amount = 12.65),
    Expense(type = "Alimentación", concept = "Aceite", amount = 12.65),
    Expense(type = "Viajes", concept = "Patatas", amount = 12.65),
    Expense(type = "Alimentación", concept = "Aceite", amount = 5.40),
    Expense(type = "Viajes", concept = "Aceite", amount = 12.65),
    Expense(type = "Alimentación", year = "2018", month = "02", concept = "Aceite", amount = 12.65),
    Expense(type = "Viajes", concept = "Aceite", amount = 50.00),
    Expense(type = "Hogar", concept = "Patatas", amount = 12.65),
    Expense(type = "Alimentación", concept = "Aceite", amount = 12.65),
    Expense(type = "Viajes", concept = "Aceite", amount = 12.65),
    Expense(type = "Hogar", concept = "Patatas", amount = 12.65),
    Expense(type = "Vestuario", concept = "Patatas", amount = 12.65),
    )

    private val addDummyExpenseItem =  Expense(type = "Alimentación", concept = "Naranjas", amount = 4.75)*/
}