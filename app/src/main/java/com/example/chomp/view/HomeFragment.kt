package com.example.chomp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.example.chomp.RowAdapter

// This fragment is the first page the user sees when they open the app
// It shows a list of restaurants in the user's zipcode and has a search bar for the user
// to then filter these restaurants

class HomeFragment :
        Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var swipe: SwipeRefreshLayout


    private fun initSwipeLayout(root: View) {
        swipe = root.findViewById(R.id.swipeRefreshLayout)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }

    private fun initAdapter(root: View) {
        // Initializing recyclerView
        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = RowAdapter(viewModel)
        rv.layoutManager = LinearLayoutManager(root.context)
        rv.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initSwipeLayout(view)
        initAdapter(view)

        return view
    }
}



