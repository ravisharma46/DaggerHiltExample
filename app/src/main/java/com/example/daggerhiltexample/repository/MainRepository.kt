package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.ImagePojo

interface MainRepository {
    suspend fun getImageList(): List<ImagePojo>
}