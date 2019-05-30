package com.mobizone.notessqlite.ui.notes.nativeimplementation

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mobizone.notessqlite.R
import com.mobizone.notessqlite.database.DatabaseHelper
import com.mobizone.notessqlite.database.entity.Note
import com.mobizone.notessqlite.ui.notes.common.ItemClickListener
import com.mobizone.notessqlite.ui.notes.common.NotesAdapter
import com.mobizone.notessqlite.utils.MyDividerItemDecoration
import java.util.*

class NativeImplementationActivity : AppCompatActivity() {
    private var mAdapter: NotesAdapter? = null
    private val notesList = ArrayList<Note>()
    private var mEmptyView: TextView? = null

    private var dbHelper: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nativeimplementation)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        mEmptyView = findViewById(R.id.empty_notes_view)

        dbHelper = DatabaseHelper(this)

        dbHelper?.allNotes?.let {
            notesList.addAll(it)
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { showNoteDialog(false, null, -1) }

        mAdapter = NotesAdapter(notesList, object : ItemClickListener {
            override fun onEdit(position: Int) {
                showNoteDialog(true, notesList[position], position)
            }

            override fun onDelete(position: Int) {
                deleteNote(position)
            }
        })
        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16))
        recyclerView.adapter = mAdapter

        toggleEmptyNotes()
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private fun createNote(note: String) {
        // inserting note in db and getting
        // newly inserted note id
        val id = dbHelper?.insertNote(note)

        // get the newly inserted note from db
        val n = id?.let { dbHelper?.getNote(it) }

        if (n != null) {
            // adding new note to array list at 0 position
            notesList.add(0, n)

            // refreshing the list
            mAdapter?.notifyDataSetChanged()

            toggleEmptyNotes()
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private fun updateNote(note: String, position: Int) {
        val n = notesList[position]
        // updating note text
        n.note = note

        // updating note in db
        dbHelper?.updateNote(n)

        // refreshing the list
        notesList[position] = n
        mAdapter?.notifyItemChanged(position)

        toggleEmptyNotes()
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private fun deleteNote(position: Int) {
        // deleting the note from db
        dbHelper?.deleteNote(notesList[position])

        // removing the note from the list
        notesList.removeAt(position)
        mAdapter?.notifyItemRemoved(position)

        toggleEmptyNotes()
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private fun showNoteDialog(shouldUpdate: Boolean, note: Note?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.dialog_note, null)

        val dialogBuilder = AlertDialog.Builder(this@NativeImplementationActivity)
        dialogBuilder.setView(view)

        val inputNote = view.findViewById<EditText>(R.id.note)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = if (!shouldUpdate) getString(R.string.lbl_new_note_title) else getString(R.string.lbl_edit_note_title)

        if (shouldUpdate && note != null) {
            inputNote.setText(note.note)
        }
        dialogBuilder
                .setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "update" else "save") { _, _ ->
                    // Empty
                }
                .setNegativeButton("cancel") { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputNote.text.toString())) {
                Toast.makeText(this@NativeImplementationActivity, "Enter note!",
                        Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                alertDialog.dismiss()
            }

            // check if user updating note
            if (shouldUpdate && note != null) {
                // update note by it's id
                updateNote(inputNote.text.toString(), position)
            } else {
                // create new note
                createNote(inputNote.text.toString())
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Toggling list and empty notes view
     */
    private fun toggleEmptyNotes() {
        // you can check notesList.size() > 0
        dbHelper?.notesCount?.let {
            if (it > 0) {
                mEmptyView?.visibility = View.GONE
            } else {
                mEmptyView?.visibility = View.VISIBLE
            }
        }
    }
}
