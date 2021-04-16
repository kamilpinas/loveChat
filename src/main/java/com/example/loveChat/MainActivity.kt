package com.example.loveChat

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);
        textLogin.setText(sharedPreferences.getString("NAME", ""))
        if (textLogin.text.toString().isNotEmpty()) {
            val intent = Intent(this, ShoutboxActivity::class.java)
            startActivity(intent)
        }
        button.setOnClickListener {
            val name = textLogin.text.toString().trim()
            val editor = sharedPreferences.edit()
            editor.putString("NAME", name)
            editor.apply()
            val intent = Intent(this, ShoutboxActivity::class.java)
            startActivity(intent)
        }
    }
}
