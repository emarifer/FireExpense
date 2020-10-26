package com.marin.fireexpense.presentation

import androidx.lifecycle.*
import com.marin.fireexpense.data.model.LoginCredentials
import com.marin.fireexpense.domain.LoginRepo
import kotlinx.coroutines.Dispatchers
import com.marin.fireexpense.vo.Result

/**
 * Created by Enrique Marín on 02-10-2020.
 */

class LoginViewModel(private val repo: LoginRepo) : ViewModel() {

    private val loginCredentialsData = MutableLiveData<LoginCredentials>()

    fun setLoginCredentials(loginCredentials: LoginCredentials) {
        loginCredentialsData.value = loginCredentials
    }

    val getLoginResult = loginCredentialsData.switchMap { loginCredentials ->
        liveData<Result<Boolean>>(Dispatchers.IO) {
            emit(Result.Loading())
            try {
                emit(repo.login(loginCredentials))
            } catch (e: Exception) {
                emit(Result.Failure(e))
            }
        }
    }
}

class LoginViewModelFactory(private val repo: LoginRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(LoginRepo::class.java).newInstance(repo)
    }
}