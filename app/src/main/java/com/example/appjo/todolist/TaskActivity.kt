package com.example.appjo.todolist

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.appjo.todolist.Adapters.CommentsAdapter
import com.example.appjo.todolist.AppUtils.Constants
import com.example.appjo.todolist.Models.TodoItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity: AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var saveDataButton: Button? = null
    private var completeStatusSwitch: SwitchCompat? = null
    private var isComplete: Boolean = false
    private var titleEditText: EditText? = null
    private var addCommentEditText: EditText? = null
    private var addCommentButton: Button? = null
    private var emptyCommentsView: TextView? = null
    private var commentRecyclerView: RecyclerView? = null
    private var commentsList: MutableList<String> = mutableListOf()
    private var commentsAdapter: CommentsAdapter? = null
    private var objectId: String = ""
    private var title: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        if (intent != null){
            objectId = intent.getStringExtra("objectId")
            title = intent.getStringExtra("title")
            isComplete = intent.getBooleanExtra("done", false)
            commentsList = intent.getStringArrayListExtra("comments")
        }
        saveDataButton = findViewById(R.id.save_data_button)
        completeStatusSwitch = findViewById(R.id.complete_status_switch)
        titleEditText = findViewById(R.id.add_title_ev)
        addCommentEditText = findViewById(R.id.add_comment_ev)
        emptyCommentsView = findViewById(R.id.empty_comments_view)
        addCommentButton = findViewById(R.id.add_comment_button)
        commentRecyclerView = findViewById(R.id.comments_rv)
        commentRecyclerView!!.setHasFixedSize(true)
        commentsAdapter = CommentsAdapter(this@TaskActivity, commentsList)
        val layoutManager = LinearLayoutManager(this@TaskActivity, LinearLayoutManager.VERTICAL, false)
        commentRecyclerView!!.layoutManager = layoutManager
        commentRecyclerView!!.adapter = commentsAdapter
        mDatabase = FirebaseDatabase.getInstance().reference
        setViews()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun setViews(){
        completeStatusSwitch!!.setChecked(isComplete)
        if (!title.isNullOrEmpty()){
            titleEditText!!.setText(title)
        }
        if (commentsList.isNullOrEmpty()){
            showEmptyText()
        }else{
            showRecyclerView()
        }
    }

    private fun showEmptyText(){
        commentRecyclerView!!.visibility = View.GONE
        emptyCommentsView!!.visibility = View.VISIBLE
    }

    private fun showRecyclerView(){
        emptyCommentsView!!.visibility = View.GONE
        commentRecyclerView!!.visibility = View.VISIBLE
    }

    private fun setListeners(){
        completeStatusSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                isComplete = true
            }else{
                isComplete = false
            }
        }

        addCommentButton!!.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (!addCommentEditText!!.text.toString().isNullOrEmpty()){
                    commentsList.add(addCommentEditText!!.text.toString())
                    addCommentEditText!!.text.clear()
                    commentsAdapter!!.setCommentList(commentsList)
                }
            }
        })
        saveDataButton!!.setOnClickListener(object : View.OnClickListener{
            val todoItem = TodoItem.create()
            var newItem: DatabaseReference? = null

            override fun onClick(p0: View?) {
                if (!titleEditText!!.text.toString().isNullOrEmpty() && objectId.isNullOrEmpty()){
                    newItem = mDatabase.push()
                    todoItem.objectId = newItem!!.key
                    todoItem.title = titleEditText!!.text.toString()
                    todoItem.done = isComplete
                    if (!commentsList.isNullOrEmpty()){
                        todoItem.comments = commentsList
                    }
                    newItem!!.setValue(todoItem)
                    titleEditText!!.text.clear()
                    commentsList.clear()
                    Toast.makeText(this@TaskActivity, "Task uploaded", Toast.LENGTH_LONG).show()
                }else if (!objectId.isNullOrEmpty()){
                    val itemReference = mDatabase
                    if (!titleEditText!!.text.toString().isNullOrEmpty()){
                        todoItem.title = titleEditText!!.text.toString()
                        todoItem.objectId = objectId
                        todoItem.done = isComplete
                        if (!commentsList.isNullOrEmpty()){
                            todoItem.comments = commentsList
                        }
                        val map = mutableMapOf<String, Any>()
                        map[objectId] = todoItem
                        itemReference.updateChildren(map)
                    }else{
                        Toast.makeText(this@TaskActivity, "Please set a title", Toast.LENGTH_LONG).show()

                    }

                }else{
                    Toast.makeText(this@TaskActivity, "Please set a title", Toast.LENGTH_LONG).show()

                }
            }
        })
    }
}