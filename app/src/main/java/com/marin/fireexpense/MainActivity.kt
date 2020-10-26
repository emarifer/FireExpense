package com.marin.fireexpense

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.marin.fireexpense.data.MainDataSource
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.databinding.ActivityMainBinding
import com.marin.fireexpense.domain.MainRepoImpl
import com.marin.fireexpense.presentation.MainViewModel
import com.marin.fireexpense.presentation.MainViewModelFactory
import com.marin.fireexpense.utils.*
import com.marin.fireexpense.vo.Result
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var dataList: List<Expense>
    }

    private val viewModel by viewModels<MainViewModel> {
        MainViewModelFactory(MainRepoImpl(MainDataSource()))
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataList = emptyList()
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)

        updateDrawerLayoutUserData()

        val userId = FirebaseAuth.getInstance().uid
        if (userId == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            viewModel.setUserId(userId)
        }

        val toolbar: Toolbar = drawerLayout.toolbar
        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_feeding, R.id.nav_wear, R.id.nav_leisure, R.id.nav_travels, R.id.nav_home
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> { logoutAndFinish() }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun updateDrawerLayoutUserData() {
        viewModel.getUserData.observe(this, { result ->
            when (result) {
                is Result.Success -> {
                    binding.navView.txtUserName.text = result.data.userName.cipherDecrypt(getPassword()!!)
                    showImage(result.data.userPhoto, binding.navView.imageView)
                }
                is Result.Failure -> {
                    showToast(result.exception.message!!)
                }
            }
        })
    }

    private fun logoutAndFinish() {
        AlertDialog.Builder(this)
            .setTitle("¿Deseas cerrar sesión y salir de la App?")
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
                FirebaseAuth.getInstance().signOut()
                removePassword()
                finishAndRemoveTask()
            }
            .setNegativeButton("No") { dialog, _  ->
                dialog.dismiss()
            }
            .show()
    }
}