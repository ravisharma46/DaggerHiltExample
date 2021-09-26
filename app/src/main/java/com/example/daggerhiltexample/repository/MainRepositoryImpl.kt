package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.MovieList

class MainRepositoryImpl(private val mainHttpInterface: MainHttpInterface) : MainRepository {
    override suspend fun getMoviewList(pageNo: Int): MovieList? {
       return mainHttpInterface.getMovieList(pageNo)
    }


}