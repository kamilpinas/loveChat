package com.example.loveChat

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_shoutbox.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ShoutboxActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navView: NavigationView
    lateinit var mAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoutbox)
        getJson()

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val sharedPref: SharedPreferences = getSharedPreferences("LOGIN", 0)
                val login = sharedPref.getString("NAME", "-").toString()
                val item = mAdapter.getItem(viewHolder.adapterPosition)
                if (isConnected == true) {
                    if (login == item.login) {
                        deleteJson(item.id)
                        mAdapter.removeItem(viewHolder.adapterPosition)
                        Toast.makeText(this@ShoutboxActivity, "Deleted!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            this@ShoutboxActivity,
                            "Unautorized access!",
                            Toast.LENGTH_LONG
                        ).show()
                        getJson()
                    }
                } else {
                    Toast.makeText(
                        this@ShoutboxActivity,
                        "NO INTERNET CONNECTION",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recycler_view)

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, 0, 0
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val sendMessage = findViewById<Button>(R.id.userTextButton)
        val messageContent = findViewById<TextView>(R.id.userTextInput)
        sendMessage.setOnClickListener {
            if (isConnected == true) {
                Toast.makeText(this, "Message sent!", Toast.LENGTH_LONG).show()
                val sharedPref: SharedPreferences = getSharedPreferences("LOGIN", 0)
                val login = sharedPref.getString("NAME", "-").toString()
                val content = messageContent.text.toString()
                postJson(login, content)
                getJson()
                messageContent.text = ""
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show()
            }

        }

        itemsswipetorefresh.setOnRefreshListener {
            if (isConnected == true) {
                getJson()
                itemsswipetorefresh.isRefreshing = false
            } else {
                Toast.makeText(this, "NO INTERNET CONNECTION", Toast.LENGTH_LONG).show()
            }

        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                if (isConnected == true) {
                    getJson()
                    handler.postDelayed(this, 30000)
                } else {
                    Toast.makeText(
                        this@ShoutboxActivity,
                        "NO INTERNET CONNECTION",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.shoutbox, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun getJson() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://tgryl.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jsonPlaceholderApi: ChatApiService = retrofit.create(ChatApiService::class.java)
        val call: Call<ArrayList<ChatItem>> = jsonPlaceholderApi.getMessages()
        call.enqueue(object : Callback<ArrayList<ChatItem>> {
            override fun onFailure(call: Call<ArrayList<ChatItem>>, t: Throwable) {
                Log.d("getJson ", "onFailure:")
            }

            override fun onResponse(
                call: Call<ArrayList<ChatItem>>, response: Response<ArrayList<ChatItem>>
            ) {
                Log.d("getJson ", response.code().toString())
                val chat = response.body()!!
                chat.reverse()
                recycler_view.adapter = ChatAdapter(chat)
                mAdapter = recycler_view.adapter as ChatAdapter
            }
        })
    }

    fun postJson(login: String, content: String) {

        val add = ChatItem()
        add.login = login
        add.content = content
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("http://tgryl.pl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val jsonPlaceholderApi: ChatApiService = retrofit.create(ChatApiService::class.java)
        val call = jsonPlaceholderApi.postMessage(add)
        call.enqueue(object : Callback<ChatItem> {
            override fun onFailure(call: Call<ChatItem>, t: Throwable) {
                Log.d("postJson ", "onFailure:")
            }

            override fun onResponse(
                call: Call<ChatItem>, response: Response<ChatItem>
            ) {
                Log.d("postJson ", "onResponse:")
                userTextInput.setText("")
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
                Log.d("deleteJson ", "onFailure:")
            }

            override fun onResponse(
                call: Call<ChatItem>, response: Response<ChatItem>
            ) {
                Log.d("deleteJson ", "onResponse:")
                Log.d("deleteJson ", response.code().toString())
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_setings -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    val Context.isConnected: Boolean
        get() {
            return (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        }
}


