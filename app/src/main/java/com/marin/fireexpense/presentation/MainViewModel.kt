package com.marin.fireexpense.presentation

import androidx.lifecycle.*
import com.marin.fireexpense.data.model.EncryptedExpense
import com.marin.fireexpense.data.model.UpdateExpense
import com.marin.fireexpense.data.model.User
import com.marin.fireexpense.domain.MainRepo
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Created by Enrique Marín on 05-10-2020.
 */

class MainViewModel(private val repo: MainRepo) : ViewModel() {

    private val fetcherUserData = MutableLiveData<String>()

    private val fetcherExpenseData = MutableLiveData<String>()

    fun setUserId(userId: String) {
        fetcherUserData.value = userId
    }

    val getUserData = fetcherUserData.switchMap { userId ->
        liveData<Result<User>>(Dispatchers.IO) {
            emit(Result.Loading())
            try {
                emit(repo.fetchUser(userId))
            } catch (e: Exception) {
                emit(Result.Failure(e))
            }
        }
    }

    fun setExpenseData(type: String) {
        fetcherExpenseData.value = type
    }

    val getExpenseData = fetcherExpenseData.switchMap { _ ->
        liveData(Dispatchers.IO) {
            emit(Result.Loading())
            repo.fetchAllExpense()
                .onEach { emit(it) }
                .catch { e -> emit(Result.Failure(e)) }
                .collect()
        }
    }

    fun createExpense(expense: EncryptedExpense) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.appendItem(expense)
        }
    }

    fun eliminateExpense(expenseId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.removeItem(expenseId)
        }
    }

    fun updateExpenseItem(updateExpense: UpdateExpense) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateItem(updateExpense)
        }
    }
}

class MainViewModelFactory(private val repo: MainRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(MainRepo::class.java).newInstance(repo)
    }
}