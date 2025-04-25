package com.example.comopseexample

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.comopseexample.ui.theme.ComopseexampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComopseexampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Your Profile Section",
                modifier = modifier.padding(16.dp),
                color = Color.Blue
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "image",
                modifier = modifier
                    .size(64.dp)
            )
        }
        TextField(
            value = "Enter your name",
            onValueChange = {},
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
        TextField(
            value = "Enter your mobile number",
            onValueChange = {},
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
        TextField(
            value = "Enter your email",
            onValueChange = {},
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
        Button(onClick = {}, modifier = modifier.padding(12.dp).fillMaxWidth().size(32.dp)) {
            Text(text = "Submit")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComopseexampleTheme {
        Greeting("Android", modifier = Modifier.padding(16.dp))
    }
}