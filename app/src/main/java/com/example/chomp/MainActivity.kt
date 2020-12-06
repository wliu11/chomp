package com.example.chomp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.io.Serializable

class MainActivity : AppCompatActivity() {

    private lateinit var navigationController: NavController

    fun hideKeyboard() {
        currentFocus?.windowToken?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?)
                ?.hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.CustomTheme)
                .setLogo(R.drawable.homepage)
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers)
                .build(),
            1
        )

        // Hide top action toolbar
        supportActionBar?.hide()

        // Initiate bottom toolbar navigation
        navigationController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.toString()) {
                "Home" -> {
                    navigationController.navigate(R.id.homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                "Profile" -> {
                    navigationController.navigate(R.id.profile)
                    return@setOnNavigationItemSelectedListener true
                }
                "Messages" -> {
                    navigationController.navigate(R.id.chatFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                "Settings" -> {
                    navigationController.navigate(R.id.settingsFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // https://developer.android.com/guide/navigation/navigation-ui
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}