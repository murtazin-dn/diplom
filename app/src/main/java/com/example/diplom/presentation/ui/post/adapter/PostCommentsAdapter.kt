package com.example.diplom.presentation.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.data.network.util.PostCommentItem
import com.example.diplom.presentation.ui.myprofile.PostClickListener
import com.example.diplom.presentation.ui.post.adapter.ViewHolderVisitor
import com.example.diplom.presentation.ui.post.adapter.ViewHoldersManager

class PostCommentsAdapter(
    private val listeners: PostCommentListeners,
    private val viewHoldersManager: ViewHoldersManager
    ) :
    ListAdapter<PostCommentItem, PostCommentsAdapter.DataViewHolder>(PostCommentDiffCallBack()) {

    inner class DataViewHolder(
        private val binding: ViewDataBinding,
        private val holder: ViewHolderVisitor
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PostCommentItem, listeners: PostCommentListeners) =
            holder.bind(binding, item, listeners)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        LayoutInflater.from(parent.context).run {
            val holder = viewHoldersManager.getViewHolder(viewType)
            DataViewHolder(DataBindingUtil.inflate(this, holder.layout, parent, false), holder)
        }


    override fun onBindViewHolder(holder: DataViewHolder, position: Int) = holder.bind(getItem(position), listeners)

    override fun getItemViewType(position: Int): Int  = viewHoldersManager.getItemType(getItem(position))


    class PostCommentDiffCallBack : DiffUtil.ItemCallback<PostCommentItem>() {
        override fun areItemsTheSame(oldItem: PostCommentItem, newItem: PostCommentItem): Boolean {
            return when {
                oldItem is PostResponse && newItem is PostResponse -> true
                oldItem is CommentResponse && newItem is CommentResponse -> {
                    oldItem.id == newItem.id
                }
                else -> false
            }
        }

        override fun areContentsTheSame(
            oldItem: PostCommentItem,
            newItem: PostCommentItem
        ): Boolean {
            return when {
                oldItem is PostResponse && newItem is PostResponse -> {
                    oldItem == newItem
                }
                oldItem is CommentResponse && newItem is CommentResponse -> {
                    oldItem == newItem
                }
                else -> false
            }
        }

    }
}

interface PostCommentListeners {
}
