package com.marin.fireexpense.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.marin.fireexpense.data.model.CreateCredentials
import com.marin.fireexpense.data.model.LoginCredentials
import com.marin.fireexpense.data.model.User
import com.marin.fireexpense.utils.cipherEncrypt
import com.marin.fireexpense.vo.Result
import kotlinx.coroutines.tasks.await

/**
 * Created by Enrique Marín on 02-10-2020.
 */

class LoginDataSource {

    suspend fun authenticateUser(credentials: LoginCredentials): Result<Boolean> {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(credentials.email, credentials.password).await()
        return Result.Success(true)
    }

    suspend fun registerUser(credentials: CreateCredentials): Result<Boolean> {

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(credentials.email, credentials.password).await()

        val ref = FirebaseStorage.getInstance()
            .getReference("/image/${credentials.fileName}")
        ref.putBytes(credentials.byteImage).await()
        val downloadUrl = ref.downloadUrl.await()

        val userUid = FirebaseAuth.getInstance().uid
        val encryptedUsername = credentials.userName.cipherEncrypt(credentials.password)
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userUid!!)
            .set(User(encryptedUsername.toString(), downloadUrl.toString()))

        return Result.Success(true)
    }
}