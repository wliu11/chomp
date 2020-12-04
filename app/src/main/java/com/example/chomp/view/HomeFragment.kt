package com.example.chomp.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chomp.*
import com.example.chomp.adapters.CollectionAdapter
import com.example.chomp.adapters.RowAdapter

class HomeFragment :
        Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var adapter: RowAdapter
    private lateinit var collectionAdapter: CollectionAdapter


    private fun initSwipeLayout(root: View) {
        swipe = root.findViewById(R.id.swipeRefreshLayout)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }

    private fun actionSearch(root: View) {

        val location = root.findViewById<EditText>(R.id.locationET)
        location
            .setOnEditorActionListener { /*v*/_, actionId, event ->
            // If user has pressed enter, or if they hit the soft keyboard "send" button
            // (which sends DONE because of the XML)
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                (activity as MainActivity).hideKeyboard()

                if(location.text.toString().isNotEmpty()){

                    viewModel.setLocation(location.text.toString())
                }
            }
            false
        }
    }

    private fun initAdapter(root: View) {
        // Initializing recyclerView
        val rvCollection = root.findViewById<RecyclerView>(R.id.horizontalRV)
        collectionAdapter = CollectionAdapter(viewModel)
        rvCollection.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
        rvCollection.adapter = collectionAdapter

        viewModel.observeCollections().observe(viewLifecycleOwner,
            {
                collectionAdapter.submitList(it)
                collectionAdapter.notifyDataSetChanged()
            })

        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = RowAdapter(viewModel)
        rv.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = adapter


        viewModel.observePosts().observe(viewLifecycleOwner,
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
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initSwipeLayout(view)
        initAdapter(view)
        actionSearch(view)
        return view
    }
}



