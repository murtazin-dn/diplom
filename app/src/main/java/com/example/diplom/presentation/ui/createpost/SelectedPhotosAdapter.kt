package com.example.diplom.presentation.ui.createpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.diplom.databinding.ItemSelectedImageErrorBinding
import com.example.diplom.databinding.ItemSelectedImageLoadedBinding
import com.example.diplom.databinding.ItemSelectedImageLoadingBinding
import com.example.diplom.presentation.ui.chats.ChatsClickListener

class SelectedPhotosAdapter(private val actions: SelectedPhotoActions) : ListAdapter<SelectedPhotoModel, RecyclerView.ViewHolder>(ChatDiffCallBack()) {

    companion object {
        const val VIEW_TYPE_LOADING = 1
        const val VIEW_TYPE_LOADED = 2
        const val VIEW_TYPE_ERROR = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).status){
            PhotoLoadedStatus.LOADING -> VIEW_TYPE_LOADING
            PhotoLoadedStatus.LOADED -> VIEW_TYPE_LOADED
            PhotoLoadedStatus.ERROR -> VIEW_TYPE_ERROR
        }
    }

    class ViewHolderLoading(val binding: ItemSelectedImageLoadingBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewHolderLoaded(val binding: ItemSelectedImageLoadedBinding) : RecyclerView.ViewHolder(binding.root)
    class ViewHolderError(val binding: ItemSelectedImageErrorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_LOADING -> ViewHolderLoading(
                ItemSelectedImageLoadingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_LOADED -> ViewHolderLoaded(
                ItemSelectedImageLoadedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_ERROR -> ViewHolderError(
                ItemSelectedImageErrorBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw RuntimeException("unknown item type in MessagesAdapter RecyclerView")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        println("bindPhoto")
        val item = getItem(position)
        when(holder){
            is ViewHolderLoaded -> {
                Glide
                    .with(holder.itemView.context)
                    .load(item.file)
                    .into(holder.binding.imageView3)
                holder.binding.btnDeleteImage.setOnClickListener{actions.deleteImage(item.id)}
            }
            is ViewHolderLoading -> {
                Glide
                    .with(holder.itemView.context)
                    .load(item.file)
                    .into(holder.binding.imageView3)
                holder.binding.btnDeleteImage.setOnClickListener{actions.deleteImage(item.id)}
            }
            is ViewHolderError -> {
                Glide
                    .with(holder.itemView.context)
                    .load(item.file)
                    .into(holder.binding.imageView3)
                holder.binding.btnDeleteImage.setOnClickListener{actions.deleteImage(item.id)}
                holder.binding.btnReloadImage.setOnClickListener{actions.reloadImage(item)}
            }
        }

    }

    class ChatDiffCallBack : DiffUtil.ItemCallback<SelectedPhotoModel>() {
        override fun areItemsTheSame(oldItem: SelectedPhotoModel, newItem: SelectedPhotoModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SelectedPhotoModel, newItem: SelectedPhotoModel): Boolean {
            return oldItem == newItem
        }
    }
}

interface SelectedPhotoActions{
    fun deleteImage(id: String)
    fun reloadImage(photo: SelectedPhotoModel)
}