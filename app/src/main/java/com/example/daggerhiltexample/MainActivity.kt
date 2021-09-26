package com.example.daggerhiltexample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Observer
import com.example.daggerhiltexample.pojo.Status
import com.example.daggerhiltexample.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Image(
                    ImageBitmap.imageResource(id = R.drawable.happy_meal),
                    contentDescription = "",
                    modifier = Modifier.height(300.dp),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Happy Meal",
                        style = TextStyle(
                            fontSize = 30.sp,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(text = "800 Calories")
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    Text(text = "$5.99")
                }

            }

        }


    }

    private fun callClick() {
        mainViewModel.getImageList().observe(this, Observer {
            it?.let { resources ->
                when (resources.status) {
                    Status.SUCCESS -> {
                        print("sucess")
                    }
                    Status.ERROR -> {

                    }
                }

            }
        })
    }

}