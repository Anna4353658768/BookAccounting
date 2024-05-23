package com.example.books.api

import com.example.books.model.User
import com.example.books.model.UserLogin
import com.example.books.model.UserToken
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserApi {

    @POST("auth/sign-in")
    fun login(@Body book: UserLogin) : Deferred<Response<UserToken>>

    @POST("auth/sign-up")
    fun registration(@Body book: User) : Deferred<Response<UserToken>>

}