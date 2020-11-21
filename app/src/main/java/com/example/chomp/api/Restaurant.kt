package com.example.chomp.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.google.gson.annotations.SerializedName

data class Restaurant (
    @SerializedName("name")
    val name: String,
    @SerializedName("icon_img")
    val iconURL: String?,
    @SerializedName("public_description")
    val publicDescription: SpannableString?,
    @SerializedName("rating")
    val rating: Int,
    @SerializedName("cost")
    val cost: Int,
    @SerializedName("title")
    val title: SpannableString,
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
