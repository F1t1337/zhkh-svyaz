package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ProgressBar
import android.widget.TextView
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
    private var selectedApartments = mutableListOf<String>()
    private var targetAll = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_send_notification, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etTitle = view.findViewById<TextInputEditText>(R.id.et_title)
        val etBody = view.findViewById<TextInputEditText>(R.id.et_body)
        val acvTarget = view.findViewById<AutoCompleteTextView>(R.id.acv_target)
        val btnSend = view.findViewById<MaterialButton>(R.id.btn_send)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val btnSelectApts = view.findViewById<MaterialButton>(R.id.btn_select_apartments)
        val tvSelectedApts = view.findViewById<TextView>(R.id.tv_selected_apartments)

        val targets = arrayOf(getString(R.string.admin_recipients_all), getString(R.string.admin_recipients_specific))
        acvTarget.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, targets))
        acvTarget.setText(targets[0], false)

        acvTarget.setOnItemClickListener { _, _, pos, _ ->
            targetAll = pos == 0
            if (targetAll) {
                btnSelectApts.visibility = View.GONE
                tvSelectedApts.visibility = View.GONE
                selectedApartments.clear()
            } else {
                btnSelectApts.visibility = View.VISIBLE
                tvSelectedApts.visibility = View.VISIBLE
                updateSelectedAptLabel(tvSelectedApts)
            }
        }

        btnSelectApts.setOnClickListener { showApartmentPicker(tvSelectedApts) }

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
                        selectedApartments.clear()
                        acvTarget.setText(targets[0], false)
                        targetAll = true
                        btnSelectApts.visibility = View.GONE
                        tvSelectedApts.visibility = View.GONE
                    }
                }
            }
        }

        // Load apartments for picker
        viewModel.loadApartments()

        val adminId = arguments?.getString(ARG_ADMIN_ID) ?: ""

        btnSend.setOnClickListener {
            val title = etTitle.text?.toString()?.trim() ?: ""
            val body = etBody.text?.toString()?.trim() ?: ""
            if (title.isBlank() || body.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!targetAll && selectedApartments.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_select_apartments, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.sendNotification(title, body, targetAll, adminId, selectedApartments.toList())
        }
    }

    private fun showApartmentPicker(tvSelectedApts: TextView) {
        val apartments = viewModel.apartments.value
        if (apartments.isEmpty()) {
            Toast.makeText(requireContext(), "Нет данных о квартирах", Toast.LENGTH_SHORT).show()
            return
        }
        val aptNumbers = apartments.map { it.first }.toTypedArray()
        val checkedItems = BooleanArray(aptNumbers.size) { selectedApartments.contains(aptNumbers[it]) }

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_apartments))
            .setMultiChoiceItems(aptNumbers, checkedItems) { _, which, isChecked ->
                if (isChecked) selectedApartments.add(aptNumbers[which])
                else selectedApartments.remove(aptNumbers[which])
            }
            .setPositiveButton(getString(R.string.save)) { _, _ -> updateSelectedAptLabel(tvSelectedApts) }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun updateSelectedAptLabel(tvSelectedApts: TextView) {
        tvSelectedApts.text = if (selectedApartments.isEmpty()) {
            getString(R.string.no_apartments_selected)
        } else {
            getString(R.string.selected_apartments, selectedApartments.sorted().joinToString(", кв. ", "кв. "))
        }
    }

    companion object {
        const val ARG_ADMIN_ID = "arg_admin_id"

        fun newInstance(adminId: String) = SendNotificationFragment().apply {
            arguments = Bundle().apply { putString(ARG_ADMIN_ID, adminId) }
        }
    }
}
