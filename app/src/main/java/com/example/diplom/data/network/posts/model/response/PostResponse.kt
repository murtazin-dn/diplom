package com.example.diplom.data.network.posts.model.response

import com.example.diplom.data.network.categories.model.response.CategoryResponse
import com.example.diplom.data.network.personinfo.model.response.MyProfileResponse
import com.example.diplom.data.network.util.PostCommentItem

data class PostResponse(
    val id: Long,
    val user: MyProfileResponse,
    val title: String,
    var text: String,
    val category: CategoryResponse,
    val timeAtCreation: Long,
    var likesCount: Long,
    var commentsCount: Long,
    var isLikeEnabled: Boolean
): PostCommentItem
