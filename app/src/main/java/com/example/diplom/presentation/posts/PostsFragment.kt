package com.example.diplom.presentation.posts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.FragmentPostsBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.myprofile.PostAdapter
import com.example.diplom.presentation.myprofile.PostClickListener
import com.example.diplom.util.PROFILE_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostsFragment : Fragment() {

    private var _binding: FragmentPostsBinding? = null
    private val binding: FragmentPostsBinding get() = _binding!!

    private val viewModel by viewModel<PostsViewModel>()

    private var screenMode: String? = null
    private var userId: Long? = null

    private lateinit var adapter: PostAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()

    }

    fun getData(){
        when(arguments?.getString(SCREEN_MODE)){
            MODE_ALL_POSTS -> viewModel.getAllPosts()
            MODE_USER_POSTS -> {
                val userId = arguments?.getLong(USER_ID)
                if(userId != 0L){
                    viewModel.getPostsByUserId(userId!!)
                }
            }
            MODE_MY_SUBSCRIPTIONS -> viewModel.getMySubscriptionsPosts()
            else -> throw RuntimeException("unknown screen mode")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recycler = binding.rcPosts
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(object : PostClickListener {
            override fun setLike(post: PostResponse) {
                viewModel.setLike(post.id)
            }
            override fun unsetLike(post: PostResponse) {
                viewModel.unsetLike(post.id)
            }

            override fun profileClick(post: PostResponse) {
                (activity as MainActivity).newsNavigation?.navigateToProfile(post.user.id)
            }

            override fun postTextClick(post: PostResponse) {
                (activity as MainActivity).newsNavigation?.navigateToPost(post.id)
            }

        })
        recycler.adapter = adapter
        setObservers()
    }

    private fun setObservers() {
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is PostsStateUI.Error -> requireContext().showToast(state.error)
                is PostsStateUI.Posts -> adapter.submitList(state.posts)
                is PostsStateUI.SetLike -> Unit
                is PostsStateUI.UnsetLike -> Unit
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val SCREEN_MODE = "screen_mode"
        const val MODE_MY_SUBSCRIPTIONS = "my_subscriptions"
        const val MODE_ALL_POSTS = "all_posts"
        const val MODE_USER_POSTS = "user_posts"
        const val USER_ID = "user_id"
    }

}