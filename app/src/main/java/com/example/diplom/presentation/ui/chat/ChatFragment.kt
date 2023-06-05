package com.example.diplom.presentation.ui.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.chats.model.response.ChatResponse
import com.example.diplom.data.network.messages.model.request.MessageRequest
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.messages.model.response.MessageType
import com.example.diplom.data.network.messages.repository.WebSocketClient
import com.example.diplom.databinding.FragmentChatBinding
import com.example.diplom.databinding.ItemMessageInBinding
import com.example.diplom.databinding.ItemMessageOutBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.calculateInSampleSize
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.common.toTimeString
import com.example.diplom.presentation.ui.createpost.SelectedPhotoActions
import com.example.diplom.presentation.ui.createpost.SelectedPhotoModel
import com.example.diplom.presentation.ui.createpost.SelectedPhotosAdapter
import com.example.diplom.presentation.ui.photoview.PhotoViewPagerFragment
import com.example.diplom.util.CHAT_ID
import com.example.diplom.util.USER_ID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding get() = _binding!!

    private lateinit var photoAdapter: SelectedPhotosAdapter
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var adapter: MessagesAdapter

    private var messagesLoaded = false

    private var chatId: Long = 0
    private var chat: ChatResponse? = null

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
        pickMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(9)) { uris ->
            if(viewModel.getItemsCount() + uris.count() > 9) {
                requireContext().showToast("Количество фотографий не должно быть больше 9")
                return@registerForActivityResult
            }
            for(uri in uris){
                println(uri)
                val fileName = UUID.randomUUID().toString()
                val file = File(requireContext().getCacheDir(), "$fileName.png")
                try {
                    var instream: InputStream =
                        requireContext().contentResolver.openInputStream(uri)!!
                    val output = FileOutputStream(file)

                    var sampleSize = 0
                    BitmapFactory.Options().run {
                        inJustDecodeBounds = true
                        BitmapFactory.decodeStream(instream, null, this)
                        sampleSize = calculateInSampleSize(this, 768, 1024)
                    }
                    instream.close()

                    instream = requireContext().contentResolver.openInputStream(uri)!!
                    val bitmap = BitmapFactory.Options().run {
                        inJustDecodeBounds = false
                        inSampleSize = sampleSize
                        BitmapFactory.decodeStream(instream, null, this)
                    }


                    bitmap?.compress(Bitmap.CompressFormat.PNG, 0, output) ?: throw RuntimeException("bitmap null")

//                    val buffer = ByteArray(1024)
//                    var size: Int
//                    while (instream.read(buffer).also { size = it } != -1) {
//                        output.write(buffer, 0, size)
//                    }
                    instream.close()
                    output.close()
                    viewModel.addPhoto(SelectedPhotoModel(id = fileName, file = file))
                } catch (e: IOException) {
                    Log.d("TAG1", "e: ${e}")
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        println(adapter.currentList)
        MainActivity.currentChatId = chatId
//        if (messagesLoaded)
//            if(adapter.currentList.isNotEmpty())
//                viewModel.getMessagesFromMessageId(chatId, adapter.currentList[0].id)
        (activity as MainActivity).showChatToolbar()
        (activity as MainActivity).hideBottomMenu()
        (activity as MainActivity).showUpButton()
        if(chat != null) {
            (activity as MainActivity).setChatInfo(chat!!)
            viewModel.connectWebSocket()
        }
    }

    override fun onStop() {
        super.onStop()
        println(adapter.currentList)
        (activity as MainActivity).hideChatToolbar()
        (activity as MainActivity).showBottomMenu()
        viewModel.disconnectWebSocket()
        MainActivity.currentChatId = null
        println(adapter.currentList)
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
                is ChatStateUi.Messages -> {
                    adapter.submitList(state.messages)
                    messagesLoaded = true
                }
                is ChatStateUi.Photos -> {
                    println("new photos")
                    println(state.photos.count())
                    if(state.photos.isEmpty()) {
                        binding.rcPhotos.visibility = View.GONE
                        binding.dividerPhotos.visibility = View.GONE
                    }else{
                        binding.rcPhotos.visibility = View.VISIBLE
                        binding.dividerPhotos.visibility = View.VISIBLE
                    }
                    photoAdapter.submitList(state.photos.toList())
                }
                is ChatStateUi.NewMessages -> {
                    if(state.messages.isNotEmpty()) {
                        val list = adapter.currentList.toMutableList()
                        list.addAll(0, state.messages)
                        println(list)
                        adapter.submitList(list)
                    }
                }
            }
        }

        photoAdapter = SelectedPhotosAdapter(object : SelectedPhotoActions {
            override fun deleteImage(id: String) {
                viewModel.deletePhoto(id)
            }

            override fun reloadImage(photo: SelectedPhotoModel) {
                viewModel.reloadPhoto(photo)
            }

        })
        val photoRecycler = binding.rcPhotos
        photoRecycler.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        photoRecycler.adapter = photoAdapter

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        binding.rcMessages.layoutManager = layoutManager
        adapter = MessagesAdapter(object : MessageActions{
            override fun readMessage(message: MessageResponse) {
                viewModel.readMessage(message.chatId, message.id)
            }

            override fun photoClick(photos: List<String>, photoIndex: Int) {
                findNavController().navigate(R.id.action_chatFragment_to_photoViewPagerFragment,
                    bundleOf(
                        PhotoViewPagerFragment.PHOTOS to ArrayList(photos),
                        PhotoViewPagerFragment.PHOTO_INDEX to photoIndex)
                )
            }
        })
        binding.rcMessages.adapter = adapter
        viewModel.setMessageListener(object : WebSocketClient.SocketListener{
            override fun onMessage(message: String) {
                val msg = Json.decodeFromString<MessageResponse>(message)
                val list = adapter.currentList.toMutableList()
                list.add(0, msg)
                adapter.submitList(list)
                binding.rcMessages.smoothScrollToPosition(-1)
            }
        })
        binding.btnSendMessage.setOnClickListener{
            val message = MessageRequest(binding.etMessage.text.toString(), listOf())
            viewModel.sendMessage(message)
            binding.etMessage.text = Editable.Factory().newEditable("")
        }
        binding.imgAttachPhotos.setOnClickListener{
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun setOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.deleteAllPhotos()
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
                    android.R.id.home -> {
                        viewModel.deleteAllPhotos()
                        findNavController().popBackStack()
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun handleChatInfo(chat: ChatResponse) {
        MainActivity.currentChatId = chat.id
        val activity = (activity as? MainActivity) ?: return
        chatId = chat.id
        this.chat = chat
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

class MessagesAdapter(private val actions: MessageActions) : ListAdapter<MessageResponse, RecyclerView.ViewHolder>(ChatDiffCallBack()) {

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
                val list = listOf(
                    holder.binding.imgMessageImage1,
                    holder.binding.imgMessageImage2,
                    holder.binding.imgMessageImage3,
                    holder.binding.imgMessageImage4,
                    holder.binding.imgMessageImage5,
                    holder.binding.imgMessageImage6,
                    holder.binding.imgMessageImage7,
                    holder.binding.imgMessageImage8,
                    holder.binding.imgMessageImage9
                )
                sentGoneVisibilityImages(list)
                setOnClickImage(list, item.images)
                if(!item.isRead) actions.readMessage(item)
                loadPhotosInImageViews(list, item.images)
            }
            is ViewHolderOut -> {
                holder.binding.tvMessageTextMessageItem.text = item.text
                holder.binding.tvMessageTimeMessageItem.text = item.date.toTimeString()
                val list = listOf(
                    holder.binding.imgMessageImage1,
                    holder.binding.imgMessageImage2,
                    holder.binding.imgMessageImage3,
                    holder.binding.imgMessageImage4,
                    holder.binding.imgMessageImage5,
                    holder.binding.imgMessageImage6,
                    holder.binding.imgMessageImage7,
                    holder.binding.imgMessageImage8,
                    holder.binding.imgMessageImage9
                )
                sentGoneVisibilityImages(list)
                setOnClickImage(list, item.images)
                loadPhotosInImageViews(list, item.images)
            }
        }
    }

    private fun sentGoneVisibilityImages(list: List<ImageView>){
        list.forEach{it.visibility = View.GONE}
    }

    private fun setOnClickImage(imageViews: List<ImageView>, photos: List<String>){
        for(i in photos.indices){
            imageViews[i].setOnClickListener { actions.photoClick(photos, i) }
        }
    }

    private fun loadPhotosInImageViews(imageViews: List<ImageView>, photos: List<String>){
        for(i in photos.indices){
            imageViews[i].visibility = View.VISIBLE
            Glide
                .with(imageViews[i].context)
                .load(photos[i].toPhotoURL())
                .into(imageViews[i])
            if (i == 8) break
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

interface MessageActions{
    fun readMessage(message: MessageResponse)
    fun photoClick(photos: List<String>, photoIndex: Int)
}