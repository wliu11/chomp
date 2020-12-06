package com.example.chomp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.*
import com.example.chomp.adapters.RowAdapter
import com.example.chomp.glide.Glide

class CollectionsFragment :
    Fragment(R.layout.fragment_collections) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RowAdapter


    private fun initAdapter(root: View) {

        val title: TextView = root.findViewById(R.id.collectionName)
        val desc: TextView = root.findViewById(R.id.collectionDesc)
        val imageurl: ImageView = root.findViewById(R.id.collectionIV)

        viewModel.observeCollectionTitle().observe(viewLifecycleOwner,
            {
                title.text = it
            })

        viewModel.observeColectionDesc().observe(viewLifecycleOwner,
            {
                desc.text = it
            })

        viewModel.observeCollectionImageURL().observe(viewLifecycleOwner,
            {
                Glide.glideFetchCollection(it,it,imageurl)
            })

        val rv = root.findViewById<RecyclerView>(R.id.collectionRecyclerView)
        adapter = RowAdapter(viewModel)
        rv.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter

        viewModel.observePostsByCollection().observe(viewLifecycleOwner,
            {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_collections, container, false)
        initAdapter(view)
        val backBut = view.findViewById<ImageView>(R.id.backBut)
        backBut.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_to_HomeFragment)
        }

        return view
    }
}

