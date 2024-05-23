package com.example.books.model

import java.util.UUID

class Publisher {

    var id: UUID private set
    var name: String = ""
    var address: String = ""
    var site: String = ""

    init {
        id = UUID.randomUUID()
    }
}


