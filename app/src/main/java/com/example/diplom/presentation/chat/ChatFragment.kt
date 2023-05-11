package com.example.diplom.presentation.chat

import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.messages.model.response.MessageType
import com.example.diplom.data.network.messages.repository.WebSocketClient
import com.example.diplom.databinding.FragmentChatBinding
import com.example.diplom.databinding.ItemMessageInBinding
import com.example.diplom.databinding.ItemMessageOutBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toTimeString
import com.example.diplom.util.CHAT_ID
import com.example.diplom.util.USER_ID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!

    private lateinit var adapter: MessagesAdapter

    private val viewModel by viewModel<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            if(args.getLong(CHAT_ID) != 0L){
                viewModel.getChatByChatId(args.getLong(CHAT_ID))
            }else if(args.getLong(USER_ID) != 0L){
                viewModel.getChatByUserId(args.getLong(USER_ID))
            }else throw RuntimeException("Argument chatId is absent")
        }
    }


    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showChatToolbar()
        (activity as MainActivity).showUpButton()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).hideChatToolbar()
        viewModel.disconnectWebSocket()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenuToolbar()
        setOnBackPressed()
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is ChatStateUi.Chat -> handleChatInfo(state.chat)
                is ChatStateUi.Error -> requireContext().showToast(state.error)
                is ChatStateUi.Messages -> adapter.submitList(state.messages)
            }
        }
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        binding.rcMessages.layoutManager = layoutManager
        adapter = MessagesAdapter()
        binding.rcMessages.adapter = adapter
        viewModel.setMessageListener(object : WebSocketClient.SocketListener{
            override fun onMessage(message: String) {
                val msg = Json.decodeFromString<MessageResponse>(message)
                val list = adapter.currentList.toMutableList()
                list.add(0, msg)
                adapter.submitList(list)
                binding.rcMessages.smoothScrollToPosition(0)
            }
        })
        binding.btnSendMessage.setOnClickListener{
            viewModel.sendMessage(binding.etMessage.text.toString())
            binding.etMessage.text = Editable.Factory().newEditable("")
        }
    }

    private fun setOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        )
    }

    private fun setMenuToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> findNavController().popBackStack()
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun handleChatInfo(chat: ChatResponse) {
        val activity = (activity as? MainActivity) ?: return
        val chatId = chat.id
        viewModel.getMessages(chatId)
        viewModel.setChatId(chatId)
        viewModel.connectWebSocket()
        activity.setChatInfo(chat)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

class MessagesAdapter : ListAdapter<MessageResponse, RecyclerView.ViewHolder>(ChatDiffCallBack()) {

    companion object {
        const val VIEW_TYPE_IN = 1
        const val VIEW_TYPE_OUT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).type){
            MessageType.IN -> VIEW_TYPE_IN
            MessageType.OUT -> VIEW_TYPE_OUT
        }
    }

    class ViewHolderIn(val binding: ItemMessageInBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewHolderOut(val binding: ItemMessageOutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_IN -> ViewHolderIn(
                ItemMessageInBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_OUT -> ViewHolderOut(
                ItemMessageOutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("unknown item type in MessagesAdapter RecyclerView")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder){
            is ViewHolderIn -> {
                holder.binding.tvMessageTextMessageItem.text = item.text
                holder.binding.tvMessageTimeMessageItem.text = item.date.toTimeString()
            }
            is ViewHolderOut -> {
                holder.binding.tvMessageTextMessageItem.text = item.text
                holder.binding.tvMessageTimeMessageItem.text = item.date.toTimeString()
            }
        }
    }

    class ChatDiffCallBack : DiffUtil.ItemCallback<MessageResponse>() {
        override fun areItemsTheSame(oldItem: MessageResponse, newItem: MessageResponse): Boolean {
            println("areItemsTheSame $oldItem  $newItem")
            return oldItem.chatId == newItem.chatId;
        }

        override fun areContentsTheSame(oldItem: MessageResponse, newItem: MessageResponse): Boolean {
            println("areContentsTheSame $oldItem  $newItem")
            return oldItem == newItem
        }
    }
}