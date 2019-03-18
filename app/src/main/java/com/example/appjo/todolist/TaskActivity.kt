package com.example.appjo.todolist

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.View
import android.widget.*
import com.example.appjo.todolist.Adapters.CommentsAdapter
import com.example.appjo.todolist.Models.TodoItem
import com.example.appjo.todolist.Receivers.AlarmReceiver
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.random.Random

class TaskActivity: AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    private var saveDataButton: Button? = null
    private var completeStatusSwitch: SwitchCompat? = null
    private var isComplete: Boolean = false
    private var isDailyReminder: Boolean = false
    private var titleEditText: EditText? = null
    private var addCommentEditText: EditText? = null
    private var addCommentButton: Button? = null
    private var emptyCommentsView: TextView? = null
    private var dailyReminderStatusSwitch: SwitchCompat? = null
    private var commentRecyclerView: RecyclerView? = null
    private var commentsList: MutableList<String> = mutableListOf()
    private var commentsAdapter: CommentsAdapter? = null
    private var objectId: String = ""
    private var title: String = ""
    private var pendingIntentRequestCode: Int = 0

    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this@TaskActivity)
        if (intent.extras != null){
            objectId = intent.getStringExtra("objectId")
            title = intent.getStringExtra("title")
            isComplete = intent.getBooleanExtra("done", false)
            commentsList = intent.getStringArrayListExtra("comments")
            pendingIntentRequestCode = intent.getIntExtra("requestCode", 0)
            isDailyReminder = sharedPref!!.getBoolean(objectId, false)
        }
        saveDataButton = findViewById(R.id.save_data_button)
        completeStatusSwitch = findViewById(R.id.complete_status_switch)
        dailyReminderStatusSwitch = findViewById(R.id.daily_reminder_status_switch)
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
        recyclerViewOrEmptyView()
        mDatabase = FirebaseDatabase.getInstance().reference
        setViews()
        setListeners()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    private fun toggleAlarmManager(title: String?){
        Log.v("TaskActivity", "Code: " + pendingIntentRequestCode)
        val alarmIntent = Intent(this@TaskActivity, AlarmReceiver::class.java)
        alarmIntent.putExtra("title", title)
        val alarmPendingIntent = PendingIntent.getBroadcast(this@TaskActivity, pendingIntentRequestCode, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (isDailyReminder){
            val calender = Calendar.getInstance()
            calender.timeInMillis = System.currentTimeMillis()
            calender.set(Calendar.HOUR_OF_DAY, 20)
            calender.set(Calendar.MINUTE, 22)
            calender.set(Calendar.SECOND, 1)
            if (calender.before(Calendar.getInstance())) {
                calender.add(Calendar.DATE, 1);
            }
            if (alarmManager != null){
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmPendingIntent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis(), alarmPendingIntent);
                }
            }
        }else{
            if (PendingIntent.getBroadcast(this@TaskActivity, pendingIntentRequestCode, alarmIntent, 0) != null && alarmManager != null){
                alarmManager.cancel(alarmPendingIntent)
            }
        }

    }

    private fun recyclerViewOrEmptyView(){
        if (commentsList.isNullOrEmpty()){
            showEmptyText()
        }else{
            showRecyclerView()
        }
    }

    private fun setViews(){
        completeStatusSwitch!!.setChecked(isComplete)
        dailyReminderStatusSwitch!!.setChecked(isDailyReminder)
        if (!title.isNullOrEmpty()){
            titleEditText!!.setText(title)
        }
        recyclerViewOrEmptyView()
    }

    private fun showEmptyText(){
        commentRecyclerView!!.visibility = View.GONE
        emptyCommentsView!!.visibility = View.VISIBLE
    }

    private fun randomizeRequestCode(): Int{
        val random = Random(System.currentTimeMillis())
        return 10000 + random.nextInt(20000)
    }

    private fun showRecyclerView(){
        emptyCommentsView!!.visibility = View.GONE
        commentRecyclerView!!.visibility = View.VISIBLE
    }

    private fun saveInSharedPref(taskKey: String?){
        val editor = sharedPref!!.edit()
        editor.putBoolean(taskKey, isDailyReminder)
        editor.apply()
    }

    private fun setListeners(){
        completeStatusSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                isComplete = true
            }else{
                isComplete = false
            }
        }

        dailyReminderStatusSwitch!!.setOnCheckedChangeListener {  buttonView, isChecked ->
            if (isChecked){
                isDailyReminder = true
            }else{
                isDailyReminder = false
            }
        }

        addCommentButton!!.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                if (!addCommentEditText!!.text.toString().isNullOrEmpty()){
                    commentsList.add(addCommentEditText!!.text.toString())
                    addCommentEditText!!.text.clear()
                    commentsAdapter!!.setCommentList(commentsList)
                    recyclerViewOrEmptyView()
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
                    val textData = titleEditText!!.text.toString()
                    todoItem.title = textData
                    todoItem.done = isComplete
                    if (isDailyReminder){
                        pendingIntentRequestCode = randomizeRequestCode()
                        todoItem.requestCode = pendingIntentRequestCode
                    }
                    if (!commentsList.isNullOrEmpty()){
                        todoItem.comments = commentsList
                    }
                    saveInSharedPref(newItem!!.key)
                    toggleAlarmManager(textData)
                    newItem!!.setValue(todoItem)
                    titleEditText!!.text.clear()
                    commentsList.clear()
//                    pendingIntentRequestCode = randomizeRequestCode()
                    commentsAdapter!!.setCommentList(commentsList)
                    Toast.makeText(this@TaskActivity, "Task uploaded", Toast.LENGTH_LONG).show()
                    finish()
                }else if (!objectId.isNullOrEmpty()){
                    val itemReference = mDatabase
                    if (!titleEditText!!.text.toString().isNullOrEmpty()){
                        val textData = titleEditText!!.text.toString()
                        todoItem.title = textData
                        todoItem.objectId = objectId
                        todoItem.done = isComplete
                        if (!commentsList.isNullOrEmpty()){
                            todoItem.comments = commentsList
                        }
                        if (pendingIntentRequestCode == 0){
                            pendingIntentRequestCode = randomizeRequestCode()
                        }
                        todoItem.requestCode = pendingIntentRequestCode
                        saveInSharedPref(objectId)
                        toggleAlarmManager(textData)
                        val map = mutableMapOf<String, Any>()
                        map[objectId] = todoItem
                        itemReference.updateChildren(map)
                        Toast.makeText(this@TaskActivity, "Task successfully updated", Toast.LENGTH_LONG).show()
                        finish()
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