package com.xabber.presentation.application.fragments.contacts

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xabber.R
import com.xabber.databinding.FragmentContactBinding
import com.xabber.presentation.application.contract.applicationToolbarChanger
import com.xabber.presentation.application.contract.navigator
import com.xabber.presentation.application.fragments.BaseFragment

class ContactsFragment : BaseFragment(R.layout.fragment_contact), ContactAdapter.Listener {
    private val binding by viewBinding(FragmentContactBinding::bind)
    private val viewModel = ContactsViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contactAdapter = ContactAdapter(this)

        binding.recyclerView.adapter = contactAdapter

        viewModel.contacts.observe(viewLifecycleOwner) {
            contactAdapter.submitList(it)
        }
    }

    override fun onAvatarClick() {
        navigator().showAccount()
    }

    override fun onContactClick() {
        navigator().showMessage("")
    }

    override fun editContact() {
        navigator().showEditContact("")
    }

    override fun deleteContact() {
        val alertDialog = AlertDialog.Builder(context)
        alertDialog.setTitle("Delete contact?")
        alertDialog.setMessage("Are you sure you want to delete the contact?")
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()

        }

        alertDialog.setPositiveButton("Delete") { dialog, _ ->
            dialog.dismiss()
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setView(R.layout.dialog_block_contact)
        }
        alertDialog.show()
    }

    override fun blockContact() {

    }

}
