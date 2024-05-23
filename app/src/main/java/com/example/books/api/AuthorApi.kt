package com.example.books.api

import com.example.books.model.Author
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface AuthorApi {

    @GET("api/authors")
    fun getAuthors() : Deferred<Response<List<Author>>>

    @GET("api/authors/{authorId}")
    fun getAuthorById(@Path("authorId") authorId: UUID) : Deferred<Response<Author>>

    @POST("api/authors")
    fun postAuthor(@Body author: Author) : Deferred<Response<Author>>

    @PUT("api/authors")
    fun putAuthor(@Body author: Author) : Deferred<Response<Author>>

    @DELETE("api/authors")
    fun deleteAuthor(@Body author: Author) : Deferred<Response<Author>>

    @DELETE("api/authors/{authorId}")
    fun deleteAuthorById(@Path("authorId") authorId: UUID) : Deferred<Response<Author>>

}