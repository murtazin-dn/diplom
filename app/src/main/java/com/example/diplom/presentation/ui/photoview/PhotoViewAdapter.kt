package com.example.diplom.presentation.ui.photoview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.databinding.ItemPhotoBinding
import com.example.diplom.presentation.common.toPhotoURL

class PhotoViewAdapter(private val photos: List<String>): RecyclerView.Adapter<PhotoViewAdapter.PagerVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PagerVH, position: Int) {
        Glide
            .with(holder.itemView.context)
            .load(photos[position].toPhotoURL())
            .into(holder.binding.photoView)
    }

    class PagerVH(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)
}

