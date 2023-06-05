package com.example.diplom.presentation.ui.chats

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestListener
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatPreview
import com.example.diplom.data.network.messages.model.response.MessageType
import com.example.diplom.databinding.FragmentChatsBinding
import com.example.diplom.databinding.ItemChatBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.common.toTimePassed
import com.example.diplom.util.BASE_URL
import com.example.diplom.util.CHAT_ID
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask

class ChatsFragment : Fragment() {

    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!

    private lateinit var timer: CountDownTimer

    private val viewModel by viewModel<ChatsViewModel>()
    private lateinit var adapter: ChatsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getChats()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        timer = object : CountDownTimer(20000, 2000){
            override fun onTick(millisUntilFinished: Long) {
                viewModel.getChats()
            }
            override fun onFinish() {
                timer.start()
            }
        }.start()
        MainActivity.getUnreadMessagesCount?.invoke()
    }

    override fun onStop() {
        super.onStop()
        timer.cancel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_messages)
        binding.rcChats.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatsAdapter(object : ChatsClickListener{
            override fun onItemClick(item: ChatPreview) {
                findNavController()
                    .navigate(R.id.action_chats_fragment_to_chatFragment,
                        bundleOf( CHAT_ID to item.chatId))
            }
        })
        binding.rcChats.adapter = adapter
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is ChatsStateUi.Chats -> handleChats(state.chats)
                is ChatsStateUi.Error -> requireContext().showToast(state.error)
            }
        }
    }

    private fun handleChats(chats: List<ChatPreview>){
        println(chats)
        adapter.submitList(chats)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class ChatsAdapter(private val listener: ChatsClickListener) :
    ListAdapter<ChatPreview, ChatsAdapter.ViewHolder>(ChatDiffCallBack()) {

    class ViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        with(holder){
            val msgText = item.lastMessage.text
            when(item.lastMessage.type){
                MessageType.IN -> {
                    binding.tvMessageTextCahtItem.text = msgText
                    if (item.lastMessage.isRead) {
                        binding.tvUnreadMessagesCount.visibility = View.GONE
                    } else {
                        binding.tvUnreadMessagesCount.visibility = View.VISIBLE
                        binding.tvUnreadMessagesCount.text = item.unreadMessagesCount.toString()
                    }

                        if (item.lastMessage.isRead) View.GONE else View.VISIBLE

                }
                MessageType.OUT -> {
                    binding.tvMessageTextCahtItem.text = "Вы: $msgText"
                    binding.imgUnreadOutMessage.visibility =
                        if (item.lastMessage.isRead) View.GONE else View.VISIBLE
                }
            }
            binding.tvTimeChatItem.text = item.lastMessage.date.toTimePassed()
            binding.tvNameChatItem.text = "${item.name} ${item.surname}"
            binding.root.setOnClickListener {
                listener.onItemClick(item)
            }

            if (!item.icon.isNullOrEmpty()) {

                Glide
                    .with(holder.itemView.context)
                    .load(item.icon.toPhotoURL())
//                    .timeout(60000)

                    .into(binding.imageView2)
            }
            println("bind view holder")
        }
    }

    class ChatDiffCallBack : DiffUtil.ItemCallback<ChatPreview>() {
        override fun areItemsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
            println("areItemsTheSame $oldItem  $newItem")
            return oldItem.chatId == newItem.chatId;
        }

        override fun areContentsTheSame(oldItem: ChatPreview, newItem: ChatPreview): Boolean {
            println("areContentsTheSame $oldItem  $newItem")
            return oldItem == newItem
        }
    }
}

interface ChatsClickListener {
    fun onItemClick(item: ChatPreview)
}
