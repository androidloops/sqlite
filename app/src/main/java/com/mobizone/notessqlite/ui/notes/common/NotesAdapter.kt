package com.mobizone.notessqlite.ui.notes.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobizone.notessqlite.R
import com.mobizone.notessqlite.database.entity.Note
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(private val notesList: List<Note>,
                   private val itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<NotesAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var note: TextView = view.findViewById(R.id.note)
        var timestamp: TextView = view.findViewById(R.id.timestamp)
        var edit: ImageButton = view.findViewById(R.id.edit)
        var delete: ImageButton = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_note, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = notesList[position]

        holder.note.text = note.note

        // Formatting and displaying timestamp
        holder.timestamp.text = formatDate(note.timestamp)

        holder.edit.setOnClickListener {
            itemClickListener.onEdit(position)
        }
        holder.delete.setOnClickListener {
            itemClickListener.onDelete(position)
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private fun formatDate(dateStr: String?): String {
        try {
            if (dateStr != null) {
                val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                val date = fmt.parse(dateStr)
                val fmtOut = SimpleDateFormat("MMM d", Locale.US)
                return fmtOut.format(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return ""
    }
}
