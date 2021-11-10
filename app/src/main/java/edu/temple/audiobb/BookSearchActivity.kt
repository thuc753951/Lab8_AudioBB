package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
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
import org.json.JSONObject
import org.json.JSONTokener

class BookSearchActivity : Activity() {

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
//    val cache = DiskBasedCache(cacheDir, 1024 * 1024)// 1mb
//
//    val network = BasicNetwork(HurlStack())

    // this request queue might need to change to volley instead and use it in onCreate. but put it here for now to see if it works
    val VolleyQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
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
        // use jsonArrayReuest instead of object request
        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                { response ->
                    Log.d("json", "line 73")
                    var bookList = BookList()
                    // response is a json array
                    //val jsonArray = JSONArray(json)
                    val jsonLength = response.length()
                    var i = 0
                    while(i < jsonLength){
                        val jsobObject = response.getJSONObject(i)
                        val book = Book(jsobObject.getString("title"), jsobObject.getString("author"), jsobObject.getInt("id"), jsobObject.getString("cover_url"))
                        bookList.add(book)
                        Log.d("json", i.toString() +": "+ book.title)
                        i++
                    }
                //send book list by intent back to main activity
                    val mainIntent = Intent(this@BookSearchActivity, MainActivity::class.java)
                    mainIntent.putExtra("data", bookList)
                    setResult(RESULT_OK, mainIntent)
                    finish()

                },
                { error ->
                    Log.e("network", "ERROR: %s".format(error.toString()))
                }
        )
        VolleyQueue.add(jsonObjectRequest)

    }
}