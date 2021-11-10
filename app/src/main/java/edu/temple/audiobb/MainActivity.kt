package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            Toast.makeText(this, "${bookList.get(0).title}", Toast.LENGTH_SHORT).show()
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
        } else
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

//    private fun getBookList() : BookList {
//        val bookList = BookList()
//        bookList.add(Book(0, "Author 9"))
//        bookList.add(Book(1, "Author 8"))
//        bookList.add(Book(2, "Author 7"))
//        bookList.add(Book(3, "Author 6"))
//        bookList.add(Book(4, "Author 5"))
//        bookList.add(Book(5, "Author 4"))
//        bookList.add(Book(6, "Author 3"))
//        bookList.add(Book(7, "Author 3"))
//        bookList.add(Book(8, "Author 2"))
//        bookList.add(Book(9, "Author 0"))
//
//        return bookList
//    }

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