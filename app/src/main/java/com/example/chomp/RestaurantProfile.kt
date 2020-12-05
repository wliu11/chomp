package com.example.chomp

import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.chomp.api.RestaurantList
import com.example.chomp.api.User_Rating
import com.example.chomp.glide.Glide
import kotlinx.android.synthetic.main.restaurant_profile.*

class RestaurantProfile : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

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
        submitRating.setOnClickListener {
            val rating = ratingBar.rating.toString()
            val review = reviewBox.text
        }

        viewModel.recommendation.observe(this, Observer {
            Log.d("mytag", "can we update the chat?")
//            val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//                    as NavHostFragment).navController
//            val navController = findNavController(parentActivityIntent.)
//            Log.d("mytag", "current dest " + navController.currentDestination?.id)
//            if (navController.currentDestination?.id == R.id.chatFragment) // Id as per set up on nav_graph.xml file
//            {/
//                Log.d("mytag", "matches")
        //
//             navController.navigate(R.id.chatFragment)
//            }
        })
    }
}