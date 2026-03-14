package com.example.contactapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.contactapp.adapter.ContactsAdapter
import com.example.contactapp.data.database.ContactDatabase
import com.example.contactapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ContactsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeContacts()

        binding.fabAdd.setOnClickListener {
            AddContactFragment().show(parentFragmentManager, "AddContactFragment")
        }
        binding.fabDelete.setOnClickListener {
            lifecycleScope.launch {
                val dao = ContactDatabase.getDatabase(requireContext()).contactDao()
                dao.getAllContacts().first().lastOrNull()?.let { contact ->
                    dao.deleteContact(contact)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ContactsAdapter { contact ->
            lifecycleScope.launch {
                ContactDatabase.getDatabase(requireContext())
                    .contactDao()
                    .deleteContact(contact)
            }
        }
        binding.rvContacts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvContacts.adapter = adapter
    }

    private fun observeContacts() {
        val dao = ContactDatabase.getDatabase(requireContext()).contactDao()
        lifecycleScope.launch {
            dao.getAllContacts().collect { contacts ->
                if (contacts.isEmpty()) {
                    binding.lottieEmpty.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.rvContacts.visibility = View.GONE
                    binding.fabAdd.visibility = View.VISIBLE
                    binding.fabDelete.visibility = View.GONE
                } else {
                    binding.lottieEmpty.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvContacts.visibility = View.VISIBLE
                    adapter.submitList(contacts)
                    if (contacts.isEmpty()) {
                        binding.fabAdd.visibility = View.VISIBLE
                        binding.fabDelete.visibility = View.GONE
                    } else if (contacts.size >= 6) {
                        binding.fabAdd.visibility = View.GONE
                        binding.fabDelete.visibility = View.VISIBLE
                    } else {
                        binding.fabAdd.visibility = View.VISIBLE
                        binding.fabDelete.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}