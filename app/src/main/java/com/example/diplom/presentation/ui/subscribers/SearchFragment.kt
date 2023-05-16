package com.example.diplom.presentation.ui.subscribers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.diplom.R
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.databinding.FragmentSubscribersBinding
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.SearchToolbar
import com.example.diplom.presentation.common.showToast
import com.example.diplom.util.PROFILE_ID
import com.example.diplom.util.USER_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSubscribersBinding? = null
    private val binding: FragmentSubscribersBinding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSubscribersBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = UsersAdapter(object : UserClickListener{
            override fun openProfile(profile: ProfileResponse) {
                if(viewModel.isMyUserId(profile.profile.id)){
//                    findNavController().navigate(R.id.action_subscribersFragment_to_navigation_profile)
                }else{
                    findNavController().navigate(R.id.action_subscribersFragment_to_navigation_profile_flow,
                        bundleOf(PROFILE_ID to profile.profile.id)
                    )
                }
            }

            override fun openChat(profile: ProfileResponse) {
                findNavController().navigate(R.id.action_subscribersFragment_to_chatFragment2,
                bundleOf(USER_ID to profile.profile.id)
                )
            }

        })
        binding.rcUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rcUsers.adapter = adapter
        (activity as MainActivity).searchToolbarImpl = SearchToolbar { text ->
            viewModel.getUsers(text)
        }
        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is SearchStateUI.Error -> requireContext().showToast(state.error)
                is SearchStateUI.Users -> adapter.submitList(state.users)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).showSearchToolbar()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideSearchToolbar()
    }

}