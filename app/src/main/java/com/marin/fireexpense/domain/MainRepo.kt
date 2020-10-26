package com.marin.fireexpense.domain

import com.marin.fireexpense.data.model.EncryptedExpense
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.data.model.UpdateExpense
import com.marin.fireexpense.data.model.User
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.flow.Flow

/**
 * Created by Enrique Marín on 05-10-2020.
 */

interface MainRepo {

    suspend fun fetchUser(userId: String): Result<User>
    suspend fun fetchAllExpense(): Flow<Result<List<EncryptedExpense>>>
    suspend fun appendItem(expense: EncryptedExpense)
    suspend fun removeItem(expenseId: String)
    suspend fun updateItem(updateExpense: UpdateExpense)
}