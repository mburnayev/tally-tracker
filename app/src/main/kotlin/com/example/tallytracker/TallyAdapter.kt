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

class TallyAdapter(private val tallies: MutableList<Tally>) : RecyclerView.Adapter<TallyAdapter.TallyViewHolder>() {

    class TallyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tallyTitle)
        val count: EditText = view.findViewById(R.id.tallyCount)
        val btnIncrement: Button = view.findViewById(R.id.btnIncrement)
        val btnDecrement: Button = view.findViewById(R.id.btnDecrement)
        val btnReset: Button = view.findViewById(R.id.btnReset)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TallyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tally_item, parent, false)
        return TallyViewHolder(view)
    }

    override fun onBindViewHolder(holder: TallyViewHolder, position: Int) {
        val tally = tallies[position]
        
        holder.title.text = tally.title
        holder.count.setText(tally.count.toString())

        // Handle buttons
        holder.btnIncrement.setOnClickListener {
            tally.count++
            holder.count.setText(tally.count.toString())
        }

        holder.btnDecrement.setOnClickListener {
            if (tally.count > 0) {
                tally.count--
                holder.count.setText(tally.count.toString())
            }
        }

        holder.btnReset.setOnClickListener {
            tally.count = 0
            holder.count.setText("0")
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
            }
        }
    }

    override fun getItemCount() = tallies.size

    fun addTally(tally: Tally) {
        tallies.add(tally)
        notifyItemInserted(tallies.size - 1)
    }
}
