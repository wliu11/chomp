package com.example.chomp.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chomp.MainActivity
import com.example.chomp.MainViewModel
import com.example.chomp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Settings : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private fun setCity(newTitle: String) {
        activity?.findViewById<TextView>(R.id.settingsLocationTV)?.text = newTitle
    }

    private fun actionSearch(root: View) {

        val location: AutoCompleteTextView = root.findViewById(R.id.settingsLocationET)
        location.dropDownWidth = 600
        val goBut = root.findViewById<Button>(R.id.settingsGo)
        val user = FirebaseAuth.getInstance().currentUser

        viewModel.getAllCities().observe(viewLifecycleOwner,
            {
                val theAdapter = ArrayAdapter(root.context,android.R.layout.simple_list_item_single_choice, it)
                location.setAdapter(theAdapter)
            })


        goBut.setOnClickListener {
            val count = viewModel.getCityCount(location.text.toString())
            if(location.text.toString().isNotEmpty() && count >0){
                viewModel.setLocation(location.text.toString())
                user?.uid?.let { it1 -> viewModel.setUserLocation(location.text.toString(), it1) }
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

    companion object {
        fun newInstance(): Settings {
            return Settings()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.settings_layout, container, false)

        viewModel.observeCity().observe(viewLifecycleOwner,{
            setCity(it)
        })

        actionSearch(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}