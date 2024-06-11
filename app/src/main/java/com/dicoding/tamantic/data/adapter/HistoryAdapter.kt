package com.dicoding.tamantic.data.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.tamantic.R
import com.dicoding.tamantic.data.model.ScanModel
import com.dicoding.tamantic.view.activity.scan.ResultScanActivity

class HistoryAdapter(private var historyList: MutableList<ScanModel>) : RecyclerView
.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById<ImageView>(R.id.image_scan_history)
        val tvDieses = view.findViewById<TextView>(R.id.nama_penyakit_history)
        val score = view.findViewById<TextView>(R.id.convidence_score_history)
        val progres = view.findViewById<ProgressBar>(R.id.circularProgressBar_history)
        val explain = view.findViewById<TextView>(R.id.explain_info)
        val expand = view.findViewById<LinearLayout>(R.id.expandable_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_list_history, parent,
            false
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = historyList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = historyList[position]

        val score = Math.round(data.confidenceScore)
        holder.tvDieses.text = data.result
        holder.score.text = score.toString() + "%"
        holder.progres.progress = score
        holder.explain.text = data.explanation

        Glide.with(holder.ivImage).load(data.image).into(holder.ivImage)

        holder.itemView.setOnClickListener {
            val isVIsible = holder.expand.visibility == View.VISIBLE
            holder.expand.visibility = if(isVIsible) View.GONE else View.VISIBLE
        }

    }
}