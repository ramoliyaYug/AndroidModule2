package com.example.coroutineexample

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.coroutineexample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var count :Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.p1btn.setOnClickListener {
            count++
            binding.counttv.text = count.toString()
        }

        binding.p2btn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch{
                for (i in 1..1000000000000000000){
                    Log.i("Flag","Downloading $i in ${Thread.currentThread().name}")
                }
            }
        }
    }
}