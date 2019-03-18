package com.example.appjo.todolist

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.appjo.todolist.Adapters.TasksAdapter
import com.example.appjo.todolist.Models.Task
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private var tasksRecyclerView: RecyclerView? = null
    private var addTaskFloatButton: FloatingActionButton? = null
    private var emptyTaskTextView: TextView? = null
    private val toDoItemList: MutableList<Task> = mutableListOf()
    private var itemListener: ValueEventListener? = null
    lateinit var tasksAdapter: TasksAdapter
    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDatabase = FirebaseDatabase.getInstance().reference
        addTaskFloatButton = findViewById(R.id.add_task_fab_button)
        emptyTaskTextView = findViewById(R.id.empty_task_text_view)
        tasksRecyclerView = findViewById(R.id.recyclerViewTasks)
        tasksRecyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        tasksAdapter = TasksAdapter(this@MainActivity, toDoItemList!!)
        tasksRecyclerView!!.layoutManager = layoutManager
        tasksRecyclerView!!.adapter = tasksAdapter
        setListenerVariables()
    }

    override fun onStart() {
        super.onStart()
        mDatabase.orderByKey().addListenerForSingleValueEvent(itemListener!!)
    }

    private fun setListenerVariables(){
        addTaskFloatButton!!.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, TaskActivity::class.java)
                startActivity(intent)
            }

        })
        itemListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toDoItemList.clear()
                dataSnapshot.children.mapNotNullTo(toDoItemList){
                    it.getValue<Task>(Task::class.java)
                }
                if (toDoItemList.size == 0){
                    emptyTaskTextView!!.visibility = View.VISIBLE
                }else{
                    emptyTaskTextView!!.visibility = View.GONE
                }
                tasksAdapter.setTaskList(toDoItemList)
            }

        }
    }

}
