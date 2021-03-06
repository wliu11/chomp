package com.example.chomp.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.MutableLiveData
import com.google.gson.annotations.SerializedName

data class User_Rating(
    @SerializedName("aggregate_rating")
    val aggregate_rating: Double?
)

data class Location(
    @SerializedName("locality_verbose")
    val locality_verbose: SpannableString?
)

data class RestaurantList(
    @SerializedName("name")
    val name: String?,
    @SerializedName("thumb")
    val thumbnailURL: String?,
    @SerializedName("featured_image")
    val imageURL: String?,
    @SerializedName("user_rating")
    val user_rating: User_Rating?,
    @SerializedName("cuisines")
    val cuisines: String?,
    @SerializedName("average_cost_for_two")
    val cost: Int?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("location")
    val locality: Location?,
    @SerializedName("highlights")
    val highlights: List<String>,
    @SerializedName("menu_url")
    val menu: String?,
    @SerializedName("phone_numbers")
    val phone: String?,
    @SerializedName("url")
    val url: String?,
    ) {
    companion object {
        // NB: This only highlights the first match
        private fun setSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.BLUE), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }

        // XXX Failed experiment.  Seems to always return true
        fun spannableStringsEqual(a: SpannableString?, b: SpannableString?): Boolean {
            if (a == null && b == null) return true
            if (a == null && b != null) return false
            if (a != null && b == null) return false
            val spA = a!!.getSpans<Any>(0, a.length, Any::class.java)
            val spB = b!!.getSpans<Any>(0, b.length, Any::class.java)
            return a.toString() == b.toString()
                    &&
                    spA.contentDeepEquals(spB)
        }
    }
}
