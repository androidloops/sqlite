package com.mobizone.notessqlite.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.mobizone.notessqlite.R
import com.mobizone.notessqlite.ui.notes.nativeimplementation.NativeImplementationActivity
import com.mobizone.notessqlite.ui.notes.roomImplementation.RoomImplementationActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        native_implementation.setOnClickListener {
            startActivity(Intent(this@MainActivity, NativeImplementationActivity::class.java))
        }

        room_implementation.setOnClickListener {
            startActivity(Intent(this@MainActivity, RoomImplementationActivity::class.java))
        }
    }
}