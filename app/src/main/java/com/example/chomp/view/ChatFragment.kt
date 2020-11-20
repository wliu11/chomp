package com.example.chomp.view

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo

import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chomp.*
import com.google.firebase.auth.FirebaseUser


class ChatFragment :
    Fragment(R.layout.chat_page) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: FirestoreChatAdapter
    private var currentUser: FirebaseUser? = null
    private var fragmentUUID: String? = null


    private fun scrollToEnd() =
        (chatAdapter.itemCount - 1).takeIf { it > 0 }?.let(chatRV::smoothScrollToPosition)

    private fun initRecyclerView() {
        chatAdapter = FirestoreChatAdapter(viewModel)
        chatRV.adapter = chatAdapter
        chatRV.layoutManager = LinearLayoutManager(context)
        //https://stackoverflow.com/questions/26580723/how-to-scroll-to-the-bottom-of-a-recyclerview-scrolltoposition-doesnt-work
        chatRV.viewTreeObserver.addOnGlobalLayoutListener {
            scrollToEnd()
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

        Log.d("mytag", "clear compose done")
    }

    // For our phone, translate dp to pixels
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun initComposeSendIB() {
        // Send message button
        composeSendIB.setOnClickListener {
            if( composeMessageET.text.isNotEmpty()) {
                val chatRow = ChatRow().apply {
                    val cUser = currentUser
                    if(cUser == null) {
                        name = "unknown"
                        ownerUid = "unknown"
                        Log.d("HomeFragment", "XXX, currentUser null!")
                    } else {
                        name = cUser.displayName
                        ownerUid = cUser.uid
                    }
                    message = composeMessageET.text.toString()
                    pictureUUID = fragmentUUID
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
            Log.d(javaClass.simpleName, "Observe Chat $it")
            chatAdapter.submitList(it)
        })

        composeMessageET.setOnEditorActionListener { /*v*/_, actionId, event ->
            // If user has pressed enter, or if they hit the soft keyboard "send" button
            // (which sends DONE because of the XML)
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
                (requireActivity() as MainActivity).hideKeyboard()
                composeSendIB.callOnClick()
            }
            true
        }
        composeCameraIB.setOnClickListener {
            viewModel.takePhoto {
                Log.d(javaClass.simpleName, "uuid $it")
                fragmentUUID = it
                composePreviewIV.doOnLayout {view ->
                    view.updateLayoutParams {
                        width = viewModel.fourFifthWidthPx
                    }
                }
                composePreviewIV.visibility = View.VISIBLE
                viewModel.glideFetch(it, composePreviewIV)
            }
        }
        composePreviewIV.visibility = View.GONE
        //Log.d(javaClass.simpleName, "vm 1/5 ${viewModel.oneFifthWidthPx} mine ${(resources.displayMetrics.widthPixels / 5).toInt()}")
    }

}
