package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.communication.R
import com.example.communication.presentation.regular.AdminViewModel
import com.example.communication.presentation.regular.AdminViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SendNotificationFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_send_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etTitle = view.findViewById<TextInputEditText>(R.id.et_title)
        val etBody = view.findViewById<TextInputEditText>(R.id.et_body)
        val acvTarget = view.findViewById<AutoCompleteTextView>(R.id.acv_target)
        val btnSend = view.findViewById<MaterialButton>(R.id.btn_send)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)

        val targets = arrayOf(getString(R.string.admin_recipients_all), getString(R.string.admin_recipients_specific))
        acvTarget.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, targets))
        acvTarget.setText(targets[0], false)

        var targetAll = true
        acvTarget.setOnItemClickListener { _, _, pos, _ -> targetAll = pos == 0 }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { loading ->
                        progress.visibility = if (loading) View.VISIBLE else View.GONE
                        btnSend.isEnabled = !loading
                    }
                }
                launch {
                    viewModel.event.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        etTitle.text?.clear()
                        etBody.text?.clear()
                    }
                }
            }
        }

        val adminId = arguments?.getString(ARG_ADMIN_ID) ?: ""

        btnSend.setOnClickListener {
            val title = etTitle.text?.toString()?.trim() ?: ""
            val body = etBody.text?.toString()?.trim() ?: ""
            if (title.isBlank() || body.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.sendNotification(title, body, targetAll, adminId)
        }
    }

    companion object {
        const val ARG_ADMIN_ID = "arg_admin_id"

        fun newInstance(adminId: String) = SendNotificationFragment().apply {
            arguments = Bundle().apply { putString(ARG_ADMIN_ID, adminId) }
        }
    }
}
