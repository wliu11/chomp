package com.example.chomp.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.chomp.R
import com.example.chomp.glide.Glide
import kotlinx.android.synthetic.main.restaurant_profile.*

class RestaurantProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_profile)

        // Obtain data that was sent
        val restaurant = intent.extras

        restaurantName.text = restaurant?.getString("name")
        Log.d("mytag", "in restaurant profile!~~~")
        // Todo: Change the getString to getInt because cost should be an integer
        restaurantCost.text = restaurant?.getString("cost")
        restaurantDescription.text = restaurant?.getString("cuisine")
//        restaurantPhone.text = restaurant?.getInt("restaurant phone number").toString()

        val imageURL = restaurant!!.getString("imageURL")
        val thumbURL = restaurant.getString("thumbnailURL")
        Log.d("mytag", "restaurant image url is " + imageURL)
        Log.d("mytag", "restaurant thumbnail url is " + thumbURL)
        Glide.glideFetch(imageURL, thumbURL, restaurantIcon)
    }
}