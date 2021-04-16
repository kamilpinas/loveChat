package com.example.loveChat

import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.example_item.view.*


class ChatAdapter(val exampleItems: ArrayList<ChatItem>) : RecyclerView.Adapter<MessageViewHolder>() {

    private var removedPosition:Int =0
    private lateinit var removedItem:ChatItem

    override fun getItemCount(): Int {
        return exampleItems.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val v = layoutInflater.inflate(R.layout.example_item, parent, false)
        return MessageViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MessageViewHolder,  position:Int) {

        val textLogin = exampleItems.get(holder.adapterPosition).login
        val textData = exampleItems.get(holder.adapterPosition).date
        val textContent = exampleItems.get(holder.adapterPosition).content
        val textId = exampleItems.get(holder.adapterPosition).id

        holder.view.textLogin.text = textLogin
        holder.view.textDate.text = textData
        holder.view.textContent.text = textContent

        holder.itemView.setOnClickListener {
            println("ID: $textId")

            val intent = Intent(holder.view.context, EditActivity::class.java)
            intent.putExtra("LOGIN_KEY", textLogin)
            intent.putExtra("CONTENT_KEY", textContent)
            intent.putExtra("DATE_KEY", textData)
            intent.putExtra("ID_KEY", textId)

            holder.view.context.startActivity(intent)

        }
    }
    fun removeItem(position: Int) {
        exampleItems.removeAt(position)
        notifyItemRemoved(position)
    }
    fun getItem(position: Int): ChatItem {
        return exampleItems[position]
    }
}

class MessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
}
