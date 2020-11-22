package com.example.chomp
import android.app.Application
import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.chomp.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.io.File
import java.io.IOException
import java.util.*


class MainViewModel(application: Application,
    // Handle provided if framework kills application, but not if user kills it.
                    private val state: SavedStateHandle
)
    : AndroidViewModel(application) {
    companion object {
        // Remember the uuid, and hence file name of file camera will create
        const val pictureUUIDKey = "pictureUUIDKey"
        // NB: Here is a problem with the way I do pictures.  It works when I use
        // local variables to save these "function pointers."  But the viewModel can be
        // cleared, so we want to save these function pointers.  But they are actually closures
        // with a reference to the activity/fragment that created them.  So we get a
        // parcelable error if we try to store them into a SavedStateHandle
        const val photoSuccessKey = "photoSuccessKey"
        const val takePhotoIntentKey = "takePhotoIntentKey"
    }
    private val appContext = getApplication<Application>().applicationContext
    private var storageDir =
        getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private var chat = MutableLiveData<List<ChatRow>>()
    private var chatListener : ListenerRegistration? = null
    private val oneFifthWidthPx = (appContext.resources.displayMetrics.widthPixels / 5).toInt()
    val fourFifthWidthPx = 4 * oneFifthWidthPx
    // assert does not work
    private lateinit var crashMe: String

    // Default function implementations for photoSuccess callback
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


    fun observeFirebaseAuthLiveData(): LiveData<FirebaseUser?> {
        return firebaseAuthLiveData
    }
    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }

    fun signOut() {
        chatListener?.remove()
        FirebaseAuth.getInstance().signOut()
        chat.value = listOf()
    }

    fun observeChat(): LiveData<List<ChatRow>> {
        return chat
    }
    fun saveChatRow(chatRow: ChatRow) {
        Log.d(
            "HomeViewModel",
            String.format(
                "saveChatRow ownerUid(%s) name(%s) %s",
                chatRow.ownerUid,
                chatRow.name,
                chatRow.message
            )
        )
        // XXX Write me.
        // https://firebase.google.com/docs/firestore/manage-data/add-data#add_a_document
        // Remember to set the rowID of the chatRow before saving it
        chatRow.rowID = db.collection("globalChat").document().id
        db.collection("globalChat")
            .document(chatRow.rowID)
            .set(chatRow)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Chat created with id: ${chatRow.rowID}"
                )
                getChat()
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun deleteChatRow(chatRow: ChatRow){
        // Delete picture (if any) on the server, asynchronously
        val uuid = chatRow.pictureUUID
        if(uuid != null) {
            Storage.deleteImage(uuid)
        }
        Log.d("mytag", "remote chatRow id: ${chatRow.rowID}")

        // XXX delete chatRow
        db.collection("globalChat").document(chatRow.rowID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Chat removed with id: ${chatRow.rowID}"
                )
            }
            .addOnFailureListener { e ->
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun getChat() {
        if(FirebaseAuth.getInstance().currentUser == null) {
            Log.d(javaClass.simpleName, "Can't get chat, no one is logged in")
            chat.value = listOf()
            return
        }
        // XXX Write me.  Limit total number of chat rows to 100
        db.collection("globalChat")
            .orderBy("timeStamp")
            .limit(100)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.w("mytag", "listen:error", ex)
                    return@addSnapshotListener
                }
                Log.d("mytag", "fetch ${querySnapshot!!.documents.size}")
                chat.value = querySnapshot.documents.mapNotNull {
                    it.toObject(ChatRow::class.java)
                }
            }
    }

    /////////////////////////////////////////////////////////////
    // This is intended to be set once by MainActivity.
    // The bummer is that taking a photo requires startActivityForResult
    // which has to be called from an activity.
    fun setPhotoIntent(_takePhotoIntent: () -> Unit) {
        takePhotoIntent = _takePhotoIntent
        state.set(takePhotoIntentKey, takePhotoIntent)
    }

    /////////////////////////////////////////////////////////////
    // Get callback for when camera intent returns.
    // Send intent to take picture
    fun takePhoto(_photoSuccess: (String) -> Unit) {
        photoSuccess = _photoSuccess
        state.set(photoSuccessKey, photoSuccess)
        takePhotoIntent.invoke()
    }

    /////////////////////////////////////////////////////////////
    // Create a file for the photo, remember it, and create a Uri
    fun getPhotoURI(): Uri {
        // Create an image file name
        state.set(pictureUUIDKey,  UUID.randomUUID().toString())
        Log.d(javaClass.simpleName, "pictureUUID ${state.get<String>(pictureUUIDKey)}")
        var photoUri: Uri? = null
        // Create the File where the photo should go
        try {
            val localPhotoFile = File(storageDir, "${state.get<String>(pictureUUIDKey)}.jpg")
            Log.d(javaClass.simpleName, "photo path ${localPhotoFile.absolutePath}")
            photoUri = FileProvider.getUriForFile(
                appContext,
                "com.example.chomp",
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
        val pictureUUID = state.get(pictureUUIDKey) ?: ""
        val localPhotoFile = File(storageDir, "${pictureUUID}.jpg")
        Log.d(javaClass.simpleName, "pictureSuccess ${localPhotoFile.absolutePath}")
        // Wait until photo is successfully uploaded before calling back
        Storage.uploadImage(localPhotoFile, pictureUUID) {
            Log.d(javaClass.simpleName, "uploadImage callback ${pictureUUID}")
            photoSuccess(pictureUUID)
            photoSuccess = ::defaultPhoto
            state.get<(String)->Unit>(photoSuccessKey)?.invoke(pictureUUID)
            state.set(photoSuccessKey, ::defaultPhoto)
            state.set(pictureUUIDKey, "")
        }
    }
    fun pictureFailure() {
        // Note, the camera intent will only create the file if the user hits accept
        // so I've never seen this called
        state.set(pictureUUIDKey, "")
        Log.d(javaClass.simpleName, "pictureFailure pictureUUID cleared")
    }

    // For our phone, translate dp to pixels
    private fun dpToPx(dp: Int): Int {
        return (dp * appContext.resources.displayMetrics.density).toInt()
    }

    fun glideFetch(pictureUUID: String, imageView: ImageView) {
        // NB: Should get apsect ratio from image itself
        Glide.fetch(Storage.uuid2StorageReference(pictureUUID), imageView,
            fourFifthWidthPx, (1.466*fourFifthWidthPx).toInt())
    }
    // Debatable how useful this is.
    override fun onCleared() {
        Log.d(javaClass.simpleName, "onCleared!!")
        super.onCleared()
        chatListener?.remove()
    }
}