package com.example.tallytracker

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout

class TallyAdapter(
    private val tallies: MutableList<Tally>,
    private val onDataChanged: () -> Unit
) : RecyclerView.Adapter<TallyAdapter.TallyViewHolder>() {

    class TallyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val root: ConstraintLayout = view.findViewById(R.id.tallyItemRoot)
        val title: TextView = view.findViewById(R.id.tallyTitle)
        val count: EditText = view.findViewById(R.id.tallyCount)
        val btnIncrement: Button = view.findViewById(R.id.btnIncrement)
        val btnDecrement: Button = view.findViewById(R.id.btnDecrement)
        val btnReset: Button = view.findViewById(R.id.btnReset)
        val btnDelete: TextView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TallyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tally_item, parent, false)
        return TallyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TallyViewHolder, position: Int) {
        val tally = tallies[position]
        
        holder.title.text = formatTitle(tally.title)
        holder.count.setText(tally.count.toString())

        // Handle delete button
        holder.btnDelete.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                tallies.removeAt(currentPosition)
                notifyItemRemoved(currentPosition)
                notifyItemRangeChanged(currentPosition, tallies.size)
                onDataChanged()
            }
        }

        // Handle buttons
        holder.btnIncrement.setOnClickListener {
            tally.count++
            holder.count.setText(tally.count.toString())
            onDataChanged()
        }

        holder.btnDecrement.setOnClickListener {
            if (tally.count > 0) {
                tally.count--
                holder.count.setText(tally.count.toString())
                onDataChanged()
            }
        }

        holder.btnReset.setOnClickListener {
            tally.count = 0
            holder.count.setText("0")
            onDataChanged()
        }

        // Handle manual text editing
        holder.count.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = holder.count.text.toString()
                if (text.isNotEmpty()) {
                    tally.count = text.toIntOrNull() ?: 0
                } else {
                    holder.count.setText(tally.count.toString())
                }
                onDataChanged()
            }
        }

        // Clear focus when clicking outside
        holder.root.setOnClickListener {
            if (holder.count.hasFocus()) {
                holder.count.clearFocus()
                val imm = holder.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(holder.count.windowToken, 0)
            }
        }
    }

    override fun getItemCount() = tallies.size

    fun addTally(tally: Tally) {
        tallies.add(tally)
        notifyItemInserted(tallies.size - 1)
        onDataChanged()
    }

    private fun formatTitle(title: String): String {
        if (title.length <= 14) return title
        
        // Find the last space within the first 14 characters
        // We look at substring(0, 15) to include the 15th char if it's a space, 
        // but actually we want to break *before* the 15th char if possible.
        // Let's look at the first 15 chars and find the last space.
        val limit = minOf(15, title.length)
        val splitIndex = title.substring(0, limit).lastIndexOf(' ')
        
        return if (splitIndex != -1) {
            // Replace that space with a newline
            title.substring(0, splitIndex) + "\n" + title.substring(splitIndex + 1)
        } else {
            // If no space found, return original
            title
        }
    }
}
