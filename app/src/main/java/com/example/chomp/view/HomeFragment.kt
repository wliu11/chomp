package com.example.chomp.view

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
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

    private fun setCity(newTitle: String) {
        activity?.findViewById<TextView>(R.id.currentCity)?.text = newTitle
    }


    private fun initSwipeLayout(root: View) {
        swipe = root.findViewById(R.id.swipeRefreshLayout)
        swipe.setOnRefreshListener {
            swipe.isRefreshing = false
        }
    }

    private fun actionSearch(root: View) {

        val location: AutoCompleteTextView = root.findViewById(R.id.locationET)
        val goBut = root.findViewById<Button>(R.id.goBut)

        viewModel.getAllCities().observe(viewLifecycleOwner,
            {
                val theAdapter = ArrayAdapter(root.context,android.R.layout.simple_list_item_single_choice, it)
                location.setAdapter(theAdapter)
        })

        goBut.setOnClickListener {
            val count = viewModel.getCityCount(location.text.toString())
            if(location.text.toString().isNotEmpty() && count >0){
                viewModel.setLocation(location.text.toString())
                location.text.clear()
            }

            else{
                Toast.makeText(root.context, "Invalid Location", Toast.LENGTH_SHORT).show()
                location.text.clear()
            }

            (activity as MainActivity).hideKeyboard()
        }

        location
            .setOnEditorActionListener { /*v*/_, actionId, event ->
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                (activity as MainActivity).hideKeyboard()
                goBut.callOnClick()
            }
            false
        }
    }

    private fun initAdapter(root: View) {

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

        viewModel.observeDummy().observe(viewLifecycleOwner,
            {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            })

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
        viewModel.observeCity().observe(viewLifecycleOwner,{
            setCity(it)
        })
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initSwipeLayout(view)
        initAdapter(view)
        actionSearch(view)
        return view
    }
}



