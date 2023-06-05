package com.example.diplom.presentation.ui.photoview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.diplom.R
import com.example.diplom.databinding.FragmentPhotoViewPagerBinding
import com.example.diplom.presentation.MainActivity


class PhotoViewPagerFragment : Fragment() {

    private var _binding: FragmentPhotoViewPagerBinding? = null
    private val binding: FragmentPhotoViewPagerBinding get() = _binding!!

    private lateinit var adapter: PhotoViewAdapter

    private lateinit var photos: List<String>
    private var currentPhotoIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photos = it.getStringArrayList(PHOTOS)?.toList() ?: throw RuntimeException("")
            currentPhotoIndex =
                if(it.containsKey(PHOTO_INDEX)) it.getInt(PHOTO_INDEX) else throw RuntimeException("")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPhotoViewPagerBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setMenuToolbar()
        setOnBackPressed()
        adapter = PhotoViewAdapter(photos)
        binding.photosViewPager.adapter = adapter
        binding.photosViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                (activity as AppCompatActivity).supportActionBar?.setTitle("${position+1} из ${photos.size}")
            }
        })
        binding.photosViewPager.currentItem = currentPhotoIndex
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setTitle("${currentPhotoIndex+1} из ${photos.size}")
        (activity as MainActivity).hideBottomMenu()
        (activity as MainActivity).showUpButton()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).showBottomMenu()
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

    companion object{
        const val PHOTOS = "photos"
        const val PHOTO_INDEX = "photo_index"
    }
}