package com.marin.fireexpense.domain

import com.marin.fireexpense.data.MainDataSource
import com.marin.fireexpense.data.model.EncryptedExpense
import com.marin.fireexpense.data.model.UpdateExpense
import com.marin.fireexpense.data.model.User
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Created by Enrique Marín on 05-10-2020.
 */

class MainRepoImpl(private val dataSource: MainDataSource) : MainRepo {

    override suspend fun fetchUser(userId: String): Result<User> = dataSource.getUserData(userId)

    @ExperimentalCoroutinesApi
    override suspend fun fetchAllExpense(): Flow<Result<List<EncryptedExpense>>> =
        dataSource.getAllExpense()

    override suspend fun appendItem(expense: EncryptedExpense) = dataSource.addExpenseItem(expense)

    override suspend fun removeItem(expenseId: String) = dataSource.deleteExpenseItem(expenseId)

    override suspend fun updateItem(updateExpense: UpdateExpense) =
        dataSource.updateExpenseItem(updateExpense)
}