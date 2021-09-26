package com.example.daggerhiltexample

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.databinding.DataBindingUtil
import androidx.ui.tooling.preview.Preview
import com.example.daggerhiltexample.databinding.ActivityMainBinding
import com.example.daggerhiltexample.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        
        mainViewModel.getImageList()
        binding.hello.setOnClickListener {
            mainViewModel.getImageList()
        }




    }

    private fun onClick(){

    }

}