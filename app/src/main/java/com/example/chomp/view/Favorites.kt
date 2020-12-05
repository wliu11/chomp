package com.example.chomp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.example.chomp.adapters.RowAdapter

class Favorites : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)

        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = RowAdapter(viewModel)
        rv.adapter = adapter

        val swipe = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

        viewModel.observeFav().observe(viewLifecycleOwner,
            Observer {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })

        return root
    }

}
