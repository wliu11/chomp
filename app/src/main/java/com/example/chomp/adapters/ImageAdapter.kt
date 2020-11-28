package com.example.chomp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.MainViewModel
import com.example.chomp.R

class ImageAdapter(private val viewModel: MainViewModel,
                   private val deletePos:((Int)->Unit)? = null) :
    ListAdapter<String, ImageAdapter.VH>(Diff()) {

    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    inner class VH(view: View) :
        RecyclerView.ViewHolder(view) {
       // private var photoIB: ImageButton = view.findViewById(R.id.photoIB)
        private var photoIB: ImageButton = view.findViewById(R.id.icon)  // Venkat - Just to compile
        fun bind(pictureUUID: String, position: Int) {
            viewModel.glideFetch(pictureUUID, photoIB)
            if(deletePos == null ) {
                photoIB.isLongClickable = false
            } else {
                photoIB.setOnLongClickListener {
                    deletePos.invoke(position)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context).inflate(
           // R.layout.photo_list_row,
            R.layout.row_chat, // Venkat - Just to compile
            parent, false)
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(currentList[holder.adapterPosition], holder.adapterPosition)
    }
}