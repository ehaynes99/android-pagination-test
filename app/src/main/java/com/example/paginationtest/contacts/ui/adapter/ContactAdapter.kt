package com.example.paginationtest.contacts.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.paginationtest.contacts.data.Contact

class ContactAdapter(
    private val onContactClick: (Contact) -> Unit
) : PagingDataAdapter<Contact, ContactAdapter.ContactViewHolder>(CONTACT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ContactViewHolder(view, onContactClick)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        if (contact != null) {
            holder.bind(contact)
        }
    }

    class ContactViewHolder(
        itemView: View,
        private val onContactClick: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(android.R.id.text1)
        private val companyTextView: TextView = itemView.findViewById(android.R.id.text2)
        
        private var currentContact: Contact? = null

        init {
            itemView.setOnClickListener {
                currentContact?.let { contact ->
                    onContactClick(contact)
                }
            }
        }

        fun bind(contact: Contact) {
            currentContact = contact
            nameTextView.text = "${contact.firstName} ${contact.lastName}"
            companyTextView.text = contact.company
        }
    }

    companion object {
        private val CONTACT_COMPARATOR = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem == newItem
            }
        }
    }
}