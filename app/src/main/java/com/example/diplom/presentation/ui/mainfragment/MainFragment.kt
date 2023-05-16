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
import com.example.diplom.presentation.HideBottomNavMenu
import com.example.diplom.presentation.MainActivity
import com.example.diplom.presentation.ShowBottomNavMenu
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
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
        (activity as MainActivity).hideBottomNavMenu = HideBottomNavMenu{
            binding.navView.visibility = View.GONE
        }
        (activity as MainActivity).showBottomNavMenu = ShowBottomNavMenu{
            binding.navView.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).hideBottomNavMenu = null
        (activity as MainActivity).showBottomNavMenu = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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