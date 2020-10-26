package com.marin.fireexpense.data.model

import java.util.*

/**
 * Created by Enrique Marín on 02-10-2020.
 */

data class LoginCredentials(
    val email: String,
    val password: String
)

data class CreateCredentials(
    val fileName: String = UUID.randomUUID().toString(),
    val byteImage: ByteArray,
    val userName: String,
    val email: String,
    val password: String
)