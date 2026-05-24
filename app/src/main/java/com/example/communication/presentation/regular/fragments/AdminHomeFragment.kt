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
import com.example.communication.presentation.utils.AnimUtils
import com.example.communication.presentation.utils.applyPressScale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AdminHomeFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }
    private var statsAnimated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvRequestCount = view.findViewById<TextView>(R.id.tv_request_count)
        val tvResidentCount = view.findViewById<TextView>(R.id.tv_resident_count)
        val btnEditMessenger = view.findViewById<MaterialButton>(R.id.btn_edit_messenger)

        // Animate title
        AnimUtils.slideDownIn(view.findViewById(R.id.tv_admin_home_title), 0)

        // Card press scale
        view.findViewById<MaterialCardView>(R.id.card_requests_stat).applyPressScale()
        view.findViewById<MaterialCardView>(R.id.card_residents_stat).applyPressScale()

        btnEditMessenger.setOnClickListener { showMessengerSheet() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.requestCount.collect { count ->
                        if (!statsAnimated) {
                            AnimUtils.countUp(tvRequestCount, count, 900)
                        } else {
                            tvRequestCount.text = count.toString()
                        }
                    }
                }
                launch {
                    viewModel.residentCount.collect { count ->
                        if (!statsAnimated) {
                            AnimUtils.countUp(tvResidentCount, count, 900)
                        } else {
                            tvResidentCount.text = count.toString()
                        }
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

        // Stagger stat cards in
        val cardReq = view.findViewById<MaterialCardView>(R.id.card_requests_stat)
        val cardRes = view.findViewById<MaterialCardView>(R.id.card_residents_stat)
        val cardMessenger = view.findViewById<View>(R.id.card_messenger_settings)
        AnimUtils.staggerEnter(listOf(cardReq, cardRes, cardMessenger), delayStep = 90, baseDelay = 150)
        statsAnimated = true
    }

    private fun showMessengerSheet() {
        val sheet = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_messenger_url, null)
        sheet.setContentView(sheetView)

        val etTelegram = sheetView.findViewById<TextInputEditText>(R.id.et_telegram_url)
        val etVk = sheetView.findViewById<TextInputEditText>(R.id.et_vk_url)
        etTelegram.setText(viewModel.telegramUrl.value)
        etVk.setText(viewModel.vkUrl.value)

        sheetView.findViewById<MaterialButton>(R.id.btn_save_url).setOnClickListener {
            val telegram = etTelegram.text?.toString()?.trim() ?: ""
            val vk = etVk.text?.toString()?.trim() ?: ""
            viewModel.saveMessengerUrls(telegram, vk)
            sheet.dismiss()
        }
        sheetView.findViewById<MaterialButton>(R.id.btn_cancel_url).setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    companion object {
        fun newInstance() = AdminHomeFragment()
    }
}
