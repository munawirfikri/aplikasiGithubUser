package com.munawirfikri.consumerapp

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.munawirfikri.consumerapp.DetailActivity.Companion.EXTRA_USER
import com.munawirfikri.consumerapp.adapter.FavoriteAdapter
import com.munawirfikri.consumerapp.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.munawirfikri.consumerapp.databinding.ActivityFavoriteBinding
import com.munawirfikri.consumerapp.helper.MappingHelper
import com.munawirfikri.consumerapp.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: ActivityFavoriteBinding

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvUser.layoutManager = LinearLayoutManager(this)
        binding.rvUser.setHasFixedSize(true)
        adapter = FavoriteAdapter(this)
        binding.rvUser.adapter = adapter
        adapter.setOnItemClickCallback(object : FavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showSelectedUser(data)
            }
        })

        binding.navBack.setOnClickListener(this)

        val handlerThread = HandlerThread("DataObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler)
        {
            override fun onChange(selfChange: Boolean) {
                loadFavoriteAsync()
            }
        }

        contentResolver.registerContentObserver(CONTENT_URI, true, myObserver)
        if (savedInstanceState == null) {
            loadFavoriteAsync()
        } else {
            savedInstanceState.getParcelableArrayList<User>(EXTRA_STATE)?.also { adapter.listFavorite = it }
        }

    }

    private fun showSelectedUser(user: User)
    {
        val intent = Intent(this@FavoriteActivity, DetailActivity::class.java)
        intent.putExtra(EXTRA_USER, user)
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavorite)
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.progressBar.visibility = View.VISIBLE
            val deferredNotes = async(Dispatchers.IO) {
                val cursor = contentResolver.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val users = deferredNotes.await()
            if (users.size > 0) {
                adapter.listFavorite = users
            } else {
                adapter.listFavorite.clear()
                adapter.listFavorite = ArrayList()
                Snackbar.make(binding.rvUser, "Tidak ada pengguna favorit saat ini", Snackbar.LENGTH_SHORT).show()
            }
            binding.progressBar.visibility = View.INVISIBLE
        }
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.nav_back -> finish()
        }
    }
}