package com.example.communication.presentation.regular.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminHomeFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvRequestCount = view.findViewById<TextView>(R.id.tv_request_count)
        val tvResidentCount = view.findViewById<TextView>(R.id.tv_resident_count)
        val btnEditMessenger = view.findViewById<MaterialButton>(R.id.btn_edit_messenger)

        btnEditMessenger.setOnClickListener { showMessengerSheet() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.requestCount.collect { count ->
                        tvRequestCount.text = count.toString()
                    }
                }
                launch {
                    viewModel.residentCount.collect { count ->
                        tvResidentCount.text = count.toString()
                    }
                }
                launch {
                    viewModel.event.collect { msg ->
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewModel.loadStats()
    }

    private fun showMessengerSheet() {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_messenger_url, null)
        sheet.setContentView(sheetView)

        val etUrl = sheetView.findViewById<TextInputEditText>(R.id.et_messenger_url)
        etUrl.setText(viewModel.messengerUrl.value)

        sheetView.findViewById<MaterialButton>(R.id.btn_save_url).setOnClickListener {
            val url = etUrl.text?.toString()?.trim() ?: ""
            viewModel.saveMessengerUrl(url)
            sheet.dismiss()
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_cancel_url).setOnClickListener {
            sheet.dismiss()
        }

        sheet.show()
    }

    companion object {
        fun newInstance() = AdminHomeFragment()
    }
}
