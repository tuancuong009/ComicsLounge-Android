package com.comics.lounge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.comics.lounge.R
import com.comics.lounge.modals.Event

class HomeEventAdapter(private val itemList: List<Event>) : RecyclerView.Adapter<HomeEventAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.item_event, parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val event = itemList[position]
        holder.eventName.text = event.productName
    }

    override fun getItemCount() = itemList.size

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventImage = itemView.findViewById<AppCompatImageView>(R.id.event_imageview)!!
        val eventName = itemView.findViewById<AppCompatTextView>(R.id.event_name_txt)!!

        init {
            eventImage.clipToOutline = true
        }
    }
}