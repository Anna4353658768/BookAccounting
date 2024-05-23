package com.example.books.controller

import android.content.Context
import com.example.books.model.Author
import com.example.books.model.Book
import com.example.books.model.Publisher
import java.util.UUID

class BookLab private constructor(context: Context) {

    var books = mutableListOf<Book>()

    companion object {
        private var INSTANCE: BookLab? = null

        fun get(context: Context): BookLab {
            if (INSTANCE == null){
                INSTANCE = BookLab(context)
            }
            return INSTANCE!!
        }
    }


//    init {
//
//        val author = Author()
//        author.surname = "Пушкин"
//        author.name = "Александр"
//        author.patronymic = "Сергеевич"
//
//        val publisher = Publisher()
//        publisher.name = "ACT"
//        publisher.site = "Новый сайт"
//        publisher.address = "Ростов-на-Дону"
//
//        val book = Book()
//        book.publisher = publisher
//        book.author = author
//        book.title = "Евгений Онегин"
//        book.essay = " Пронзительная любовная история, драматические повороты сюжета, тонкий психологизм персонажей, детальное описание быта и нравов той эпохи "
//        book.yearPublish = "2024"
//        book.code = "123-343-342-521-234"
//        book.hardcover = "Мягкий переплет"
//        book.countPage = 254
//        book.status = true
//
//        addBook(book)
//    }


    fun getBook(id: UUID): Book? {
        for (book in books) {
            if (book.id == id) {
                return book
            }
        }
        return null
    }


    fun addBook(book: Book){
        books.add(book)
    }


    fun deleteBook(book: Book){
        books.remove(book)
    }

}