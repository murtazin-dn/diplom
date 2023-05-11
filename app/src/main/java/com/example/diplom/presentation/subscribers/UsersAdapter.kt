package com.example.diplom.presentation.subscribers

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.messages.model.response.MessageResponse
import com.example.diplom.data.network.messages.model.response.MessageType
import com.example.diplom.data.network.personinfo.model.response.ProfileResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.ItemMessageInBinding
import com.example.diplom.databinding.ItemMessageOutBinding
import com.example.diplom.databinding.ItemPostBinding
import com.example.diplom.databinding.ItemUserBinding
import com.example.diplom.presentation.common.toTimeString
import com.example.diplom.presentation.myprofile.PostAdapter
import com.example.diplom.util.BASE_URL

class UsersAdapter(private val actions: UserClickListener) :
    ListAdapter<ProfileResponse, UsersAdapter.ViewHolder>(ProfileDiffCallBack()) {

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        with(holder){
            binding.tvCategoryUserItem.text = item.profile.category.name
            binding.tvNameUserItem.text = "${item.profile.name} ${item.profile.surname}"
            binding.root.setOnClickListener{
                actions.openProfile(item)
            }
            binding.imgMessage.setOnClickListener{
                actions.openChat(item)
            }
            if (!item.profile.icon.isNullOrEmpty()) {
                val imgAddress = "${BASE_URL}users/photo/${item.profile.icon}"
                Glide
                    .with(holder.itemView.context)
                    .load(imgAddress)
                    .into(binding.imgProfileIcon)
            }else{
                Glide
                    .with(holder.itemView.context)
                    .load(R.drawable.no_photo)
                    .into(binding.imgProfileIcon)
            }
        }
    }

    class ProfileDiffCallBack : DiffUtil.ItemCallback<ProfileResponse>() {
        override fun areItemsTheSame(oldItem: ProfileResponse, newItem: ProfileResponse): Boolean {
            println("areItemsTheSame $oldItem  $newItem")
            return oldItem.profile.id == newItem.profile.id;
        }

        override fun areContentsTheSame(oldItem: ProfileResponse, newItem: ProfileResponse): Boolean {
            println("areContentsTheSame $oldItem  $newItem")
            return oldItem == newItem
        }
    }
}

interface UserClickListener {
    fun openProfile(profile: ProfileResponse)
    fun openChat(profile: ProfileResponse)
}