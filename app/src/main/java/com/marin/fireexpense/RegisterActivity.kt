package com.marin.fireexpense

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marin.fireexpense.data.LoginDataSource
import com.marin.fireexpense.data.model.CreateCredentials
import com.marin.fireexpense.databinding.ActivityRegisterBinding
import com.marin.fireexpense.domain.LoginRepoImpl
import com.marin.fireexpense.presentation.RegisterViewModel
import com.marin.fireexpense.presentation.RegisterViewModelFactory
import com.marin.fireexpense.utils.*

class RegisterActivity : AppCompatActivity() {

    private val viewModel by viewModels<RegisterViewModel> {
        RegisterViewModelFactory(LoginRepoImpl(LoginDataSource()))
    }

    private val binding: ActivityRegisterBinding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }
    private var selectdPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateLoginScreen(viewModel.getRegisterResult, binding.progressBar)

        binding.apply {
            selectphotoButtonRegister.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }

            btnRegister.setOnClickListener { performRegister() }
        }
    }

    private fun performRegister() {
        if (selectdPhotoUri == null) {
            showToast("Imagen no seleccionada")
            return
        }

        binding.apply {
            val byteImage = compressImages(selectdPhotoUri)
            val userName = editTextUserName.text.toString()
            val email = editTextTextEmailAddress.text.toString()
            val password = editTextTextPassword.text.toString()
            if (userName.isNotBlank() && password.length == 16) {
                savePassword(password)
                val createCredentials = CreateCredentials(
                    byteImage = byteImage,
                    userName = userName,
                    email = email,
                    password = password
                )
                viewModel.setRegisterCredentials(createCredentials)
            } else {
                showToast("El nombre de usuario está vacío o la contraseña tiene menos de 16 caracteres")
            }

            editTextUserName.text.clear()
            editTextTextEmailAddress.text.clear()
            editTextTextPassword.text.clear()
            editTextUserName.requestFocus()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectdPhotoUri = data.data
            binding.apply {
                selectphotoImageviewRegister.setImageBitmap(getCapturedImage(selectdPhotoUri))
                selectphotoButtonRegister.visibility = View.INVISIBLE
            }
        }
    }
}
