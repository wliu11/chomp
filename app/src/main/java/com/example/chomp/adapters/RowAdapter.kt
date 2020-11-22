package com.example.chomp

import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.api.Restaurant

class RowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Restaurant, RowAdapter.VH>(RedditDiff()) {
    class RedditDiff : DiffUtil.ItemCallback<Restaurant>() {

        override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            TODO("Not yet implemented")
        }

        override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
            TODO("Not yet implemented")
        }
    }

    companion object {
        val TAG = "RowAdapter"
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(view: View)
        : RecyclerView.ViewHolder(view) {

        private var nameTextView = view.findViewById<TextView>(R.id.restaurantNameView)
        private var priceView = view.findViewById<TextView>(R.id.priceView)


        init {
            view.setOnClickListener {
                // TODO: Complete launchPost
//                MainViewModel.launchPost(view.context, getItem(adapterPosition))
            }
        }

        fun bind(item: Restaurant) {
            nameTextView.text = item.name
            // TODO: We could change the int.toString() later to visual stars
            priceView.text = item.cost.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.row_post,
            parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
