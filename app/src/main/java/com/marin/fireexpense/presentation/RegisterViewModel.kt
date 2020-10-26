package com.marin.fireexpense.presentation

import androidx.lifecycle.*
import com.marin.fireexpense.data.model.CreateCredentials
import com.marin.fireexpense.domain.LoginRepo
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.Dispatchers

/**
 * Created by Enrique Marín on 04-10-2020.
 */

class RegisterViewModel(private val repo: LoginRepo) : ViewModel() {

    private val registerCredentialsData = MutableLiveData<CreateCredentials>()

    fun setRegisterCredentials(registerCredentials: CreateCredentials){
        registerCredentialsData.value = registerCredentials
    }

    val getRegisterResult = registerCredentialsData.switchMap { registerCredentials ->
        liveData<Result<Boolean>>(Dispatchers.IO) {
            emit(Result.Loading())
            try {
                emit(repo.register(registerCredentials))
            } catch (e:Exception) {
                emit(Result.Failure(e))
            }
        }
    }
}

class RegisterViewModelFactory(private val repo: LoginRepo): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(LoginRepo::class.java).newInstance(repo)
    }
}