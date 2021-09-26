package com.example.daggerhiltexample.viewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.daggerhiltexample.pojo.MoviePojo
import com.example.daggerhiltexample.pojo.Resource
import com.example.daggerhiltexample.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel
@ViewModelInject
constructor(private val mainRepository: MainRepository) : ViewModel() {

    val movieList: MutableState<List<MoviePojo?>?> = mutableStateOf(ArrayList())


    init {
        getMovie()
    }

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

    fun getMovie() {
        viewModelScope.launch {
            val result = mainRepository.getMoviewList(1)
            movieList.value = result?.Search
        }
    }


}