package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.MovieList
import retrofit2.http.GET
import retrofit2.http.Query

interface MainHttpInterface {
    @GET("/?s=comedy&apikey=cd1f7521")
    suspend fun getMovieList(@Query("page") pageNo: Int): MovieList?
}