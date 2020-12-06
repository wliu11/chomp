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

class RestaurantProfile : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_profile)

        // Hide top action toolbar
        supportActionBar?.hide()

        // Obtain data that was sent
        val restaurant = intent.extras

        val name = restaurant?.getString("name")
        val cost = restaurant?.getString("cost")
        val cuisine = restaurant?.getString("cuisine")
        val phone = restaurant?.getString("phone").toString()
        val menuURL = "<a href='" + restaurant?.getString("menu") + "'>Menu</a>"
        val url = restaurant?.getString("url")
        val imageURL = restaurant!!.getString("imageURL")
        val thumbURL = restaurant.getString("thumbnailURL")
        val highlights = restaurant.getStringArrayList("highlights")


        shareBut.setOnClickListener {
            val rec = cost?.let { it1 -> RestaurantList(name, it1, cuisine,
                null, phone, cost.toInt(), null, null, highlights as
                        ArrayList<String>,
            menuURL, phone, url) }

            Log.d("mytag", "we're sharing this to the chat")
            if (rec != null) {
                viewModel.newRecommendation(rec)
            }
            finish()

        }

        restaurantName.text = name
        // Todo: Change the getString to getInt because cost should be an integer
        restaurantCost.text = cost
        restaurantDescription.text = cuisine
        restaurantPhone.text = phone
        restaurantMenu.movementMethod = LinkMovementMethod.getInstance()
        restaurantMenu.text = Html.fromHtml(menuURL, Html.FROM_HTML_MODE_LEGACY)
        restaurantURl.text = Html.fromHtml(url, Html.FROM_HTML_MODE_LEGACY)
        Glide.glideFetch(imageURL, thumbURL, restaurantIcon)

        val listView = findViewById<ListView>(R.id.reviewList)
        val theAdapter = ReviewAdapter(this)
        listView.adapter = theAdapter

        submitRating.setOnClickListener {
            Log.d("mytag", "hiello yes we're clicking it yup hi")
            val rating = ratingBar.rating.toInt()
            val review = reviewBox.text.toString()
            Log.d("mytag", "rating is " + rating)
            Log.d("mytag", "review is " + review)
            theAdapter.add(Review(review, rating))

        }
    }
}