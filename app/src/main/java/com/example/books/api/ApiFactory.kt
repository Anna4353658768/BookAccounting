package com.example.books.api

import com.example.books.fragment.AuthorizationFragment
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private val interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = AuthorizationFragment.USER_TOKEN?.token

        val requestBuilder = originalRequest.newBuilder()
        if (token != null) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    private val gsonBuilder = GsonBuilder()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://c1ac-46-147-217-82.ngrok-free.app")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val bookApi: BookApi = retrofit.create(BookApi::class.java)
    val publisherApi: PublisherApi = retrofit.create(PublisherApi::class.java)
    val authorApi: AuthorApi = retrofit.create(AuthorApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)
}
