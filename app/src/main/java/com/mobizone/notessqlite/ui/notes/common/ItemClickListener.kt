package com.mobizone.notessqlite.ui.notes.common

interface ItemClickListener {
    fun onEdit(position: Int)

    fun onDelete(position: Int)
}