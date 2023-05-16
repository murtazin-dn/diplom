package com.example.diplom.presentation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.FragmentNewsBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.NewsNavigation
import com.example.diplom.presentation.common.showToast
import com.example.diplom.presentation.ui.myprofile.PostAdapter
import com.example.diplom.presentation.ui.myprofile.PostClickListener
import com.example.diplom.util.POST_ID
import com.example.diplom.util.PROFILE_ID
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<NewsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.title_home)
        binding.viewPager.adapter = NewsTabAdapter(requireActivity())
        TabLayoutMediator(binding.tbLayout, binding.viewPager) { tab, position ->
            when(position){
                0 -> tab.setText(R.string.title_news)
                1 -> tab.setText(R.string.title_actual)
            }
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).newsNavigation = object : NewsNavigation{
            override fun navigateToProfile(id: Long) {
                if(viewModel.isMyUserId(id)){
                    findNavController().navigate(R.id.action_news_fragment_to_navigation_profile)
                }else{
                    findNavController().navigate(
                        R.id.action_news_fragment_to_navigation_profile_flow,
                        bundleOf(PROFILE_ID to id)
                    )
                }
            }

            override fun navigateToPost(id: Long) {
                findNavController().navigate(R.id.action_news_fragment_to_postFragment2,
                bundleOf(POST_ID to id)
                )
            }

        }
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).newsNavigation = null
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}