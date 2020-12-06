package com.example.chomp.view

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chomp.*
import com.example.chomp.adapters.FirestoreChatAdapter
import com.example.chomp.api.RestaurantList
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.chat_page.*

class ChatFragment :
    Fragment(R.layout.chat_page) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: FirestoreChatAdapter
    private var currentUser: FirebaseUser? = null
    private var fragmentUUID: String? = null


    private fun scrollToEnd(chatRV: RecyclerView) =
        (chatAdapter.itemCount - 1).takeIf { it > 0 }?.let(chatRV::smoothScrollToPosition)

    private fun initRecyclerView() {
        chatAdapter = FirestoreChatAdapter(viewModel)
        val chatRV = activity?.findViewById<RecyclerView>(R.id.chatRV)
        chatRV?.adapter = chatAdapter
        chatRV?.layoutManager = LinearLayoutManager(context)
        //https://stackoverflow.com/questions/26580723/how-to-scroll-to-the-bottom-of-a-recyclerview-scrolltoposition-doesnt-work
        chatRV?.viewTreeObserver?.addOnGlobalLayoutListener {
            scrollToEnd(chatRV)
        }
    }

    private fun initAuth() {
        viewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
        })
    }

    private fun clearCompose() {
        // XXX Write me
        composeMessageET.text.clear()
        composePreviewIV.visibility = View.GONE
        fragmentUUID = null
    }

    private fun initComposeSendIB() {
        // Send message button
        composeSendIB.setOnClickListener {
            if (composeMessageET.text.isNotEmpty()) {
                val chatRow = ChatRow().apply {
                    val cUser = currentUser
                    if (cUser == null) {
                        name = "unknown"
                        ownerUid = "unknown"
                        Log.d("HomeFragment", "XXX, currentUser null!")
                    } else {
                        name = cUser.displayName
                        ownerUid = cUser.uid
                    }
                    message = composeMessageET.text.toString()
                    clearCompose()
                }
                viewModel.saveChatRow(chatRow)
            }
        }
    }

    // Something might have changed.  Redo query
    override fun onResume() {
        super.onResume()
        viewModel.getChat()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAuth()
        initComposeSendIB()
        initRecyclerView()

        viewModel.observeChat().observe(viewLifecycleOwner, Observer {
            chatAdapter.submitList(it)
            Log.d("mytag", "we're refreshing the list")
        })

        composeMessageET.setOnEditorActionListener { /*v*/_, actionId, event ->
            // If user has pressed enter, or if they hit the soft keyboard "send" button
            // (which sends DONE because of the XML)
            if ((event != null
                        && (event.action == KeyEvent.ACTION_DOWN)
                        && (event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)
            ) {
                (requireActivity() as MainActivity).hideKeyboard()
                composeSendIB.callOnClick()
            }
            true
        }
        composePreviewIV.visibility = View.GONE
    }

}

