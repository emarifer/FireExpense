package com.marin.fireexpense

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marin.fireexpense.data.LoginDataSource
import com.marin.fireexpense.data.model.LoginCredentials
import com.marin.fireexpense.databinding.ActivityLoginBinding
import com.marin.fireexpense.domain.LoginRepoImpl
import com.marin.fireexpense.presentation.LoginViewModel
import com.marin.fireexpense.presentation.LoginViewModelFactory
import com.marin.fireexpense.utils.savePassword
import com.marin.fireexpense.utils.showToast
import com.marin.fireexpense.utils.updateLoginScreen

class LoginActivity : AppCompatActivity() {

    private val viewModel by viewModels<LoginViewModel> {
        LoginViewModelFactory(LoginRepoImpl(LoginDataSource()))
    }

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        updateLoginScreen(viewModel.getLoginResult, binding.progressBar)

        binding.apply {
            btnLogin.setOnClickListener { userLogin() }
            txtRegister.setOnClickListener {
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    private fun userLogin() {
        binding.apply {
            val email = editTextTextEmailAddress.text.toString()
            val password = editTextTextPassword.text.toString()
            if (email.isNotBlank() && password.length == 16) {
                savePassword(password)
                viewModel.setLoginCredentials(LoginCredentials(email, password))
            } else {
                showToast("No has ingresado email o la contraseña tiene menos de 16 caracteres")
            }

            editTextTextEmailAddress.text.clear()
            editTextTextPassword.text.clear()
            editTextTextEmailAddress.requestFocus()
        }
    }
}
