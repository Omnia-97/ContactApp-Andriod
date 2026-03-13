package com.example.contactapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.data.model.Contact
import com.example.contactapp.databinding.ItemContactBinding

class ContactsAdapter(
    private val onDeleteClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(DiffCallback()) {

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            binding.tvName.text = contact.name
            binding.tvEmail.text = contact.email
            binding.tvPhone.text = contact.phone

            if (contact.imagePath != null) {
                binding.ivContact.setImageURI(android.net.Uri.parse(contact.imagePath))
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(contact)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact) =
            oldItem == newItem
    }
}