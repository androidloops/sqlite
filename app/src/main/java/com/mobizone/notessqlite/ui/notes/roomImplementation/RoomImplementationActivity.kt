package com.mobizone.notessqlite.ui.notes.roomImplementation

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobizone.notessqlite.R
import com.mobizone.notessqlite.database.AppDatabase
import com.mobizone.notessqlite.database.entity.Note
import com.mobizone.notessqlite.databinding.ActivityRoomimplementationBinding
import com.mobizone.notessqlite.ui.notes.common.ItemClickListener
import com.mobizone.notessqlite.ui.notes.common.NotesAdapter
import com.mobizone.notessqlite.utils.MyDividerItemDecoration
import java.util.*

class RoomImplementationActivity : AppCompatActivity() {
    private val notes = ArrayList<Note>()
    private var mAdapter: NotesAdapter? = null
    private lateinit var db: AppDatabase
    lateinit var binding: ActivityRoomimplementationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_roomimplementation)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        db = AppDatabase.getAppDatabase(this)

        val users = db.userDao().all
        notes.addAll(users)

        binding.fab.setOnClickListener {
            showNoteDialog(false, null, -1)
        }

        mAdapter = NotesAdapter(notes, object : ItemClickListener {
            override fun onEdit(position: Int) {
                showNoteDialog(true, notes[position], position)
            }

            override fun onDelete(position: Int) {
                deleteNote(position)
            }
        })
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.addItemDecoration(MyDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 16))
        binding.recyclerView.adapter = mAdapter

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

        val dialogBuilder = AlertDialog.Builder(this@RoomImplementationActivity)
        dialogBuilder.setView(view)

        val inputNote = view.findViewById<EditText>(R.id.note)
        val dialogTitle = view.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = if (!shouldUpdate) getString(R.string.lbl_new_note_title)
        else getString(R.string.lbl_edit_note_title)

        if (shouldUpdate && note != null) {
            inputNote.setText(note.note)
        }
        dialogBuilder.setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "update" else "save") { _, _ ->
                    // Empty
                }
                .setNegativeButton("cancel") { dialogBox, _ -> dialogBox.cancel() }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            // Show toast message when no text is entered
            if (TextUtils.isEmpty(inputNote.text.toString())) {
                Toast.makeText(this@RoomImplementationActivity, "Enter note!",
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

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private fun updateNote(note: String, position: Int) {
        val n = notes[position]
        // updating note text
        n.note = note

        // updating note in db
        db.userDao().updateNote(n.id, note)

        // refreshing the list
        notes[position] = n
        mAdapter?.notifyItemChanged(position)

        toggleEmptyNotes()
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private fun deleteNote(position: Int) {
        // deleting the note from db
        db.userDao().delete(notes[position])

        // removing the note from the list
        notes.removeAt(position)
        mAdapter?.notifyItemRemoved(position)

        toggleEmptyNotes()
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private fun createNote(note: String) {
        // inserting note in db and getting
        // newly inserted note id
        val user = Note()
        user.note = note
        db.userDao().insert(user)

        // adding new note to array list at 0 position
        notes.clear()
        notes.addAll(db.userDao().all)

        // refreshing the list
        mAdapter?.notifyDataSetChanged()

        toggleEmptyNotes()
    }

    /**
     * Toggling list and empty notes view
     */
    private fun toggleEmptyNotes() {
        // you can check notesList.size() > 0
        db.userDao().all.size.let {
            if (it > 0) {
                binding.emptyNotesView.visibility = View.GONE
            } else {
                binding.emptyNotesView.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        AppDatabase.destroyInstance()
        super.onDestroy()
    }
}
