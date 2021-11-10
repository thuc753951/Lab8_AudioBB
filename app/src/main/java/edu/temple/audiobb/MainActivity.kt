package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider



class MainActivity : AppCompatActivity(), BookListFragment.BookSelectedInterface {

    var bookList = BookList()

    val BookSearchActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        //Log.d("Returned data", it.data?.getStringExtra("resultValue").toString())
        result->
        if(result.resultCode == Activity.RESULT_OK){
            bookList = result.data?.getSerializableExtra("data") as BookList
            Log.d("json", "MAIN-ACTIVITY, 0: "+ bookList.get(0).title) // ssuccesss does go through
            val fragment = supportFragmentManager.findFragmentById(R.id.container1)
            if(fragment is BookListFragment){
                Log.d("json", "fragment is booklist") // successs does go through
                fragment.setBookList(bookList)
                supportFragmentManager.beginTransaction()
                        .detach(fragment)
                        .attach(fragment)
                        .commit()
            }
        }

    }

    val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    val selectedBookViewModel : SelectedBookViewModel by lazy {
        ViewModelProvider(this).get(SelectedBookViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //var booklist = getBookList()


        // If we're switching from one container to two containers
        // clear BookDetailsFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookDetailsFragment) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment.newInstance(bookList))
                .commit()
        }else
            // If activity loaded previously, there's already a BookListFragment
            // If we have a single container and a selected book, place it on top

            if (isSingleContainer && selectedBookViewModel.getSelectedBook().value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }

            // If we have two containers but no BookDetailsFragment, add one to container2
            if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookDetailsFragment)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container2, BookDetailsFragment())
                    .commit()

            val button = findViewById<Button>(R.id.Searchbutton)
            button.setOnClickListener {
                val intent = Intent(this@MainActivity, BookSearchActivity::class.java)
                BookSearchActivityLauncher.launch(intent)
            }

    }


    override fun onBackPressed() {
        // Backpress clears the selected book
        selectedBookViewModel.setSelectedBook(null)
        super.onBackPressed()
    }

    override fun bookSelected() {
        // Perform a fragment replacement if we only have a single container
        // when a book is selected

        if (isSingleContainer) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }
}