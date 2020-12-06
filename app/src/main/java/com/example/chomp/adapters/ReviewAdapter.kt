package com.example.chomp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.chomp.R
import com.example.chomp.api.Review

class ReviewAdapter(context: Context):
BaseAdapter() {

    private var list = mutableListOf<Review>()
    private val inflater = LayoutInflater.from(context)

    /////////////////////////////////////////////////////////////
    // These must be provided for any BaseAdapter implementation
    override fun getCount(): Int {
        return list.size //returns total of items in the list
    }
    override fun getItem(position: Int) : Review {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addAll(newList: List<Review>){
        list.addAll(newList)
    }
    fun removeAt(position: Int) {
        list.removeAt(position)
    }

    fun add(review: Review) {
        list.add(review)
    }

    private fun bindView(position: Int, view: View): View {
        // We retrieve the item from the array
        val nameAndRating = getItem(position)
        // Now we bind the item, by copying the important parts into the view
        //SSS
        val textView = view.findViewById<TextView>(R.id.reviewText)
        textView.text = nameAndRating.review

        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val optLevel = 1
        lateinit var returnView: View
        when (optLevel) {
            0 -> {
                returnView = inflater.inflate(R.layout.row_review, parent, false)
            }
            1 -> {
                if (convertView == null) {
                    Log.d("mytag", "$position NULL")
                } else {
                    Log.d("mytag", "$position not NULL")

                }
                returnView = convertView ?: inflater.inflate(R.layout.row_review, parent, false)
            }
        }
        return bindView(position, returnView)
    }

}