package com.example.chomp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.glide.Glide
import com.example.chomp.api.RestaurantList

class RowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<RestaurantList, RowAdapter.VH>(RestaurantDiff()) {

    class RestaurantDiff : DiffUtil.ItemCallback<RestaurantList>() {

        override fun areContentsTheSame(oldItem: RestaurantList, newItem: RestaurantList): Boolean {
            //TODO("Not yet implemented")
            return oldItem.name == newItem.name
                    && oldItem.cuisines == newItem.cuisines
                    && oldItem.cost == newItem.cost
                    && oldItem.locality == oldItem.locality
        }

        override fun areItemsTheSame(oldItem: RestaurantList, newItem: RestaurantList): Boolean {
            //TODO("Not yet implemented")
            return oldItem.name == newItem.name
        }
    }

    companion object {
        val TAG = "RowAdapter"
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(view: View)
        : RecyclerView.ViewHolder(view) {

        //private var nameTextView = view.findViewById<TextView>(R.id.restaurantNameView)
        //private var priceView = view.findViewById<TextView>(R.id.priceView)

        private var image = view.findViewById<ImageView>(R.id.imageIV)
        private var restaurantName = view.findViewById<TextView>(R.id.restaurantName)
        private var rating = view.findViewById<TextView>(R.id.ratingTV)
        private var cuisine = view.findViewById<TextView>(R.id.cuisineTV)
        private var cost = view.findViewById<TextView>(R.id.costTV)
        private var address = view.findViewById<TextView>(R.id.addressTV)


        init {
            view.setOnClickListener {
                // TODO: Complete launchPost
//                MainViewModel.launchPost(view.context, getItem(adapterPosition))
            }
        }


        fun bind(item: RestaurantList) {
            restaurantName.text = item.name
            when {
                item.user_rating.aggregate_rating!! >= 4.0 -> rating.setTextColor(Color.GREEN)
                item.user_rating.aggregate_rating >= 3.0 && item.user_rating.aggregate_rating < 4.0 -> rating.setTextColor(Color.BLUE)
                item.user_rating.aggregate_rating < 3.0 -> rating.setTextColor(Color.RED)
            }
            rating.text = item.user_rating.aggregate_rating.toString()
            cuisine.text = item.cuisines
            cost.text = StringBuilder().append(item.currency).append(item.cost).append(" ").append("for two")
            address.text = item.locality?.locality_verbose.toString()
            Glide.glideFetch(item.imageURL.toString(),item.thumbnailURL,image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.row_restaurant,
            parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
