package com.example.labexam4

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView

class WinstreakAdapter(private var winStreak: List<WinStreak>, context: Context) :
    RecyclerView.Adapter<WinstreakAdapter.WinViewHolder>() {

    class WinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val winstreak: TextView = itemView.findViewById(R.id.winstreakview)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WinViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: WinViewHolder, position: Int) {
        val winStreakoj = winStreak[position]
        holder.winstreak.text = winStreakoj.winStreak.toString()
    }

    fun refreshData(newWinStreak: List<WinStreak>){
        winStreak = newWinStreak
        notifyDataSetChanged()
    }
}