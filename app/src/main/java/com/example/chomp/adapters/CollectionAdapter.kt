package com.example.chomp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.example.chomp.api.CollectionList
import com.example.chomp.glide.Glide
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController

class CollectionAdapter(private val viewModel: MainViewModel)
    : ListAdapter<CollectionList, CollectionAdapter.VH>(CollectionDiff()) {
    private lateinit var navigationController: NavController

    class CollectionDiff : DiffUtil.ItemCallback<CollectionList>() {

        override fun areContentsTheSame(oldItem: CollectionList, newItem: CollectionList): Boolean {
            return oldItem.title == newItem.title
                    && oldItem.res_count == newItem.res_count
                    && oldItem.image_url == newItem.image_url
        }

        override fun areItemsTheSame(oldItem: CollectionList, newItem: CollectionList): Boolean {
            return oldItem.title == newItem.title
        }
    }

    companion object {
        val TAG = "CollectionAdapter"
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(view: View)
        : RecyclerView.ViewHolder(view) {

        private var collectionImage = view.findViewById<ImageView>(R.id.collectionImageIV)
        private var collectionName = view.findViewById<TextView>(R.id.collectionName)
        private var collectionResCount = view.findViewById<TextView>(R.id.collectionResCount)

        init {
            view.setOnClickListener {
                viewModel.setCollection(getItem(adapterPosition))
                findNavController(it).navigate(R.id.action_to_CollectionsFragment)
            }

        }

        fun bind(item: CollectionList) {
            collectionName.text = item.title
            collectionResCount.text = StringBuilder().append(item.res_count.toString()).append(" Restaurants")
            Glide.glideFetchCollection(item.image_url.toString(),item.image_url.toString(),collectionImage)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_collection,
                parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
