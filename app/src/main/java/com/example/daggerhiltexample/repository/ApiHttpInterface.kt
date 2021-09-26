package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.ImagePojo
import retrofit2.http.GET

interface ApiHttpInterface {
    @GET("/imagesApi")
    fun getImageList(): List<ImagePojo>
}