package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.communication.R
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestCategory
import com.example.communication.data.models.RequestStatus
import com.example.communication.presentation.regular.AdminViewModel
import com.example.communication.presentation.regular.AdminViewModelFactory
import com.example.communication.presentation.regular.adapters.AdminRequestAdapter
import com.example.communication.presentation.utils.animateItems
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminRequestsFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }
    private lateinit var adapter: AdminRequestAdapter
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_requests, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_requests)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        adapter = AdminRequestAdapter(
            onChangeStatus = { request -> showStatusDialog(request) },
            onItemClick = { request -> showRequestDetailSheet(request) }
        )
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        swipeRefresh.setColorSchemeResources(R.color.color_primary)
        swipeRefresh.setOnRefreshListener { viewModel.loadAllRequests() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.requests.collect { list ->
                        adapter.submitList(list)
                        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
                        if (list.isNotEmpty() && firstLoad) {
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
                    viewModel.event.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.loadAllRequests()
    }

    private fun showRequestDetailSheet(request: Request) {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_admin_reply, null)
        sheet.setContentView(sheetView)

        val categoryStr = when (request.category) {
            RequestCategory.PLUMBING -> getString(R.string.request_category_plumbing)
            RequestCategory.ELECTRICITY -> getString(R.string.request_category_electricity)
            RequestCategory.CLEANING -> getString(R.string.request_category_cleaning)
            RequestCategory.REPAIR -> getString(R.string.request_category_repair)
            RequestCategory.OTHER -> getString(R.string.request_category_other)
        }

        sheetView.findViewById<TextView>(R.id.tv_sheet_category).text = categoryStr
        sheetView.findViewById<TextView>(R.id.tv_sheet_description).text = request.description
        sheetView.findViewById<TextView>(R.id.tv_sheet_resident).text = "Жилец: ${request.residentId}"
        sheetView.findViewById<TextView>(R.id.tv_sheet_date).text = "Дата: ${request.createdAt.take(10)}"

        val etReply = sheetView.findViewById<TextInputEditText>(R.id.et_reply)
        if (!request.adminResponse.isNullOrBlank()) {
            etReply.setText(request.adminResponse)
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_send_reply).setOnClickListener {
            val reply = etReply.text?.toString()?.trim() ?: ""
            if (reply.isBlank()) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.replyToRequest(request.id, reply)
            sheet.dismiss()
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_change_status_sheet).setOnClickListener {
            sheet.dismiss()
            showStatusDialog(request)
        }

        sheet.show()
    }

    private fun showStatusDialog(request: Request) {
        val available = viewModel.availableStatuses(request.status).toList()
        if (available.isEmpty()) {
            Toast.makeText(requireContext(), "Статус нельзя изменить", Toast.LENGTH_SHORT).show()
            return
        }
        val labels = available.map { status ->
            when (status) {
                RequestStatus.NEW         -> getString(R.string.request_status_new)
                RequestStatus.IN_PROGRESS -> getString(R.string.request_status_progress)
                RequestStatus.DONE        -> getString(R.string.request_status_done)
                RequestStatus.REJECTED    -> getString(R.string.request_status_rejected)
            }
        }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.admin_set_status))
            .setItems(labels) { _, which ->
                viewModel.updateRequestStatus(request.id, available[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        fun newInstance() = AdminRequestsFragment()
    }
}
