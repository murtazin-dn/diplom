package com.example.diplom.presentation.ui.prifile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.FragmentProfileBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.ui.myprofile.*
import com.example.diplom.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!

    private var profileId: Long? = null
    private lateinit var adapter: PostAdapter
    private var _posts = mutableListOf<PostResponse>()

    private val viewModel by viewModel<MyProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileId = arguments?.getLong(PROFILE_ID)
        when(profileId){
            null -> throw RuntimeException("Param PROFILE_ID is absent")
            0L -> throw RuntimeException("Param PROFILE_ID is absent")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showUpButton()
    }
    override fun onStop() {
        super.onStop()
        (activity as MainActivity).hideUpButton()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_my_profile)
        viewModel.getProfileInfo(profileId!!)
        setMenuToolbar()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }

            }
        )
        showShimmer()
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is MyPersonInfoStateUI.Error -> requireContext().showToast(state.error)
                is MyPersonInfoStateUI.MyProfileInfo -> Unit
                is MyPersonInfoStateUI.Posts -> handlePosts(state.postResponse)
                is MyPersonInfoStateUI.SetLike -> handleSetLike(state.postId)
                is MyPersonInfoStateUI.UnsetLike -> handleUnsetLike(state.postId)
                is MyPersonInfoStateUI.ProfileInfo -> handleProfileInfo(state.profileInfo)
                is MyPersonInfoStateUI.Subscribe -> setSubscribeButton(true)
                is MyPersonInfoStateUI.Unsubscribe -> setSubscribeButton(false)
            }
        }
        val recycler = binding.rcPostsMyProfile
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(object : PostClickListener {
            override fun setLike(post: PostResponse) {
                viewModel.setLike(post.id)
            }

            override fun unsetLike(post: PostResponse) {
                viewModel.unsetLike(post.id)
            }

            override fun profileClick(post: PostResponse) {

            }

            override fun postTextClick(post: PostResponse) {

            }

        })
        recycler.adapter = adapter

        binding.btnSendMessagePrifile.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_chatFragment3,
                bundleOf(USER_ID to profileId!!)
            )
        }


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
                        findNavController().popBackStack()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun handleUnsetLike(postId: Long) {
        val list = _posts.map {
            if (it.id == postId) {
                it.isLikeEnabled = false
                it.text = "fdkljgnkjdhkgjh"
                it
            } else it
        }.toMutableList()
        println(_posts)
        adapter.submitList(list.toMutableList())
    }

    private fun handleSetLike(postId: Long) {
        _posts = _posts.map {
            if (it.id == postId) {
                it.isLikeEnabled = true
                it.text = "fdkljgnkjdhkgjh"
                it.copy()
            } else it.copy()
        }.toMutableList()
        println(_posts)
        adapter.submitList(_posts.toMutableList())
    }

    private fun setSubscribeButton(isSubscribe: Boolean) {
        if (isSubscribe) {
            binding.btnSubscribeProfile.setText(R.string.unsubscribe)
            binding.btnSubscribeProfile.setOnClickListener { viewModel.unsubscribe(profileId!!) }
        } else {
            binding.btnSubscribeProfile.setText(R.string.subscribe)
            binding.btnSubscribeProfile.setOnClickListener { viewModel.subscribe(profileId!!) }
        }
    }

    private fun handleProfileInfo(personInfo: ProfileResponse) = with(binding) {
        viewModel.getPosts(personInfo.profile.id)
        tvNameMyPersonInfo.text = "${personInfo.profile.name} ${personInfo.profile.surname}"
        tvCategoryMyPersonInfo.text = personInfo.profile.category.name
        imageVerificateStatus.visibility = if (personInfo.profile.doctorStatus) View.VISIBLE else View.GONE
        if (!personInfo.profile.icon.isNullOrEmpty()) {
            Glide
                .with(requireContext())
                .load(personInfo.profile.icon.toPhotoURL())
                .into(iconUserMyPersonInfo)
        } else {
            Glide
                .with(requireContext())
                .load(R.drawable.no_photo)
                .into(iconUserMyPersonInfo)
        }
        println("dsfffffffffff")
        hideShimmer()
        setSubscribeButton(personInfo.isSubscribe)
    }

    private fun showShimmer() = with(binding) {
        viewShimmerBackground.visibility = View.VISIBLE
        shimmer.visibility = View.VISIBLE
        shimmer.startShimmer()
    }

    private fun hideShimmer() = with(binding) {
        viewShimmerBackground.visibility = View.GONE
        shimmer.visibility = View.GONE
        shimmer.stopShimmer()
    }

    private fun handlePosts(posts: List<PostResponse>) = with(binding) {
        _posts.clear()
        _posts.addAll(posts)
        adapter.submitList(posts.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}