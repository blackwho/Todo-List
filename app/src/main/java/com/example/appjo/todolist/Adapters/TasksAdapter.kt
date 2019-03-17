package com.example.appjo.todolist.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.appjo.todolist.Models.Task
import com.example.appjo.todolist.R
import com.example.appjo.todolist.TaskActivity

class TasksAdapter(var mContext: Context?, var tasksList: List<Task>): RecyclerView.Adapter<TasksAdapter.TaskViewHolder>(){

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            holder.taskTitle!!.text = tasksList[position].title
            if (!tasksList[position].done){
                holder.completeStatusImageView!!.setImageResource(R.drawable.ic_incomplete_circle)
            }else{
                holder.completeStatusImageView!!.setImageResource(R.drawable.ic_comment_circle)
            }
            holder.cardView!!.setOnClickListener {
                val intent = Intent(mContext, TaskActivity::class.java)
                intent.putExtra("objectId", tasksList[position].objectId)
                intent.putExtra("title", tasksList[position].title)
                intent.putExtra("done", tasksList[position].done)
                val arrayCommentsList: ArrayList<String> = ArrayList()
                try {
                    for (i in 0 until tasksList[position].comments!!.size){
                        arrayCommentsList.add(tasksList[position].comments!!.get(i))
                    }
                }catch (e: KotlinNullPointerException){
                    e.printStackTrace()
                }

                intent.putStringArrayListExtra("comments", arrayCommentsList)
                mContext!!.startActivity(intent)
            }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.task_item, parent, false))
    }

    fun setTaskList(data: List<Task>){
        if (data != null){
            tasksList = data
            notifyDataSetChanged()
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var taskTitle: TextView? = null
        var cardView: CardView? = null
        var completeStatusImageView: ImageView? = null
        init {
            cardView = itemView.findViewById(R.id.task_item_card_view) as CardView
            taskTitle = itemView.findViewById(R.id.comment_item_tv) as TextView
            completeStatusImageView = itemView.findViewById(R.id.ic_status_circle_iv) as ImageView
        }
    }
}