package com.example.diplom.presentation.ui.home

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.diplom.presentation.ui.posts.PostsFragment


class NewsTabAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment = when(position){
        0 -> {
            val fragment = PostsFragment()
            fragment.arguments = bundleOf(PostsFragment.SCREEN_MODE to PostsFragment.MODE_MY_SUBSCRIPTIONS)
            fragment
        }

        else -> {
            val fragment = PostsFragment()
            fragment.arguments = bundleOf(PostsFragment.SCREEN_MODE to PostsFragment.MODE_ALL_POSTS)
            fragment
        }
    }
}