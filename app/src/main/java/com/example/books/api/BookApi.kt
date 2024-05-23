package com.example.books.api

import com.example.books.model.Book
import com.example.books.model.UserToken
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface BookApi {

    @GET("api/books")
    fun getBooks(): Deferred<Response<List<Book>>>

    @GET("api/books/{bookId}")
    fun getBookById(@Path("bookId") bookId: UUID) : Deferred<Response<Book>>

    @POST("api/books")
    fun postBook(@Body book: Book) : Deferred<Response<Book>>

    @PUT("api/books")
    fun putBook(@Body book: Book) : Deferred<Response<Book>>

    @DELETE("api/books")
    fun deleteBook(@Body book: Book) : Deferred<Response<Book>>

    @DELETE("api/books/{bookId}")
    fun deleteBookById(@Path("bookId") bookId: UUID) : Deferred<Response<Book>>

}