package com.example.loveChat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class EditActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var mToolbar: Toolbar
    lateinit var mDrawerLayout: DrawerLayout
    lateinit var mNavigationView: NavigationView

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_message)

        val loginTextView = findViewById<TextView>(R.id.textLogin)
        val dateTextView = findViewById<TextView>(R.id.textDate)
        val contentEditText = findViewById<TextView>(R.id.editContent)

        var login = intent.getStringExtra("LOGIN_KEY").toString()
        val id = intent.getStringExtra("ID_KEY").toString()
        loginTextView.text = login
        dateTextView.text = intent.getStringExtra("DATE_KEY")
        contentEditText.text = intent.getStringExtra("CONTENT_KEY")

        val sharedPref: SharedPreferences = getSharedPreferences("LOGIN", 0)
        var savedLogin = sharedPref.getString("NAME", "-").toString()
        val inputSend = findViewById<EditText>(R.id.editContent)
        val buttonSend = findViewById<Button>(R.id.buttonUpdate)

        buttonSend.setOnClickListener {
            if (isConnected == true) {
                if (savedLogin == login) {
                    val content = inputSend.text.toString()
                    Thread {
                        putJson(login, content, id)
                    }.start()
                    val intent = Intent(this, ShoutboxActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Unauthorized login", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show()
            }

        }

        val deleteMessage = findViewById(R.id.imageDelete) as ImageView
        deleteMessage.setOnClickListener {
            if (isConnected == true) {
                if (savedLogin == login) {
                    deleteJson(id)
                    Toast.makeText(this, "Deleted !", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ShoutboxActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Unautorized access !", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show()
            }

        }


        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        mDrawerLayout = findViewById(R.id.drawer_layout_edit)
        mNavigationView = findViewById(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, mDrawerLayout, mToolbar, 0, 0
        )
        mDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mNavigationView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.shoutbox, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setings -> {
                val mainActivity = Intent(this, LoginActivity::class.java)
                startActivity(mainActivity)
                finish()
            }
            R.id.nav_home -> {
                val shoutboxActivity = Intent(this, ShoutboxActivity::class.java)
                startActivity(shoutboxActivity)
                finish()
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun putJson(login: String, content: String, id: String) {
        val put = ChatItem()
        put.login = login
        put.content = content

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://tgryl.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jsonPlaceholderApi: ChatApiService = retrofit.create(ChatApiService::class.java)
        val call = jsonPlaceholderApi.putMessage(id.toString(), put)
        call.enqueue(object : Callback<ChatItem> {
            override fun onFailure(call: Call<ChatItem>, t: Throwable) {
                Log.d("error ", "onFailure:")
            }

            override fun onResponse(
                call: Call<ChatItem>, response: Response<ChatItem>
            ) {
                Log.d("good ", "onResponse:")
            }
        })
    }

    fun deleteJson(id: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://tgryl.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jsonPlaceholderApi: ChatApiService = retrofit.create(ChatApiService::class.java)
        val call = jsonPlaceholderApi.deleteMessage(id.toString())
        call.enqueue(object : Callback<ChatItem> {
            override fun onFailure(call: Call<ChatItem>, t: Throwable) {
                Log.d("error ", "onFailure:")
            }

            override fun onResponse(
                call: Call<ChatItem>, response: Response<ChatItem>
            ) {
                Log.d("good ", "onResponse:")
            }
        })
    }

    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }
}

