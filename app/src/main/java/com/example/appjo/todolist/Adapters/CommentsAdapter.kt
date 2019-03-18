package com.example.appjo.todolist.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.appjo.todolist.R
import org.w3c.dom.Text

class CommentsAdapter(mContext: Context?, var commentsList: List<String>): RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.comment_item, parent, false))
    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        if (!commentsList[position].isNullOrEmpty()){
                holder.commentTextView!!.text = commentsList[position]
            }
    }

    public fun setCommentList(data: List<String>){
        commentsList = data
        notifyDataSetChanged()

    }


    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var commentTextView: TextView? = null
        init {
            commentTextView = itemView.findViewById(R.id.comment_item_tv) as TextView
        }
    }
}