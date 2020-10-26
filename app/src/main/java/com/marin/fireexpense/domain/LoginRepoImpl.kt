package com.marin.fireexpense.domain

import com.marin.fireexpense.data.LoginDataSource
import com.marin.fireexpense.data.model.CreateCredentials
import com.marin.fireexpense.data.model.LoginCredentials
import com.marin.fireexpense.vo.Result

/**
 * Created by Enrique Marín on 02-10-2020.
 */

class LoginRepoImpl(private val dataSource: LoginDataSource): LoginRepo {

    override suspend fun login(credentials: LoginCredentials): Result<Boolean> {
        return dataSource.authenticateUser(credentials)
    }

    override suspend fun register(credentials: CreateCredentials): Result<Boolean> {
        return dataSource.registerUser(credentials)
    }
}