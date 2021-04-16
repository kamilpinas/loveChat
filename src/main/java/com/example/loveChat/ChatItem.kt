package com.example.loveChat

class ChatItem {
    lateinit var content: String
    lateinit var login: String
    lateinit var date: String
    lateinit var id: String

    constructor(content: String, login: String, date: String, id: String) {
        this.content = content
        this.login = login
        this.date = date
        this.id = id
    }
    constructor()


}