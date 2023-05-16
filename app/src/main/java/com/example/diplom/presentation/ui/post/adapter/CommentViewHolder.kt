package com.example.diplom.presentation.ui.post.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.CommentResponse
import com.example.diplom.databinding.ItemCommentBinding
import com.example.diplom.presentation.common.toDateString
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.post.PostCommentListeners
import com.example.diplom.util.BASE_URL

class CommentViewHolder : ViewHolderVisitor {
    override val layout: Int = R.layout.item_comment

    override fun acceptBinding(item: Any): Boolean = item is CommentResponse

    override fun bind(binding: ViewDataBinding, item: Any, listeners: PostCommentListeners) {
        val context = binding.root.context
        binding as ItemCommentBinding
        item as CommentResponse

        binding.tvTextCommentItem.text = item.text
        binding.tvTimePublicationCommentItem.text = item.date.toDateString()
        binding.tvUserNameCommentItem.text = "${item.user.name} ${item.user.surname}"
        binding.imageVerificateUserCommentItem.visibility =
            if(item.user.doctorStatus) View.VISIBLE else View.GONE

        if (!item.user.icon.isNullOrEmpty()) {
            Glide
                .with(context)
                .load(item.user.icon.toPhotoURL())
                .into(binding.iconPerson)
        }

    }
}