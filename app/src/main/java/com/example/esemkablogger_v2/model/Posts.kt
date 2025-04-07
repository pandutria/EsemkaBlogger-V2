package com.example.esemkablogger_v2.model

data class Posts(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var thumbnail: String? = null,
    var imageContent: String? = null,
    var date: String? = null,
    var likeCount:Int? = null,
    var category: Category,
    var user: User
)
