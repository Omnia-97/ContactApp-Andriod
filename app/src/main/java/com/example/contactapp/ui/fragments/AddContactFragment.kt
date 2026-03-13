package com.example.contactapp.ui.fragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.contactapp.R
import com.example.contactapp.data.database.ContactDatabase
import com.example.contactapp.data.model.Contact
import com.example.contactapp.databinding.FragmentAddContactBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class AddContactFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentAddContactBinding
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivContactImage.cancelAnimation()
            binding.ivContactImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivContactImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.etName.addTextChangedListener {
            binding.tvPreviewName.text =
                if (it.isNullOrEmpty()) "User Name" else it.toString()
        }

        binding.etEmail.addTextChangedListener {
            binding.tvPreviewEmail.text =
                if (it.isNullOrEmpty()) "example@email.com" else it.toString()
        }

        binding.etPhone.addTextChangedListener {
            binding.tvPreviewPhone.text =
                if (it.isNullOrEmpty()) "+200000000000" else it.toString()
        }

        binding.ivContactImage.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.btnSave.setOnClickListener {
            saveContact()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )
            bottomSheet?.let {
                it.setBackgroundResource(android.R.color.transparent)
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun saveContact() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            return
        }
        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            return
        }
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone is required"
            return
        }

        val contact = Contact(
            name = name,
            email = email,
            phone = phone,
            imagePath = selectedImageUri?.toString()
        )

        lifecycleScope.launch {
            ContactDatabase.getDatabase(requireContext())
                .contactDao()
                .insertContact(contact)

            Toast.makeText(requireContext(), "Contact Saved!", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}