package com.example.daggerhiltexample.repository

import com.example.daggerhiltexample.pojo.ImagePojo

class MainRepositoryImpl(private val apiHttpInterface: ApiHttpInterface) : MainRepository {

    override suspend fun getImageList(): List<ImagePojo> {
        return apiHttpInterface.getImageList()
    }
}