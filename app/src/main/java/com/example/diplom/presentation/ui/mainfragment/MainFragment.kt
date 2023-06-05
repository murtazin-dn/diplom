package com.example.diplom.presentation.ui.mainfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentMainBinding
import com.example.diplom.presentation.GetUnreadMessagesCount
import com.example.diplom.presentation.HideBottomNavMenu
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.ShowBottomNavMenu
import com.example.diplom.presentation.common.showToast
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUnreadDialogsCount()
        (activity as MainActivity).hideBottomNavMenu = HideBottomNavMenu{
            binding.navView.visibility = View.GONE
        }
        (activity as MainActivity).showBottomNavMenu = ShowBottomNavMenu{
            binding.navView.visibility = View.VISIBLE
        }
        MainActivity.getUnreadMessagesCount = GetUnreadMessagesCount{
            viewModel.getUnreadDialogsCount()
        }
    }

    override fun onPause() {
        super.onPause()
        MainActivity.getUnreadMessagesCount = null
        (activity as MainActivity).hideBottomNavMenu = null
        (activity as MainActivity).showBottomNavMenu = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner){ state ->
            when(state){
                is MainFragmentStateUi.Error -> requireActivity().showToast(state.error)
                is MainFragmentStateUi.UnreadMessagesCount -> {
                    val badge = binding.navView.getOrCreateBadge(R.id.navigation_chats)
                    if(state.count != 0L){
                        badge.isVisible = true
                        badge.number = state.count.toInt()
                    }else{
                        badge.isVisible = false
                    }
                }
            }
        }

        val navView: BottomNavigationView = binding.navView

        val navController =
            (childFragmentManager.findFragmentById(R.id.nav_host_fragment_fragment_main) as NavHostFragment)
                .navController
        navView.setupWithNavController(navController)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}