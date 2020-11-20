package com.example.chomp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.google.android.gms.auth.api.Auth

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var auth: Auth
    private lateinit var navigationController: NavController
    private val paths = arrayOf("Home", "My Profile")

    private fun initUserUI() {
        viewModel.observeFirebaseAuthLiveData().observe(this, Observer {
            if( it == null ) {
                Log.d("mytag", "No one is signed in")
            } else {
                Log.d("mytag", "${it.displayName} ${it.email} ${it.uid} signed in")
            }
        })
    }

    fun hideKeyboard() {
        currentFocus?.windowToken?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager?)
                ?.hideSoftInputFromWindow(it, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationController = findNavController(R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navigationController)

        initUserUI()
        val authInitIntent = Intent(this, AuthInitActivity::class.java)
        startActivity(authInitIntent)

        supportActionBar?.hide()

        val spinner = findViewById<Spinner>(R.id.dropdown_menu)
        val spinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, paths
        )

        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Log.d("mytag", "selected "+parent.getItemAtPosition(position).toString())
                when(parent.getItemAtPosition(position).toString()) {
                    "Home" -> navigationController.navigate(R.id.homeFragment)
                    "My Profile" -> navigationController.navigate(R.id.profile)
                    else -> Log.d("mytag", "is this even possble")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }
    }


    // Need home fragment
    companion object {
        const val cameraRC = 10
    }


    private fun takePhotoIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePhotoIntent ->
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, viewModel.getPhotoURI())
            startActivityForResult(takePhotoIntent, cameraRC)
        }
        Log.d(javaClass.simpleName, "takePhotoIntent")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(javaClass.simpleName, "onActivityResult")
        when (requestCode) {
            cameraRC -> {
                if (resultCode == RESULT_OK) {
                    viewModel.pictureSuccess()
                } else {
                    viewModel.pictureFailure()
                }
            }
        }
    }

    override fun onSupportNavigateUp() = navigationController.navigateUp()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // https://developer.android.com/guide/navigation/navigation-ui
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


}