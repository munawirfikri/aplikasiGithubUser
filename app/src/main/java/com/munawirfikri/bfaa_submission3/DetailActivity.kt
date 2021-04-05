package com.munawirfikri.bfaa_submission3

import android.content.ContentValues
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.munawirfikri.bfaa_submission3.adapter.SectionsPagerAdapter
import com.munawirfikri.bfaa_submission3.database.DatabaseContract
import com.munawirfikri.bfaa_submission3.database.DatabaseContract.FavColumns.Companion.CONTENT_URI
import com.munawirfikri.bfaa_submission3.databinding.ActivityDetailBinding
import com.munawirfikri.bfaa_submission3.helper.MappingHelper
import com.munawirfikri.bfaa_submission3.model.User
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class DetailActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityDetailBinding
    private lateinit var tvName : TextView
    private lateinit var tvCompany : TextView
    private lateinit var tvLocation : TextView
    private lateinit var tvRepository : TextView
    private lateinit var tvFollowers : TextView
    private lateinit var tvFollowing : TextView
    private lateinit var uriWithUserId: Uri

    companion object {
        private val TAG = DetailActivity::class.java.simpleName
        const val EXTRA_USER = "extra_user"
        private var statusFavorite = false
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tvUsername: TextView = findViewById(R.id.tv_detail_username)
        val imgAvatar: ImageView = findViewById(R.id.img_detail_avatar)
        val goBack : LinearLayout = findViewById(R.id.navigation)
        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User


        tvUsername.text = user.username
        Glide.with(this)
            .load(user.avatar)
            .apply(RequestOptions().override(100, 100))
            .into(imgAvatar)

        getData(user.username.toString())
        goBack.setOnClickListener(this)

        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        sectionsPagerAdapter.username = user.username.toString()
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f

        // STATE FAVORITE

        uriWithUserId = Uri.parse(CONTENT_URI.toString() + "/" + user.username)
//        Log.d(TAG, "hasil: " + uriWithUserId)
//        setStatusFavorite(statusFavorite)
//        binding.rlFavorite.setOnClickListener(this)

        try {
            val cursor = contentResolver.query(uriWithUserId, null, null, null, null)
            if (cursor != null) {
                val uuser : User = MappingHelper.mapCursorToObject(cursor)
                statusFavorite = true
                cursor.close()
                Log.d(TAG, "cursor: " + uuser)
            }
            Log.d(TAG,"hasil: " + cursor)
        }catch (e: Exception) {
            statusFavorite = false
        }
        setStatusFavorite(statusFavorite)
        binding.rlFavorite.setOnClickListener(this)

    }

    private fun setStatusFavorite(statusFavorite: Boolean) {
        if (statusFavorite) {
            val color = Color.RED
            binding.tvFav.compoundDrawables.get(1).setTint(color)
            binding.tvFav.text = getString(R.string.hapus_fav)
        }else
        {
            val color = Color.WHITE
            binding.tvFav.compoundDrawables.get(1).setTint(color)
            binding.tvFav.text = getString(R.string.tambah_fav)
        }
    }

    private fun getData(query: String) {
        binding.progressBar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token " + BuildConfig.GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$query"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                binding.progressBar.visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val responseObject = JSONObject(result)
                    tvName = findViewById(R.id.tv_detail_name)
                    tvCompany = findViewById(R.id.tv_detail_company)
                    tvLocation= findViewById(R.id.tv_detail_location)
                    tvRepository = findViewById(R.id.tv_detail_repository)
                    tvFollowers = findViewById(R.id.tv_detail_followers)
                    tvFollowing = findViewById(R.id.tv_detail_following)

                    tvName.text = responseObject.getString("name")
                    tvCompany.text = responseObject.getString("company")
                    tvLocation.text = responseObject.getString("location")
                    tvRepository.text = getString(R.string.repository, responseObject.getString("public_repos"))
                    tvFollowers.text = getString(R.string.followers, responseObject.getString("followers"))
                    tvFollowing.text = getString(R.string.following, responseObject.getString("following"))

                } catch (e: Exception) {
                    Toast.makeText(this@DetailActivity, e.message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
                // Parsing JSON
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
                Toast.makeText(this@DetailActivity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.navigation -> finish()
            R.id.rl_favorite -> {
                statusFavorite = !statusFavorite
                if (statusFavorite) {
                    // Tambah favorite
                    val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
                    val userId = user.id
                    val username = user.username
                    val userURL = user.userURL
                    val avatar = user.avatar

                    val values = ContentValues()
                    values.put(DatabaseContract.FavColumns.ID_USER, userId)
                    values.put(DatabaseContract.FavColumns.USERNAME, username)
                    values.put(DatabaseContract.FavColumns.USER_URL, userURL)
                    values.put(DatabaseContract.FavColumns.AVATAR, avatar)
                    contentResolver.insert(CONTENT_URI, values)
                    Snackbar.make(binding.viewPager, "User berhasil di tambahkan ke daftar favorit", Snackbar.LENGTH_SHORT).show()
                }else {
                    // Hapus favorite
                    Log.d(TAG,"idnya: " + uriWithUserId)
                    contentResolver.delete(uriWithUserId, null, null)
                    Snackbar.make(binding.viewPager, "User berhasil di hapus dari daftar favorit", Snackbar.LENGTH_SHORT).show()
                }
                setStatusFavorite(statusFavorite)
            }
        }
    }

}