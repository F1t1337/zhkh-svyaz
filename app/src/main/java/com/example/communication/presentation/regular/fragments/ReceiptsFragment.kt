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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.communication.R
import com.example.communication.data.models.Receipt
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.example.communication.presentation.regular.adapters.ReceiptAdapter
import com.example.communication.presentation.utils.AnimUtils
import com.example.communication.presentation.utils.animateItems
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class ReceiptsFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }
    private lateinit var adapter: ReceiptAdapter
    private var allReceipts: List<Receipt> = emptyList()
    private var residentId: String = ""
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_receipts, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv = view.findViewById<RecyclerView>(R.id.rv_receipts)
        val tvEmpty = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)
        val btnArchive = view.findViewById<MaterialButton>(R.id.btn_archive)

        residentId = arguments?.getString(ARG_RESIDENT_ID) ?: return

        adapter = ReceiptAdapter(onItemClick = { receipt -> showReceiptDetail(receipt) })
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        swipeRefresh.setColorSchemeResources(R.color.color_primary)
        swipeRefresh.setOnRefreshListener { viewModel.loadReceipts(residentId) }

        btnArchive.setOnClickListener { showArchiveSheet() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.receipts.collect { list ->
                        allReceipts = list
                        val latest = list.maxByOrNull { it.period }?.let { listOf(it) } ?: emptyList()
                        adapter.submitList(latest)
                        tvEmpty.visibility = if (latest.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility = if (latest.isEmpty()) View.GONE else View.VISIBLE
                        if (latest.isNotEmpty() && firstLoad) {
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
            }
        }

        viewModel.loadReceipts(residentId)
    }

    private fun showReceiptDetail(receipt: Receipt) {
        if (!receipt.isRead) viewModel.markReceiptRead(receipt.id, residentId)
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.detail_receipt))
            .setMessage(buildString {
                appendLine("📅 ${receipt.period}")
                appendLine()
                appendLine("Холодная вода:   %.2f ₽".format(receipt.coldWater))
                appendLine("Горячая вода:    %.2f ₽".format(receipt.hotWater))
                appendLine("Электроэнергия:  %.2f ₽".format(receipt.electricity))
                appendLine("Газ:             %.2f ₽".format(receipt.gas))
                appendLine()
                append("Итого: %.2f ₽".format(receipt.totalAmount))
            })
            .setPositiveButton(R.string.close, null)
            .show()
    }

    private fun showArchiveSheet() {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_archive_list, null)
        sheet.setContentView(sheetView)
        sheetView.findViewById<TextView>(R.id.tv_archive_title).text = getString(R.string.archive_receipts)

        val archiveAdapter = ReceiptAdapter(onItemClick = { receipt ->
            showReceiptDetail(receipt)
            sheet.dismiss()
        })
        val rv = sheetView.findViewById<RecyclerView>(R.id.rv_archive)
        rv.adapter = archiveAdapter
        rv.layoutManager = LinearLayoutManager(requireContext())

        val tvEmpty = sheetView.findViewById<TextView>(R.id.tv_archive_empty)
        val latest = allReceipts.maxByOrNull { it.period }
        val archive = allReceipts.filter { it != latest }.sortedByDescending { it.period }
        archiveAdapter.submitList(archive)
        if (archive.isNotEmpty()) rv.animateItems()
        tvEmpty.visibility = if (archive.isEmpty()) View.VISIBLE else View.GONE
        tvEmpty.text = getString(R.string.receipts_archive_empty)
        sheet.show()
    }

    companion object {
        const val ARG_RESIDENT_ID = "arg_resident_id"
        fun newInstance(residentId: String) = ReceiptsFragment().apply {
            arguments = Bundle().apply { putString(ARG_RESIDENT_ID, residentId) }
        }
    }
}
