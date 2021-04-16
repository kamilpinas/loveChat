package com.example.loveChat

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        textLogin.setText(sharedPreferences.getString("NAME", ""))

        button.setOnClickListener {
            val name = textLogin.text.toString().trim()
            val editor = sharedPreferences.edit()
            editor.putString("NAME", name)
            editor.apply()
            if(isConnected==true)
            {
                val intent = Intent(this, ShoutboxActivity::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show()
            }
        }
    }
    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }
}