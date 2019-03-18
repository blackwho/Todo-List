package com.example.appjo.todolist.Models

data class Task(
    var comments: List<String>? = null,
    var done: Boolean = false,
    var objectId: String = "",
    var requestCode: Int = 0,
    var title: String = "")