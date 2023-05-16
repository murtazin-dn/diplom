package com.example.diplom.presentation.ui.post.adapter

class ViewHoldersManagerImpl : ViewHoldersManager {

    private val holdersMap = emptyMap<Int, ViewHolderVisitor>().toMutableMap()

    init {
        registerViewHolder(PostCommentItemTypes.POST, PostViewHolder())
        registerViewHolder(PostCommentItemTypes.COMMENT, CommentViewHolder())
    }

    override fun registerViewHolder(itemType: Int, viewHolder: ViewHolderVisitor) {
        holdersMap += itemType to viewHolder
    }

    override fun getItemType(item: Any): Int {
        holdersMap.forEach { (itemType, holder) ->
            if(holder.acceptBinding(item)) return itemType
        }
        return PostCommentItemTypes.UNKNOWN
    }

    override fun getViewHolder(itemType: Int) = holdersMap[itemType] ?: throw TypeCastException("Unknown recycler item type!")
}