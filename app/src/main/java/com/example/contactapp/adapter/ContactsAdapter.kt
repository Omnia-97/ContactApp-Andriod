package com.example.contactapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.data.model.Contact
import com.example.contactapp.databinding.ItemContactBinding
import java.io.File

class ContactsAdapter(
    private val onDeleteClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactsAdapter.ContactViewHolder>(DiffCallback()) {

    inner class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.tvName.text = contact.name
            binding.tvEmail.text = contact.email
            binding.tvPhone.text = contact.phone

            if (!contact.imagePath.isNullOrEmpty()) {
                val file = File(contact.imagePath)
                if (file.exists()) {
                    binding.ivContact.setImageBitmap(
                        BitmapFactory.decodeFile(contact.imagePath)
                    )
                }
            } else {
                binding.ivContact.setImageResource(R.drawable.ic_plus)
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