package com.example.appjo.todolist.Models

data class Task(
    var comments: List<String>? = null,
    var done: Boolean = false,
    var objectId: String = "",
    var title: String = "")