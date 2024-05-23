package com.example.books.api

import com.example.books.model.Publisher
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.UUID

interface PublisherApi {

    @GET("api/publishers")
    fun getPublishers() : Deferred<Response<List<Publisher>>>

    @GET("api/publishers/{publisherId}")
    fun getPublisherById(@Path("publisherId") publisherId: UUID) : Deferred<Response<Publisher>>

    @POST("api/publishers")
    fun postPublisher(@Body publisher: Publisher) : Deferred<Response<Publisher>>

    @PUT("api/publishers")
    fun putPublisher(@Body publisher: Publisher) : Deferred<Response<Publisher>>

    @DELETE("api/publishers")
    fun deletePublisher(@Body publisher: Publisher) : Deferred<Response<Publisher>>

    @DELETE("api/publishers/{publisherId}")
    fun deletePublisherById(@Path("publisherId") publisherId: UUID) : Deferred<Response<Publisher>>

}