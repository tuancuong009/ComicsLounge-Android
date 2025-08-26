package com.comics.lounge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.comics.lounge.R
import com.comics.lounge.modals.memberrshipmeta.GiftedMembership

class ShareMembershipAdapter(private val context: Context, private val itemList: List<GiftedMembership>,
                             private val membershipScreenStatus: Int,
                             private val onMenuClick: (Int) -> Unit) : RecyclerView.Adapter<ShareMembershipAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.share_member_ship_item_cell, parent, false))
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val event = itemList[position]
        holder.title.text = event.membershipname
        if (membershipScreenStatus == 0) {
            holder.email.text = event.email
        } else {
            holder.email.text = "Transection Id : "+event.transaction_id
        }
        holder.startDate.text = event.start_date

        if (membershipScreenStatus == 1) {
            holder.llMainLayout.setOnClickListener {
                if (membershipScreenStatus == 1) {
                    onMenuClick.invoke(position)
                }

            }
        }

        if (position % 2 == 1) {
            holder.llMainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.llMainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.contact_us_edt_back_color))
        }
    }

    override fun getItemCount() = itemList.size

    /*fun addClickEvent(  onMenuClick: (Int) -> Unit) {
        onMenuClick.invoke(position)
    }*/

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llMainLayout = itemView.findViewById<LinearLayout>(R.id.llMainLayout)!!
        val title = itemView.findViewById<AppCompatTextView>(R.id.txtMembershipTitle)!!
        val email = itemView.findViewById<AppCompatTextView>(R.id.txtMembershipEmail)!!
        val startDate = itemView.findViewById<AppCompatTextView>(R.id.txtMembershipStartDate)!!

    }
}