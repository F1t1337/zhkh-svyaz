package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.communication.R
import com.example.communication.data.models.Request
import com.example.communication.data.models.RequestStatus
import com.example.communication.presentation.regular.AdminViewModel
import com.example.communication.presentation.regular.AdminViewModelFactory
import com.example.communication.presentation.regular.adapters.AdminRequestAdapter
import kotlinx.coroutines.launch

class AdminRequestsFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }
    private lateinit var adapter: AdminRequestAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_requests, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_requests)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)

        adapter = AdminRequestAdapter(onChangeStatus = { request -> showStatusDialog(request) })
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

        viewModel.loadAllRequests()
    }

    private fun showStatusDialog(request: Request) {
        // State pattern: показываем только допустимые переходы
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
