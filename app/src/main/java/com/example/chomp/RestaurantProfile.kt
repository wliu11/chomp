package com.example.chomp

import android.media.Rating
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Rational
import android.widget.RatingBar
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
        restaurantPhone.text = restaurant?.getString("phone").toString()
//        restaurantHighlights.text = restaurant?.getString("highlights")
        restaurantMenu.movementMethod = LinkMovementMethod.getInstance()

        val menuURL = "<a href='" + restaurant?.getString("menu") + "'>Menu</a>"
        Log.d("mytag", "menu url is $menuURL")
        restaurantMenu.text = Html.fromHtml(menuURL, Html.FROM_HTML_MODE_LEGACY)

        val url = restaurant?.getString("url")
        restaurantURl.text = Html.fromHtml(url, Html.FROM_HTML_MODE_LEGACY)
        val imageURL = restaurant!!.getString("imageURL")
        val thumbURL = restaurant.getString("thumbnailURL")

        Glide.glideFetch(imageURL, thumbURL, restaurantIcon)

        submitRating.setOnClickListener {
            val rating = ratingBar.rating.toString()
            val review = reviewBox.text
            Log.d("mytag", "rating is " + rating)
            Log.d("mytag", "review is " + review)
        }
    }
}