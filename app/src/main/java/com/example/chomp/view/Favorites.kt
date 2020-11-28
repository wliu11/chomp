package com.example.chomp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.example.chomp.RowAdapter

class Favorites : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.profile_layout, container, false)
        setHasOptionsMenu(true)

//        requireActivity().onBackPressedDispatcher.addCallback(this) {
//            (root.context as FragmentActivity).supportFragmentManager.popBackStack()
//        }

        val rv = root.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = RowAdapter(viewModel)
        rv.adapter = adapter

        val swipe = root.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }

//        viewModel.observeFav().observe(viewLifecycleOwner,
//            Observer {
//                adapter.submitList(it)
//                adapter.notifyDataSetChanged()
//            })

//        viewModel.setReddit(false)
//        viewModel.setSubredditFilt(false)
//        viewModel.setFaveFilt(true)

//        viewModel.observeLiveReddits().observe(viewLifecycleOwner,
//            Observer {list ->
//                viewModel.observeSearchTerm().observe(viewLifecycleOwner,
//                    Observer {
//                        adapter.submitList(list)
//                        adapter.notifyDataSetChanged()
//                    })
//            })

        return root
    }
}
