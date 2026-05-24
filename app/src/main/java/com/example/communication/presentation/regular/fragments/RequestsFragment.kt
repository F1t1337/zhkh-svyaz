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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestCategory
import com.example.communication.data.models.RequestStatus
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.example.communication.presentation.regular.adapters.RequestAdapter
import com.example.communication.presentation.utils.AnimUtils
import com.example.communication.presentation.utils.animateItems
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RequestsFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }
    private lateinit var adapter: RequestAdapter
    private var allRequests: List<Request> = emptyList()
    private var residentId: String = ""
    private var apartmentNumber: String = ""
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_requests, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_requests)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val fab = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_new_request)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val btnArchive = view.findViewById<MaterialButton>(R.id.btn_archive)

        residentId = arguments?.getString(ARG_RESIDENT_ID) ?: return
        apartmentNumber = arguments?.getString(ARG_APARTMENT) ?: ""

        adapter = RequestAdapter(onItemClick = { request -> showRequestDetail(request) })
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        swipeRefresh.setColorSchemeResources(R.color.color_primary)
        swipeRefresh.setOnRefreshListener { viewModel.loadRequests(residentId) }

        btnArchive.setOnClickListener { showArchiveSheet() }

        // FAB spring entrance
        AnimUtils.showFab(fab)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.requests.collect { list ->
                        allRequests = list
                        val active = list.filter {
                            it.status == RequestStatus.NEW || it.status == RequestStatus.IN_PROGRESS
                        }
                        adapter.submitList(active)
                        tvEmpty.visibility = if (active.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility = if (active.isEmpty()) View.GONE else View.VISIBLE
                        if (active.isNotEmpty() && firstLoad) {
                            rv.animateItems()
                            firstLoad = false
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        if (!loading) swipeRefresh.isRefreshing = false
                        progress.visibility = if (loading && adapter.currentList.isEmpty()) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.error.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewModel.loadRequests(residentId)

        fab.setOnClickListener { showNewRequestSheet(residentId) }
    }

    private fun showRequestDetail(request: Request) {
        val categoryStr = when (request.category) {
            RequestCategory.PLUMBING -> getString(R.string.request_category_plumbing)
            RequestCategory.ELECTRICITY -> getString(R.string.request_category_electricity)
            RequestCategory.CLEANING -> getString(R.string.request_category_cleaning)
            RequestCategory.REPAIR -> getString(R.string.request_category_repair)
            RequestCategory.OTHER -> getString(R.string.request_category_other)
        }
        val statusStr = when (request.status) {
            RequestStatus.NEW -> getString(R.string.request_status_new)
            RequestStatus.IN_PROGRESS -> getString(R.string.request_status_progress)
            RequestStatus.DONE -> getString(R.string.request_status_done)
            RequestStatus.REJECTED -> getString(R.string.request_status_rejected)
        }
        val responseText = if (!request.adminResponse.isNullOrBlank()) request.adminResponse
        else getString(R.string.no_admin_response)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.detail_request))
            .setMessage(buildString {
                appendLine("📋 $categoryStr  •  $statusStr")
                appendLine()
                appendLine(request.description)
                appendLine()
                appendLine("Подано: ${request.createdAt.take(10)}")
                appendLine("Срок: ${request.deadline.take(10)}")
                appendLine()
                appendLine(getString(R.string.admin_response_label))
                append(responseText)
            })
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun showArchiveSheet() {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_archive_list, null)
        sheet.setContentView(sheetView)
        sheetView.findViewById<TextView>(R.id.tv_archive_title).text = getString(R.string.archive_requests)

        val archiveAdapter = RequestAdapter(onItemClick = { showRequestDetail(it) })
        val rv = sheetView.findViewById<RecyclerView>(R.id.rv_archive)
        rv.adapter = archiveAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        val tvEmpty = sheetView.findViewById<TextView>(R.id.tv_archive_empty)
        val archived = allRequests.filter { it.status == RequestStatus.DONE || it.status == RequestStatus.REJECTED }
        archiveAdapter.submitList(archived)
        if (archived.isNotEmpty()) rv.animateItems()
        tvEmpty.visibility = if (archived.isEmpty()) View.VISIBLE else View.GONE
        tvEmpty.text = getString(R.string.requests_archive_empty)
        sheet.show()
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

        dropdown.setOnItemClickListener { _, _, position, _ -> selectedCategory = categoryValues[position] }

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
                },
                apartmentNumber = apartmentNumber
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
        const val ARG_APARTMENT = "arg_apartment"
        fun newInstance(residentId: String, apartment: String = "") = RequestsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_RESIDENT_ID, residentId)
                putString(ARG_APARTMENT, apartment)
            }
        }
    }
}
