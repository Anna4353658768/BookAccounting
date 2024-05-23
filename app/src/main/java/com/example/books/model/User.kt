package com.example.books.model

data class User(
    var username: String,
    var password: String,
    var email: String
)


data class UserLogin(
    var username: String,
    var password: String
)


data class UserToken(
    var token: String
)