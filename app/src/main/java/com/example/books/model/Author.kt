package com.example.books.model

import java.util.UUID

class Author{

    var id: UUID private set
    var surname: String = ""
    var name: String = ""
    var patronymic: String = ""

    init {
        id = UUID.randomUUID()
    }
}

