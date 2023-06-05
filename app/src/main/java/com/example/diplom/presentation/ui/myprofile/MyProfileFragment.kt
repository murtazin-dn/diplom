package com.example.diplom.presentation.ui.myprofile

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.FragmentMyProfileBinding
import com.example.diplom.databinding.ItemPostBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.common.toDateString
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.common.toTimeString
import com.example.diplom.util.BASE_URL
import com.example.diplom.util.POST_ID
import com.example.diplom.util.PROFILE_ID
import com.example.diplom.util.USER_ID
import org.koin.androidx.viewmodel.ext.android.viewModel


class MyProfileFragment : Fragment() {

    private var profileId: Long? = null

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PostAdapter
    private var _posts = mutableListOf<PostResponse>()

    private val viewModel by viewModel<MyProfileViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_my_profile)
        viewModel.getMyProfileInfo()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }

            }
        )
        showShimmer()
        launchMyProfileMode()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MyPersonInfoStateUI.Error -> requireContext().showToast(state.error)
                is MyPersonInfoStateUI.MyProfileInfo -> handleMyProfileInfo(state.profileInfo)
                is MyPersonInfoStateUI.Posts -> handlePosts(state.postResponse)
                is MyPersonInfoStateUI.SetLike -> handleSetLike(state.postId)
                is MyPersonInfoStateUI.UnsetLike -> handleUnsetLike(state.postId)
                is MyPersonInfoStateUI.ProfileInfo -> Unit
                is MyPersonInfoStateUI.Subscribe -> Unit
                is MyPersonInfoStateUI.Unsubscribe -> Unit
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

            override fun profileClick(post: PostResponse) {}

            override fun postTextClick(post: PostResponse) {}

        })
        recycler.adapter = adapter
    }



    private fun launchMyProfileMode() {
        setMenuToolbar()
        binding.btnNewPost.visibility = View.VISIBLE
        binding.btnNewPost.setOnClickListener {
            findNavController().navigate(R.id.action_my_profile_fragment_to_createPostFragment)
        }
    }

    private fun setMenuToolbar() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.my_profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        navigateToSettingsFragment()
                        true
                    }
                    R.id.edit_profile -> {
                        navigateToEditProfileFragment()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun navigateToEditProfileFragment() {
        findNavController().navigate(R.id.action_my_profile_fragment_to_editPersonInfoFragment)
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

    private fun navigateToSettingsFragment() {
        findNavController().navigate(R.id.action_my_profile_fragment_to_settingsFragment)
    }


    private fun handleMyProfileInfo(personInfo: MyProfileResponse) = with(binding) {
        viewModel.getPosts(personInfo.id)
        tvNameMyPersonInfo.text = "${personInfo.name} ${personInfo.surname}"
        tvCategoryMyPersonInfo.text = personInfo.category.name
        imageVerificateStatus.visibility = if (personInfo.doctorStatus) View.VISIBLE else View.GONE
        if (!personInfo.icon.isNullOrEmpty()) {
            Glide
                .with(requireContext())
                .load(personInfo.icon.toPhotoURL())
                .into(iconUserMyPersonInfo)
        } else {
            Glide
                .with(requireContext())
                .load(R.drawable.no_photo)
                .into(iconUserMyPersonInfo)
        }
        println("dsfffffffffff")
        hideShimmer()
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

class PostAdapter(private val actions: PostClickListener) :
    ListAdapter<PostResponse, PostAdapter.ViewHolder>(PostDiffCallBack()) {

    class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder) {
            binding.tvPostCategoryPostItem.text = item.category.name
            binding.tvLikeCountPostItem.text = item.likesCount.toString()
            binding.tvCommentCountPostItem.text = item.commentsCount.toString()
            binding.tvUserNamePostItem.text = "${item.user.name} ${item.user.surname}"
            binding.tvTimePublicationPostItem.text = "${item.timeAtCreation.toDateString()} в ${item.timeAtCreation.toTimeString()}"
            binding.imageVerificateUserPostItem.visibility =
                if (item.user.doctorStatus) View.VISIBLE else View.GONE
            binding.tvPostTitlePostItem.text = item.title
            binding.tvPostTextPostItem.text = item.text
            if (item.isLikeEnabled) {
                binding.tvLikeCountPostItem.setTextColor(itemView.context.getColor(R.color.color_disabled))
                binding.icLikePostItem.setImageResource(R.drawable.ic_heart_enabled)
                binding.btnLikePostItem.setCardBackgroundColor(
                    itemView.context.getColor(R.color.color_enabled_background)
                )
                binding.btnLikePostItem.setOnClickListener {
                    item.isLikeEnabled = false
                    item.likesCount -= 1L
                    notifyItemChanged(position)
                    actions.unsetLike(item)
                }
            } else {
                binding.tvLikeCountPostItem.setTextColor(itemView.context.getColor(R.color.color_disabled))
                binding.icLikePostItem.setImageResource(R.drawable.ic_heart_disabled)
                binding.btnLikePostItem.setCardBackgroundColor(
                    itemView.context.getColor(R.color.color_disabled_background)
                )
                binding.btnLikePostItem.setOnClickListener {
                    item.isLikeEnabled = true
                    item.likesCount += 1L
                    notifyItemChanged(position)
                    actions.setLike(item)
                }
            }
            binding.clProfilePostItem.setOnClickListener {
                actions.profileClick(item)
            }
            binding.llPostText.setOnClickListener {
                actions.postTextClick(item)
            }
            if (!item.user.icon.isNullOrEmpty()) {
                Glide
                    .with(holder.itemView.context)
                    .load(item.user.icon.toPhotoURL())
                    .into(binding.iconPerson)
            } else {
                Glide
                    .with(holder.itemView.context)
                    .load(R.drawable.no_photo)
                    .into(binding.iconPerson)
            }
            val list = listOf(
                binding.imgPostImage1,
                binding.imgPostImage2,
                binding.imgPostImage3,
                binding.imgPostImage4,
                binding.imgPostImage5,
                binding.imgPostImage6,
                binding.imgPostImage7,
                binding.imgPostImage8,
                binding.imgPostImage9
            )

            for(i in 0 until item.images.size){
                list[i].visibility = View.VISIBLE
                Glide
                    .with(holder.itemView.context)
                    .load(item.images[i].toPhotoURL())
                    .into(list[i])
                if (i == 8) break
            }
            println("bind view holder")
        }
    }

    class PostDiffCallBack : DiffUtil.ItemCallback<PostResponse>() {
        override fun areItemsTheSame(oldItem: PostResponse, newItem: PostResponse): Boolean {
            println("areItemsTheSame $oldItem  $newItem")
            return oldItem.id == newItem.id;
        }

        override fun areContentsTheSame(oldItem: PostResponse, newItem: PostResponse): Boolean {
            println("areContentsTheSame $oldItem  $newItem")
            return oldItem == newItem
        }
    }
}

interface PostClickListener {
    fun setLike(post: PostResponse)
    fun unsetLike(post: PostResponse)
    fun profileClick(post: PostResponse)
    fun postTextClick(post: PostResponse)
}

enum class ScreenMode { MY_PROFILE, NOT_MY_PROFILE }
