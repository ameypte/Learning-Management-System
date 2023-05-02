package com.example.staffdashboard

import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.staffdashboard.databinding.FragmentMessagesBinding


class Messages : Fragment() {
    private lateinit var messagesBinding: FragmentMessagesBinding
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        messagesBinding = FragmentMessagesBinding.inflate(inflater, container, false)

        adapter = MessagesAdapter(messages)
        messagesBinding.messageRecyclerView.adapter = adapter

        messagesBinding.btnsend.setOnClickListener{
            val msg = messagesBinding.etMessage.text.toString()
            Toast.makeText(requireContext(),"$msg",Toast.LENGTH_SHORT).show()
//            val message = Message(
//                UUID.randomUUID().toString(), // generate a unique ID for the message
//                "sender_id", // replace with actual sender ID
//                "receiver_id", // replace with actual receiver ID
//                msg,
//                System.currentTimeMillis()
//            )
//            messages.add(message)
//            adapter.notifyItemInserted(messages.size - 1)
//            messagesBinding.messageRecyclerView.scrollToPosition(messages.size - 1)
//            messagesBinding.etMessage.setText("")
        }
        return messagesBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Messages().apply {
                arguments = Bundle().apply {
                }
            }
    }
}