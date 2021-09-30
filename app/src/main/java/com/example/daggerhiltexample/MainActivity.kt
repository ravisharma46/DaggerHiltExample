package com.example.daggerhiltexample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import com.example.daggerhiltexample.presentation.component.MovieCard
import com.example.daggerhiltexample.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            composeView()
        }


    }

    @Composable
    private fun composeView() {
        val movieList = mainViewModel.movieList.value

        LazyColumn {
            itemsIndexed(
                items = movieList!!
            ) { index, movie ->
                MovieCard(
                    moviePojo = movie!!,
                    onClick = {},
                    this@MainActivity
                )
            }
        }

    }


}