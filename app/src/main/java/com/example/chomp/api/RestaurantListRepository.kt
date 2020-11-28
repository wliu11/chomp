package com.example.chomp.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.example.chomp.MainActivity

class RestaurantListRepository(private val restaurantApi: RestaurantApi) {

    private fun unpackPosts(response: RestaurantApi.ListingData): List<RestaurantList>? {

        return response.restaurants.map {
            it.restaurant
        }
    }

    private fun unpackCollections(response: RestaurantApi.CollectionData): List<CollectionList>? {

        return response.collections.map {
            it.collection
        }
    }

    private fun unpackCities(response: RestaurantApi.CityData): CityList? {

        val city = response.location_suggestions.map {
            it
        }
        if(city.isNotEmpty())
            return city[0]
        else
            return null
    }

    suspend fun getRestaurants(city_id: Int): List<RestaurantList>? {
         //   val response = restaurantApi.getRestaurants(city_id)
        val response = restaurantApi.getRestaurants(city_id)
             return unpackPosts(response)
    }

    suspend fun getCollections(city_id: Int): List<CollectionList>? {
            val response = restaurantApi.getCollections(city_id)
             return unpackCollections(response)
    }

    suspend fun getCityID(city_name: String): CityList? {
        val response = restaurantApi.getCityID(city_name)
        return unpackCities(response)
    }
}
