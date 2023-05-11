package com.example.diplom.data.network.posts.model.response

import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.PostCommentItem

data class CommentResponse(
    val date: Long,
    val id: Int,
    val postId: Int,
    val text: String,
    val user: MyProfileResponse
): PostCommentItem