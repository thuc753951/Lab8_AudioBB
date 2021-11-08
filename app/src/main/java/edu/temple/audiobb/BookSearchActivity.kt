package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException

class BookSearchActivity : AppCompatActivity() {

    // the views init
    val webEditTextView: EditText by lazy {
        findViewById(R.id.WebEditText)
    }
    val webSearch: Button by lazy {
        findViewById(R.id.WebSearch)
    }
    val webCancel: Button by lazy{
        findViewById(R.id.WebCancel)
    }

    /*val volleyQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }*/
    val cache = DiskBasedCache(cacheDir, 1024 * 1024)// 1mb

    val network = BasicNetwork(HurlStack())

    val requestQueue = RequestQueue(cache,network).apply {
        start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        //make intent


        webSearch.setOnClickListener {
            getData()
        }

        webCancel.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

    }

    fun getData(){

        val url = "https://Kamorris.com/lab/cis3515/search.php?term=" + webEditTextView.text

        //val title: String, val author: String, val id: Int, val coverURL: String
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response -> try {
                val bookList = BookList()
                val jsonArray = JSONArray(response)
                val jsonLength = jsonArray.length()
                var i = 0
                while(i < jsonLength){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val book = Book(jsonObject.getString("title"), jsonObject.getString("author"), jsonObject.getInt("id"), jsonObject.getString("cover_url"))
                    bookList.add(book)
                    i++
                }

            } catch (e:JSONException){
                e.printStackTrace()
            } },
            Response.ErrorListener { error ->
                Log.e("network", "ERROR: %s".format(error.toString()))
            }
        )
        requestQueue.add(jsonObjectRequest)

    }
}