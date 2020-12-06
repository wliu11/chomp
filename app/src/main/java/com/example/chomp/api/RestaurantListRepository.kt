package com.example.chomp.api

class RestaurantListRepository(private val restaurantApi: RestaurantApi) {

    private fun unpackPosts(response: RestaurantApi.ListingData): List<RestaurantList>? {

        return response.restaurants.map {
            it.restaurant
        }
    }

    private fun unpackCollections(response: RestaurantApi.CollectionData?): List<CollectionList>? {

        return response?.collections?.map {
            it.collection
        }
    }

    suspend fun getRestaurants(city_id: Int): List<RestaurantList>? {
        val response = restaurantApi.getRestaurants(city_id)
             return unpackPosts(response)
    }

    suspend fun getCollections(city_id: Int): List<CollectionList>? {
        val response = restaurantApi.getCollections(city_id)
        return unpackCollections(response)
    }

    suspend fun getRestaurantsByCollection(city_id: Int, collection_id: Int): List<RestaurantList>? {
        val response = restaurantApi.getRestaurantsByCollection(city_id,collection_id)
        return unpackPosts(response)
    }

}
