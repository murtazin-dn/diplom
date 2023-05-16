package com.example.diplom.presentation.ui.post.adapter

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.example.diplom.R
import com.example.diplom.data.network.posts.model.response.PostResponse
import com.example.diplom.databinding.ItemPostBinding
import com.example.diplom.presentation.common.toDateString
import com.example.diplom.presentation.common.toPhotoURL
import com.example.diplom.presentation.common.toTimeString
import com.example.diplom.presentation.post.PostCommentListeners
import com.example.diplom.util.BASE_URL

class PostViewHolder : ViewHolderVisitor{
    override val layout: Int = R.layout.item_post

    override fun acceptBinding(item: Any): Boolean = item is PostResponse

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(binding: ViewDataBinding, item: Any, listeners: PostCommentListeners) {
        val context = binding.root.context
        binding as ItemPostBinding
        item as PostResponse
        binding.tvPostCategoryPostItem.text = item.category.name
        binding.tvLikeCountPostItem.text = item.likesCount.toString()
        binding.tvCommentCountPostItem.text = item.commentsCount.toString()
        binding.tvTimePublicationPostItem.text = "${item.timeAtCreation.toDateString()} в ${item.timeAtCreation.toTimeString()}"
        binding.tvUserNamePostItem.text = "${item.user.name} ${item.user.surname}"
        binding.imageVerificateUserPostItem.visibility =
            if(item.user.doctorStatus) View.VISIBLE else View.GONE
        binding.tvPostTitlePostItem.text = item.title
        binding.tvPostTextPostItem.text = item.text
        if(item.isLikeEnabled){
            binding.tvLikeCountPostItem.setTextColor(context.getColor(R.color.color_disabled))
            binding.icLikePostItem.setImageResource(R.drawable.ic_heart_enabled)
            binding.btnLikePostItem.setCardBackgroundColor(
                context.getColor(R.color.color_enabled_background))
//            binding.btnLikePostItem.setOnClickListener { actions.unsetLike(item) }
        }else {
            binding.tvLikeCountPostItem.setTextColor(context.getColor(R.color.color_disabled))
            binding.icLikePostItem.setImageResource(R.drawable.ic_heart_disabled)
            binding.btnLikePostItem.setCardBackgroundColor(
                context.getColor(R.color.color_disabled_background))
//            binding.btnLikePostItem.setOnClickListener { actions.setLike(item) }
        }
        binding.clProfilePostItem.setOnClickListener{
//            actions.profileClick(item)
        }
        if (!item.user.icon.isNullOrEmpty()) {
            Glide
                .with(context)
                .load(item.user.icon.toPhotoURL())
                .into(binding.iconPerson)
        }else{
            Glide
                .with(context)
                .load(R.drawable.no_photo)
                .into(binding.iconPerson)
        }


    }
}