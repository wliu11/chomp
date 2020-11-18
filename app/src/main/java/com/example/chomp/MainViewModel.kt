package com.example.chomp

import android.app.Application
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.chomp.glide.Glide
import java.io.File
import java.io.IOException
import java.util.*

class MainViewModel(application: Application) :
        AndroidViewModel(application) {
    private val appContext = getApplication<Application>().applicationContext

    // Remember the uuid, and hence file name of file camera will create
    private var pictureUUID: String = ""
    private var storageDir =
            getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    private lateinit var auth: Auth
    private lateinit var storage: Storage
    private lateinit var crashMe: String


    // NB: Here is a problem with this whole strategy.  It "works" when you use
    // local variables to save these "function pointers."  But the viewModel can be
    // cleared, so we want to save these function pointers that are actually closures
    // with a reference to the activity/fragment that created them.  So we get a
    // parcelable error if we try to store them into a SavedHandleState
    private fun noPhoto() {
        Log.d(javaClass.simpleName, "Function must be initialized to something that can start the camera intent")
        crashMe.plus(" ")
    }
    private var takePhotoIntent: () -> Unit = ::noPhoto

    private fun defaultPhoto(@Suppress("UNUSED_PARAMETER") path: String) {
        Log.d(javaClass.simpleName, "Function must be initialized to photo callback" )
        crashMe.plus(" ")
    }
    private var photoSuccess: (path: String) -> Unit = ::defaultPhoto

    /////////////////////////////////////////////////////////////
    // This is intended to be set once by MainActivity.
    // The bummer is that taking a photo requires startActivityForResult
    // which has to be called from an activity.
    fun setPhotoIntent(_takePhotoIntent: () -> Unit) {
        takePhotoIntent = _takePhotoIntent
    }

    /////////////////////////////////////////////////////////////
    // Get callback for when camera intent returns.
    // Send intent to take picture
    fun takePhoto(_photoSuccess: (String) -> Unit) {
        photoSuccess = _photoSuccess
        takePhotoIntent()
    }

    /////////////////////////////////////////////////////////////
    // Create a file for the photo, remember it, and create a Uri
    fun getPhotoURI(): Uri {
        // Create an image file name
        pictureUUID = UUID.randomUUID().toString()
        var photoUri: Uri? = null
        // Create the File where the photo should go
        try {
            val localPhotoFile = File(storageDir, "${pictureUUID}.jpg")
            Log.d(javaClass.simpleName, "photo path ${localPhotoFile.absolutePath}")
            photoUri = FileProvider.getUriForFile(
                    appContext,
                    "edu.utap.firenote",
                    localPhotoFile
            )
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.d(javaClass.simpleName, "Cannot create file", ex)
        }
        // CRASH.  Production code should do something more graceful
        return photoUri!!
    }

    /////////////////////////////////////////////////////////////
    // Callbacks from MainActivity.getResultForActivity from camera intent
    // We can't just schedule the file upload and return.
    // The problem is that our previous picture uploads can still be pending.
    // So a note can have a pictureUUID that does not refer to an existing file.
    // That violates referential integrity, which we really like in our db (and programming
    // model).
    // So we do not add the pictureUUID to the note until the picture finishes uploading.
    // That means a user won't see their picture updates immediately, they have to
    // wait for some interaction with the server.
    // You could imagine dealing with this somehow using local files while waiting for
    // a server interaction, but that seems error prone.
    // Freezing the app during an upload also seems bad.
    fun pictureSuccess() {
        val localPhotoFile = File(storageDir, "${pictureUUID}.jpg")
        // Wait until photo is successfully uploaded before calling back
        storage.uploadImage(localPhotoFile, pictureUUID) {
            photoSuccess(pictureUUID)
            photoSuccess = ::defaultPhoto
            pictureUUID = ""
        }
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        pictureUUID = ""
    }

    /////////////////////////////////////////////////////////////
    fun firestoreInit(auth: Auth, storage: Storage) {
        this.auth = auth
        this.storage = storage
    }

//    fun glideFetch(pictureUUID: String, imageView: ImageView) {
//        Glide.fetch(storage.uuid2StorageReference(pictureUUID),
//                imageView)
//    }
}


