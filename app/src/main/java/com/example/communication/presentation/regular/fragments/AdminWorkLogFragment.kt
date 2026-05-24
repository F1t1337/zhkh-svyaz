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
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.presentation.regular.AdminViewModel
import com.example.communication.presentation.regular.AdminViewModelFactory
import com.example.communication.presentation.regular.adapters.WorkLogAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminWorkLogFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }
    private val adapter = WorkLogAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_work_log, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_work_log)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add_entry)

        rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.workLog.collect { list ->
                        adapter.submitList(list)
                        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        progress.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.event.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val adminId = arguments?.getString(ARG_ADMIN_ID) ?: ""
        viewModel.loadWorkLog()

        fab.setOnClickListener { showAddEntrySheet(adminId) }
    }

    private fun showAddEntrySheet(adminId: String) {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_add_work_log, null)
        sheet.setContentView(sheetView)

        val workTypes = resources.getStringArray(R.array.work_log_types)
        val acv = sheetView.findViewById<AutoCompleteTextView>(R.id.acv_work_type)
        acv.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, workTypes))
        var selectedType = workTypes.firstOrNull() ?: ""
        acv.setText(selectedType, false)
        acv.setOnItemClickListener { _, _, pos, _ -> selectedType = workTypes[pos] }

        sheetView.findViewById<MaterialButton>(R.id.btn_submit).setOnClickListener {
            val desc = sheetView.findViewById<TextInputEditText>(R.id.et_description).text?.toString()?.trim() ?: ""
            val location = sheetView.findViewById<TextInputEditText>(R.id.et_location).text?.toString()?.trim() ?: ""
            if (desc.isBlank() || location.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addWorkLogEntry(selectedType, desc, location, adminId)
            sheet.dismiss()
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    companion object {
        const val ARG_ADMIN_ID = "arg_admin_id"

        fun newInstance(adminId: String) = AdminWorkLogFragment().apply {
            arguments = Bundle().apply { putString(ARG_ADMIN_ID, adminId) }
        }
    }
}
