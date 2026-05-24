package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
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
        val statusLabels = arrayOf(
            getString(R.string.request_status_new),
            getString(R.string.request_status_progress),
            getString(R.string.request_status_done),
            getString(R.string.request_status_rejected)
        )
        val statusValues = arrayOf(
            RequestStatus.NEW,
            RequestStatus.IN_PROGRESS,
            RequestStatus.DONE,
            RequestStatus.REJECTED
        )
        val currentIdx = statusValues.indexOf(request.status)

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.admin_set_status))
            .setSingleChoiceItems(statusLabels, currentIdx) { dialog, which ->
                viewModel.updateRequestStatus(request.id, statusValues[which])
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    companion object {
        fun newInstance() = AdminRequestsFragment()
    }
}
