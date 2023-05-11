package com.example.diplom.presentation.post.adapter

import androidx.databinding.ViewDataBinding
import com.example.diplom.presentation.post.PostCommentListeners

interface ViewHolderVisitor {
    val layout: Int
    fun acceptBinding(item: Any): Boolean
    fun bind(binding: ViewDataBinding, item: Any, listeners: PostCommentListeners)
}