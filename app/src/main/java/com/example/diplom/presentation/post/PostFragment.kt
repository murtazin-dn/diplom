package com.example.diplom.presentation.post

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.PostCommentItem
import com.example.diplom.databinding.FragmentPostBinding
import com.example.diplom.databinding.FragmentPostsBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.post.adapter.ViewHoldersManager
import com.example.diplom.util.POST_ID
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding: FragmentPostBinding get() = _binding!!
    private val viewHoldersManager: ViewHoldersManager by inject()
    private lateinit var adapter: PostCommentsAdapter

    private var postId: Long? = null
    private val list = mutableListOf<PostCommentItem>()

    private var isPostLoaded = false
    private var isCommentsLoaded = false

    private val viewModel by viewModel<PostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getLong(POST_ID)?.let { id ->
            postId = id
            loadData()
        }
    }

    private fun loadData() {
        list.clear()
        viewModel.getPost(postId!!)
        viewModel.getComments(postId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_comments)
        setupRecycler()
        setupObserver()
        setMenuToolbar()
        setOnBackPressed()
        setListeners()
    }

    private fun setListeners() {
        binding.btnSendComment.setOnClickListener{
            viewModel.createComment(postId!!, binding.etComment.text.toString())
            binding.etComment.text = Editable.Factory().newEditable("")
        }
    }

    private fun setupObserver() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PostStateUi.Comments -> handleComments(state.comments)
                is PostStateUi.Error -> TODO()
                is PostStateUi.Post -> handlePost(state.post)
                is PostStateUi.Comment -> loadData()
            }
        }
    }

    private fun setupRecycler() {
        val recycler = binding.rcCommentsPost
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostCommentsAdapter(object : PostCommentListeners {}, viewHoldersManager)
        recycler.adapter = adapter
    }

    private fun handlePost(post: PostResponse) {
        list.add(0, post)
        isPostLoaded = true
        handleCommentsAndPost()
    }

    private fun handleComments(comments: List<CommentResponse>) {
        list.addAll(comments)
        isCommentsLoaded = true
        handleCommentsAndPost()
    }


    private fun handleCommentsAndPost() {
        if(isCommentsLoaded && isPostLoaded) adapter.submitList(list.toList())
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).hideBottomMenu()
        (activity as MainActivity).showUpButton()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomMenu()
        (activity as MainActivity).hideUpButton()
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

}



