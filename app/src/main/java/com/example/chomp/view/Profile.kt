package com.example.chomp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chomp.MainViewModel
import com.example.chomp.R

class Profile : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    companion object {
        fun newInstance(): Profile {
            return Profile()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.profile_layout, container, false)
    }
}