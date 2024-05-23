package com.example.books.controller

import android.content.Context
import com.example.books.model.Author
import com.example.books.model.Publisher
import java.util.UUID

class AuthorLab private constructor(context: Context) {

    var authors = mutableListOf<Author>()

    companion object {
        private var INSTANCE: AuthorLab? = null

        fun get(context: Context): AuthorLab {
            if (INSTANCE == null){
                INSTANCE = AuthorLab(context)
            }
            return INSTANCE!!
        }
    }

//    init {
//
//        val author = Author()
//        author.surname = "Попова"
//        author.name = "Анна"
//        author.patronymic = "Евгеньевна"
//
//        addAuthor(author)
//
//        val author2 = Author()
//        author2.surname = "Ковалев"
//        author2.name = "Дмитрий"
//        author2.patronymic = "Владимирович"
//
//        addAuthor(author2)
//
//        val author3 = Author()
//        author3.surname = "Иванов"
//        author3.name = "Иван"
//        author3.patronymic = "Иванович"
//
//        addAuthor(author3)
//
//    }


    fun getAuthor(id: UUID): Author? {
        for (author in authors) {
            if (author.id == id) {
                return author
            }
        }
        return null
    }


    fun addAuthor(author: Author){
        authors.add(author)
    }


    fun deleteAuthor(author: Author){
        authors.remove(author)
    }


}