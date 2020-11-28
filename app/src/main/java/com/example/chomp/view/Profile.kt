package com.example.chomp.view

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chomp.MainActivity.Companion.cameraRC
import com.example.chomp.MainViewModel
import com.example.chomp.R
import kotlinx.android.synthetic.main.profile_layout.*
import android.Manifest
import com.example.chomp.MainActivity

// This fragment shows the user's profile page

class Profile : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var RESULT_OK = 0
    private val PERMISSION_CODE = 1000
    private var imageURI : Uri? = null
    private val IMAGE_CAPTURE_CODE = 1001



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener so that user can change profile picture by clicking on it
        profilePic.setOnClickListener {
            viewModel.setPhotoIntent(activity.takePhotoIntent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        if (resultCode == Activity.RESULT_OK){
            //set image captured to image view
            profilePic.setImageURI(imageURI)
        }
    }


}