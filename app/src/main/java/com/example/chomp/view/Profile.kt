package com.example.chomp.view

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.chomp.MainActivity
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.profile_layout.*


// This fragment shows the user's profile page

class Profile : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val pickImage = 100
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.profile_layout, container, false)

        viewModel.observeProfileURI().observe(viewLifecycleOwner,{
            activity?.findViewById<ImageView>(R.id.profilePic)?.setImageURI(it)
        })

        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, {
            if (it == null) {
                Log.d("mytag", "No one is signed in")
            } else {
                Log.d("mytag", "${it.displayName} ${it.email} ${it.uid} signed in")
                val displayText =
                    StringBuilder("Welcome back!! \nUser:  ${it.displayName}\nEmail:  ${it.email}")
                activity?.findViewById<Button>(R.id.setDisplayName)?.text =
                    displayText.toString()
            }
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set click listener so that user can change profile picture by clicking on it
        profilePic.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        val displayButton = activity?.findViewById<Button>(R.id.setNewDisplayName)
        displayButton?.setOnClickListener{
            val user = FirebaseAuth.getInstance().currentUser

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayNameET.text.toString()).build()

            user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                val displayText = StringBuilder("Welcome back!! \nUser:  ${user.displayName}\nEmail:  ${user.email}")
                activity?.findViewById<Button>(R.id.setDisplayName)?.text = displayText.toString()
            }

            (activity as MainActivity).hideKeyboard()
            displayNameET.text.clear()
        }

        parentFragmentManager
            .beginTransaction()
            .replace(R.id.main_frame, Favorites.newInstance())
            .addToBackStack(null)
            .commit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //called when image was captured from camera intent
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            profilePic.setImageURI(imageUri)
            imageUri?.let { viewModel.setImageURI(it) }
        }
    }
}