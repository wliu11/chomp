package com.example.chomp

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.chomp.adapters.ReviewAdapter
import com.example.chomp.api.RestaurantList
import com.example.chomp.api.Review
import com.example.chomp.api.User_Rating
import com.example.chomp.glide.Glide
import kotlinx.android.synthetic.main.restaurant_profile.*
import kotlin.math.cos

class RestaurantProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_profile)

        // Hide top action toolbar
        supportActionBar?.hide()

        // Obtain data that was sent
        val restaurant = intent.extras

        val name = restaurant?.getString("name")
        val cost = "Average cost for two: $" + restaurant?.getString("cost")
        val cuisine = "Type of cuisine: " + restaurant?.getString("cuisine")
        val phone = restaurant?.getString("phone").toString()
        val menuURL = "<a href='" + restaurant?.getString("menu") + "'>Menu</a>"
        val url = "<a href='" + restaurant?.getString("url") + "'>Website</a>"
        val imageURL = restaurant!!.getString("imageURL")
        val thumbURL = restaurant.getString("thumbnailURL")
        val highlights = restaurant.getStringArrayList("highlights")
        val rating = restaurant.getString("rating")

        restaurantName.text = name
        // Todo: Change the getString to getInt because cost should be an integer
        restaurantCost.text = cost
        restaurantDescription.text = cuisine
        restaurantPhone.text = phone
        restaurantMenu.movementMethod = LinkMovementMethod.getInstance()
        restaurantMenu.text = Html.fromHtml(menuURL, Html.FROM_HTML_MODE_LEGACY)
        restaurantURl.movementMethod = LinkMovementMethod.getInstance()
        restaurantURl.text = Html.fromHtml(url, Html.FROM_HTML_MODE_LEGACY)
        reviewRating.text = rating

        Glide.glideFetch(imageURL, thumbURL, restaurantIcon)

        val highlightAdapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, highlights)
        restaurantHighlights.adapter = highlightAdapter


        val listView = findViewById<ListView>(R.id.reviewList)
        val reviewAdapter = ReviewAdapter(this)
        listView.adapter = reviewAdapter

        submitRating.setOnClickListener {
            val yourRating = ratingBar.rating.toInt()
            val yourReview = reviewBox.text.toString()
            reviewAdapter.add(Review(yourReview, yourRating))
            reviewAdapter.notifyDataSetChanged()

            // Clear the review textbox after review was recorded
            reviewBox.text.clear()
        }
    }
}