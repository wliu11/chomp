package com.example.chomp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import com.example.chomp.api.CollectionList
import com.example.chomp.api.RestaurantList
import com.example.chomp.api.RestaurantApi
import com.example.chomp.api.RestaurantListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.chomp.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainViewModel(application: Application,
    // Handle provided if framework kills application, but not if user kills it.
                    private val state: SavedStateHandle
)
    : AndroidViewModel(application) {

    private val appContext = getApplication<Application>().applicationContext
    private var storageDir =
        getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private var chat = MutableLiveData<List<ChatRow>>()
    private var chatListener : ListenerRegistration? = null
    private val oneFifthWidthPx = (appContext.resources.displayMetrics.widthPixels / 5).toInt()
    private val fourFifthWidthPx = 4 * oneFifthWidthPx
    // assert does not work
    private lateinit var crashMe: String

    // Live data
    private var favRestaurants = MutableLiveData<List<RestaurantList>>().apply {
        value = mutableListOf()
    }

    var recommendation = MutableLiveData<RestaurantList>()

    // Venkat started here

    private var restaurantApi = RestaurantApi.create()
    private var restaurantRepository = RestaurantListRepository(restaurantApi)
    private var restaurants = MutableLiveData<List<RestaurantList>>()
    private var city_name = MutableLiveData<String>().apply {
        value = "Austin,TX"
    }
    private var cityId = MutableLiveData<Int>().apply {
        value = 278
    }
    private var collections = MutableLiveData<List<CollectionList>>()

    //Venkat end here

    fun newRecommendation(restaurant: RestaurantList) {
        recommendation.value = restaurant
        Log.d("mytag", "hello we are updating the rec yes ")
    }

    // Default function implementations for photoSuccess callback
    private fun noPhoto() {
        Log.d(javaClass.simpleName, "Function must be initialized to something that can start the camera intent")
        crashMe.plus(" ")
    }

    private fun defaultPhoto(@Suppress("UNUSED_PARAMETER") path: String) {
        Log.d(javaClass.simpleName, "Function must be initialized to photo callback" )
        crashMe.plus(" ")
    }


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

//started here - Venkat

    private fun posts() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        restaurants.postValue(cityId.value?.let { restaurantRepository.getRestaurants(it) })
    }

    private fun collections() =  viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        collections.postValue(cityId.value?.let { restaurantRepository.getCollections(it) })
    }

    private fun cities() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        cityId.postValue(city_name.value?.let { restaurantRepository.getCityID(it)?.id })
    }

    init {
        posts()
        collections()
    }

    fun setLocation(newLocation: String) {
        city_name.value = newLocation
        Log.d("XXX", "New City name: $newLocation")
        cities()
    }

    private var  newRestaurants = MediatorLiveData<List<RestaurantList>>().apply {

        addSource(cityId) {value = newList()}
        //addSource(restaurants) {value = newList()}

        // Initial value
        value = restaurants.value
    }

    private fun newList(): List<RestaurantList>? {
       // cities()
        posts()
        collections()
        return restaurants.value
    }

    fun observePosts(): LiveData<List<RestaurantList>> {
        return newRestaurants
    }

    fun observeCollections(): LiveData<List<CollectionList>> {
        return collections
    }

//End here - Venkat

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

    // Manage favorites list
    fun addFav(post : RestaurantList) {
        val localList = favRestaurants.value?.toMutableList()
        localList?.let {
            it.add(post)
            favRestaurants.value = it
        }
    }

    fun isFav(post : RestaurantList): Boolean {
        return favRestaurants.value?.contains(post) ?: false
    }

    fun removeFav(post : RestaurantList) {
        val localList = favRestaurants.value?.toMutableList()
        localList?.let {
            it.remove(post)
            favRestaurants.value = it
        }
    }

    internal fun observeFav(): LiveData<List<RestaurantList>> {
        return favRestaurants
    }

    // For our phone, translate dp to pixels
    private fun dpToPx(dp: Int): Int {
        return (dp * appContext.resources.displayMetrics.density).toInt()
    }

    fun glideFetch(pictureUUID: String, imageView: ImageView) {
        Glide.fetch(Storage.uuid2StorageReference(pictureUUID), imageView,
            fourFifthWidthPx, (1.466*fourFifthWidthPx).toInt())
    }


    override fun onCleared() {
        Log.d(javaClass.simpleName, "onCleared!!")
        super.onCleared()
        chatListener?.remove()
    }

    // Convenient place to put it as it is shared
    companion object {
        fun launchPost(context: Context, restaurant: RestaurantList) {
            val intent = Intent(context, RestaurantProfile::class.java)

            intent.putExtra("name", restaurant.name.toString())
            intent.putExtra("cuisine", restaurant.cuisines.toString())
            intent.putExtra("cost", restaurant.cost.toString())
            intent.putExtra("imageURL", restaurant.imageURL.toString())
            intent.putExtra("thumbnailURL", restaurant.thumbnailURL)
            intent.putExtra("menu", restaurant.menu)
            intent.putStringArrayListExtra("highlights",
                restaurant.highlights.toMutableList() as ArrayList<String>?
            )
            intent.putExtra("phone", restaurant.phone)
            intent.putExtra("url", restaurant.url)
            intent.putExtra("rating", restaurant.user_rating.toString())
            context.startActivity(intent)

        }


    }
}