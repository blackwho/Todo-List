package com.example.appjo.todolist.Models

class TodoItem {

    companion object Factory {
        fun create(): TodoItem = TodoItem()
    }

    var objectId: String? = null
    var title: String? = null
    var done: Boolean? = false
    var comments: List<String>? = null
    var requestCode: Int? = 0
}