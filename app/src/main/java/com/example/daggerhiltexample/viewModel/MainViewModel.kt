package com.example.daggerhiltexample.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.daggerhiltexample.pojo.Resource
import com.example.daggerhiltexample.repository.MainRepository
import kotlinx.coroutines.Dispatchers


class MainViewModel
@ViewModelInject
constructor(private val mainRepository: MainRepository) : ViewModel() {

    fun getImageList() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = mainRepository.getMoviewList(1)
                )
            )

        } catch (exception: Exception) {
            emit(Resource.error(exception.message ?: "Error Occurred!", data = null))
        }
    }


}