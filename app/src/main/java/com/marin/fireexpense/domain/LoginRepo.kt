package com.marin.fireexpense.domain

import com.marin.fireexpense.data.model.CreateCredentials
import com.marin.fireexpense.data.model.LoginCredentials
import com.marin.fireexpense.vo.Result

/**
 * Created by Enrique Marín on 02-10-2020.
 */

interface LoginRepo {
    suspend fun login(credentials: LoginCredentials): Result<Boolean>
    suspend fun register(credentials: CreateCredentials): Result<Boolean>
}