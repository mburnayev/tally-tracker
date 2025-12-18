package com.example.tallytracker

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TallyAdapter
    private val tallies = mutableListOf<Tally>()
    private val PREFS_NAME = "TallyTrackerPrefs"
    private val KEY_TALLIES = "tallies_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load saved tallies
        loadTallies()

        // Setup RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.tallyRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = TallyAdapter(tallies) { saveTallies() }
        recyclerView.adapter = adapter

        // Setup Add Button
        val addButton = findViewById<MaterialButton>(R.id.addButton)
        addButton.setOnClickListener {
            showAddTallyDialog()
        }
    }

    override fun onPause() {
        super.onPause()
        saveTallies()
    }

    private fun saveTallies() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(tallies)
        editor.putString(KEY_TALLIES, json)
        editor.apply()
    }

    private fun loadTallies() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString(KEY_TALLIES, null)
        
        if (json != null) {
            val type = object : TypeToken<MutableList<Tally>>() {}.type
            val savedTallies: MutableList<Tally> = gson.fromJson(json, type)
            tallies.clear()
            tallies.addAll(savedTallies)
        }
    }

    private fun showAddTallyDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_tally, null)
        val inputName = dialogView.findViewById<EditText>(R.id.inputTallyName)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Make dialog background transparent to show our rounded drawable
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            val title = inputName.text.toString()
            if (title.isNotEmpty()) {
                val newTally = Tally(title)
                adapter.addTally(newTally)
                saveTallies() // Save immediately when adding
                dialog.dismiss()
            } else {
                inputName.error = "Please enter a title"
            }
        }

        dialog.show()
    }


    override fun dispatchTouchEvent(event: android.view.MotionEvent): Boolean {
        if (event.action == android.view.MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = android.graphics.Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
