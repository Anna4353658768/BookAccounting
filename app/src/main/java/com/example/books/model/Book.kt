package com.example.books.model

import java.util.UUID

class Book{

    var id: UUID private set

    var title: String = ""
    var code: String = ""
    var hardcover: String = ""
    var essay: String = ""
    var yearPublish: String? = ""
    var author: Author? = null
    var publisher: Publisher? = null
    var countPage: Long? = null
    var status: Boolean = false

    init {
        id = UUID.randomUUID()
    }

}
