package com.example.chomp

import android.app.Application
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.chomp.api.CollectionList
import com.example.chomp.api.RestaurantList
import com.example.chomp.api.RestaurantApi
import com.example.chomp.api.RestaurantListRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.chomp.glide.Glide
import com.example.chomp.roomdb.City
import com.example.chomp.roomdb.CityRepository
import com.example.chomp.RestaurantProfile
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

    private var cityName = MutableLiveData<String>()
    private var cityId = MutableLiveData<Int>()

    private var collections = MutableLiveData<List<CollectionList>>()

    private var collectionId = MutableLiveData<Int>()
    private var collectionTitle = MutableLiveData<String>()
    private var collectionDesc = MutableLiveData<String>()
    private var collectionImageURL = MutableLiveData<String>()

    private var profileUri = MutableLiveData<Uri>()

    private var restaurantsByCollection = MutableLiveData<List<RestaurantList>>()

    private val cityRepository = CityRepository(application)
    private val allCities = cityRepository.getAllCities()


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

    private fun postsByCollection() = viewModelScope.launch(
        context = viewModelScope.coroutineContext
                + Dispatchers.IO) {
        // Update LiveData from IO dispatcher, use postValue
        restaurantsByCollection.postValue(cityId.value?.let {
            collectionId.value?.let { it1 ->
                restaurantRepository.getRestaurantsByCollection(
                    it, it1
                )
            }
        })
    }

    init {
        val user = FirebaseAuth.getInstance().currentUser
        val city = user?.uid?.let { cityRepository.getUserCityName(it) }
        if(city.isNullOrBlank()){
                cityName.value = "Austin,TX"
                cityId.value = 278
            }
        else{
            cityName.value = city
            cityId.value = cityRepository.getCityId(city)
        }
    }

    fun setLocation(newLocation: String) {
        cityName.value = newLocation
        Log.d("XXX", "New City name: $newLocation")
        cityId.postValue(cityRepository.getCityId(newLocation))
    }

    fun setUserLocation(newLocation: String, user: String) {
        Log.d("XXX", "New Default City for $user is: $newLocation")
        val count = cityRepository.getUserCity(user)
        if(count >0){
            Log.d("xxx","count is $count Updating")
            cityRepository.updateUser(user,newLocation)
        }
        else{
            cityRepository.insertUser(user,newLocation)
            Log.d("xxx","count is $count Inserting")
        }

    }

    fun getCityCount(newLocation: String): Int {
        return cityRepository.getCityCount(newLocation)
    }

    fun setCollection(newCollection: CollectionList) {
        collectionId.value = newCollection.collection_id
        collectionTitle.value = newCollection.title.toString()
        collectionDesc.value = newCollection.description.toString()
        collectionImageURL.value = newCollection.image_url
        Log.d("XXX", "New Collection name: ${newCollection.title.toString()}")
        Log.d("XXX", "New Collection id: ${newCollection.collection_id.toString()}")
    }

    fun setImageURI(newImage: Uri) {
        profileUri.value = newImage
    }

    private var  newRestaurants = MediatorLiveData<List<RestaurantList>>().apply {

        addSource(cityId) {value = newList()}

        value = restaurants.value
    }

    private fun newList(): List<RestaurantList>? {
        posts()
        collections()
        return restaurants.value
    }

    fun observePosts(): LiveData<List<RestaurantList>> {
        return restaurants
    }

    fun observeDummy(): LiveData<List<RestaurantList>> {
        return newRestaurants
    }

    fun observeCollections(): LiveData<List<CollectionList>> {
        return collections
    }

    fun observePostsByCollection(): LiveData<List<RestaurantList>> {
        postsByCollection()
        return restaurantsByCollection
    }

    fun getAllCities(): LiveData<List<String>> {
        return allCities
    }

    fun observeCity(): LiveData<String> {
        return cityName
    }

    fun observeCollectionTitle(): LiveData<String> {
        return collectionTitle
    }

    fun observeColectionDesc(): LiveData<String> {
        return collectionDesc
    }

    fun observeCollectionImageURL(): LiveData<String> {
        return collectionImageURL
    }

    fun observeProfileURI(): LiveData<Uri> {
        return profileUri
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
}