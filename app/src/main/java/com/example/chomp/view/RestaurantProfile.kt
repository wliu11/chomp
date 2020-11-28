package com.example.chomp.view

import android.os.Bundle
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

        restaurantName.text = restaurant?.getString("restaurant name")

        // Todo: Change the getString to getInt because cost should be an integer
        restaurantCost.text = restaurant?.getString("restaurant cost")
        restaurantDescription.text = restaurant?.getString("restaurant description")
        restaurantPhone.text = restaurant?.getInt("restaurant phone number").toString()

        val imageURL = restaurant!!.getString("restaurant image")
        val thumbURL = restaurant.getString("restaurant icon")

        Glide.glidefetch(imageURL, thumbURL, restaurantIcon)
    }
}