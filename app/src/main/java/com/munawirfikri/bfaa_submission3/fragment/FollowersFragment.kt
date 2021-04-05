package com.munawirfikri.bfaa_submission3.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.munawirfikri.bfaa_submission3.BuildConfig
import com.munawirfikri.bfaa_submission3.DetailActivity
import com.munawirfikri.bfaa_submission3.R
import com.munawirfikri.bfaa_submission3.adapter.UserAdapter
import com.munawirfikri.bfaa_submission3.model.User
import cz.msebera.android.httpclient.Header
import org.json.JSONArray

class FollowersFragment : Fragment() {

    private lateinit var rvFollow : RecyclerView

    companion object {
        private const val ARG_USERNAME = "username"
        private val TAG = FollowersFragment::class.java.simpleName

        fun newInstance(username: String) =
            FollowersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARG_USERNAME)
        getData(username)
    }

    private fun getData(query: String?) {
        rvFollow = requireView().findViewById(R.id.rv_followers)
        activity!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("Authorization", "token " + BuildConfig.GITHUB_TOKEN)
        client.addHeader("User-Agent", "request")
        val url = "https://api.github.com/users/$query/followers"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                // Jika koneksi berhasil
                activity!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                val result = String(responseBody)
                Log.d(TAG, result)
                try {
                    val users = arrayListOf<User>()
                    val items = JSONArray(result)
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
                    }
                    rvFollow.layoutManager = LinearLayoutManager(activity)
                    rvFollow.scrollToPosition(0)
                    rvFollow.setHasFixedSize(true)
                    val userAdapter = UserAdapter(users)
                    rvFollow.adapter = userAdapter
                    userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
                        override fun onItemClicked(data: User) {
                            showSelectedUser(data)
                        }
                    })
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
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
                activity!!.findViewById<ProgressBar>(R.id.progressBar).visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }
                Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showSelectedUser(user: User)
    {
        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, user)
        startActivity(intent)
    }


}