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
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestCategory
import com.example.communication.data.models.RequestStatus
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.example.communication.presentation.regular.adapters.RequestAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RequestsFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }
    private val adapter = RequestAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_requests, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_requests)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_new_request)

        rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.requests.collect { list ->
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
            }
        }

        val residentId = arguments?.getString(ARG_RESIDENT_ID) ?: return
        viewModel.loadRequests(residentId)

        fab.setOnClickListener { showNewRequestSheet(residentId) }
    }

    private fun showNewRequestSheet(residentId: String) {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_new_request, null)
        sheet.setContentView(sheetView)

        val categories = resources.getStringArray(R.array.request_categories)
        val categoryValues = RequestCategory.values()
        val dropdown = sheetView.findViewById<AutoCompleteTextView>(R.id.dropdown_category)
        dropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories))

        var selectedCategory = RequestCategory.OTHER

        dropdown.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = categoryValues[position]
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_submit).setOnClickListener {
            val desc = sheetView.findViewById<TextInputEditText>(R.id.et_description).text?.toString()?.trim() ?: ""
            if (desc.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val request = Request(
                id = System.currentTimeMillis().toString(),
                residentId = residentId,
                category = selectedCategory,
                description = desc,
                attachments = emptyList(),
                status = RequestStatus.NEW,
                createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()),
                deadline = run {
                    val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 7) }
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(cal.time)
                }
            )
            viewModel.submitRequest(request, residentId)
            Toast.makeText(requireContext(), R.string.request_submitted, Toast.LENGTH_SHORT).show()
            sheet.dismiss()
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    companion object {
        const val ARG_RESIDENT_ID = "arg_resident_id"

        fun newInstance(residentId: String) = RequestsFragment().apply {
            arguments = Bundle().apply { putString(ARG_RESIDENT_ID, residentId) }
        }
    }
}
