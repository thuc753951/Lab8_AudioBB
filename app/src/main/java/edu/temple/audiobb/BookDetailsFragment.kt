package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class BookDetailsFragment : Fragment() {

    lateinit var titleTextView: TextView
    lateinit var authorTextView: TextView
    lateinit var coverImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_book_details, container, false)

        titleTextView = layout.findViewById(R.id.titleTextView)
        authorTextView = layout.findViewById(R.id.authorTextView)
        coverImageView = layout.findViewById(R.id.coverImageView)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewModelProvider(requireActivity()).get(SelectedBookViewModel::class.java)
            .getSelectedBook().observe(requireActivity(), {updateBook(it)})
    }


    private fun updateBook(book: Book?) {
        book?.run {
            titleTextView.text = book.id.toString()//title
            authorTextView.text = book.coverURL//author
            coverImageView.setImageResource(book.id)
            //TODO add image view here and fix the online to get books

        }
    }
}