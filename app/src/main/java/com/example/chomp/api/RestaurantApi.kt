package com.example.chomp.api

import android.text.SpannableString
import androidx.lifecycle.LiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type


interface RestaurantApi {

    @GET("/api/v2.1/search?entity_type=city&count=100&apikey=7b1ba5ba55dfca9b21cd7607f336e9c1")
    suspend fun getRestaurants(@Query("entity_id") entity_id: Int) : ListingData

    @GET("/api/v2.1/collections?count=100&apikey=7b1ba5ba55dfca9b21cd7607f336e9c1")
    suspend fun getCollections(@Query("city_id") city_id: Int) : CollectionData

    @GET("/api/v2.1/search?entity_type=city&count=100&apikey=7b1ba5ba55dfca9b21cd7607f336e9c1")
    suspend fun getRestaurantsByCollection(@Query("entity_id") entity_id: Int, @Query("collection_id") collection_id: Int) : ListingData

//    @GET("/api/v2.1/cities?apikey=7b1ba5ba55dfca9b21cd7607f336e9c1")
//    suspend fun getCityID(@Query("q") city_name: String) : CityData
//
//    @androidx.room.Query("SELECT * FROM cities")
//    suspend fun getAllCities() : CityData

    class ListingData(val restaurants: List<RestaurantsResponse>)
    data class RestaurantsResponse(val restaurant: RestaurantList)

    class CollectionData(val collections: List<CollectionResponse>)
    data class CollectionResponse(val collection: CollectionList)

//    data class CityData(val location_suggestions: List<CityList>)

    // This class allows Retrofit to parse items in our model of type
    // SpannableString.  Note, given the amount of "work" we do to
    // enable this behavior, one can argue that Retrofit is a bit...."simple."
    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {
        // Tell Gson to use our SpannableString deserializer
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder().registerTypeAdapter(
                SpannableString::class.java, SpannableDeserializer()
            )
            return GsonConverterFactory.create(gsonBuilder.create())
        }

        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("developers.zomato.com")
            .build()
        fun create(): RestaurantApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): RestaurantApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(RestaurantApi::class.java)
        }
    }
}