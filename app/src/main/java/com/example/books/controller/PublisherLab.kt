package com.example.books.controller

import android.content.Context
import com.example.books.model.Publisher
import java.util.UUID

class PublisherLab private constructor(context: Context) {

    var publishers = mutableListOf<Publisher>()

    companion object {
        private var INSTANCE: PublisherLab? = null

        fun get(context: Context): PublisherLab {
            if (INSTANCE == null){
                INSTANCE = PublisherLab(context)
            }
            return INSTANCE!!
        }
    }

//    init {
//
//        val publisher = Publisher()
//        publisher.name = "AST"
//        publisher.site = "Новый сайт"
//        publisher.address = "Ростов-на-Дону"
//
//        addPublisher(publisher)
//
//        val publisher2 = Publisher()
//        publisher2.name = "Site"
//        publisher2.site = "Новый сайт"
//        publisher2.address = "Ростов-на-Дону"
//
//        addPublisher(publisher2)
//
//        val publisher3 = Publisher()
//        publisher3.name = "Piter"
//        publisher3.site = "Новый сайт"
//        publisher3.address = "Ростов-на-Дону"
//        addPublisher(publisher3)
//
//    }


    fun getPublisher(id: UUID): Publisher? {
        for (publisher in publishers) {
            if (publisher.id == id) {
                return publisher
            }
        }
        return null
    }


    fun addPublisher(publisher: Publisher){
        publishers.add(publisher)
    }


    fun deletePublisher(publisher: Publisher){
        publishers.remove(publisher)
    }

}