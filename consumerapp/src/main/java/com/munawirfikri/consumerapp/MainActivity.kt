package com.munawirfikri.consumerapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.munawirfikri.consumerapp.adapter.UserAdapter
import com.munawirfikri.consumerapp.databinding.ActivityMainBinding
import com.munawirfikri.consumerapp.model.User
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var users = arrayListOf<User>()
    private lateinit var rvUser: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private lateinit var tvFavorite: TextView

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvFavorite = findViewById(R.id.tv_favorite)
        rvUser = findViewById(R.id.rv_user)
        rvUser.setHasFixedSize(true)

        tvFavorite.setOnClickListener(this)

        // SEARCH
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = findViewById<SearchView>(R.id.search)

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                binding.progressBar.visibility = View.VISIBLE
                binding.info.visibility = View.GONE
                getData(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

    }


    private fun getData(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.svList.visibility = View.INVISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token b7b8fcbbf2d185478477f70fd909c47a51c821cc")
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/search/users?q=$query&page=1&per_page=50"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                binding.svList.visibility = View.VISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    val items = responseObject.getJSONArray("items")

                    users.clear()
                    for (i in 0 until items.length()) {
                        val item = items.getJSONObject(i)
                        val user = User()
                        user.username = item.getString("login")
                        user.avatar = item.getString("avatar_url")
                        user.id = item.getInt("id")
                        user.userURL = item.getString("html_url")

                        for(a in 0 until users.size) {
                            if ( users[a].username.equals(user.username) )
                            {
                                break
                            }
                        }
                        users.add(user)
                        showRecyclerList()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                // Jika koneksi gagal
                binding.progressBar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showRecyclerList() {
        rvUser.layoutManager = LinearLayoutManager(this)
        val userAdapter = UserAdapter(users)
        rvUser.adapter = userAdapter

        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })
    }

    private fun showSelectedUser(user: User)
    {
        val intent = Intent(this@MainActivity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, user)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
            when(v) {
                tvFavorite -> {
                    val intent = Intent(this@MainActivity, FavoriteActivity::class.java)
                    startActivity(intent)
                }
            }
    }


}