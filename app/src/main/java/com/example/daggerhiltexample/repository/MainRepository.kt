package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.MovieList

interface MainRepository {
    suspend fun getMoviewList(pageNo: Int): MovieList?
}