package com.example.communication.presentation.regular.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.communication.R
import com.example.communication.presentation.regular.ResidentViewModel
import com.example.communication.presentation.regular.ResidentViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private val viewModel: ResidentViewModel by activityViewModels { ResidentViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val apartment = arguments?.getString(ARG_APARTMENT) ?: ""
        val entrance = arguments?.getString(ARG_ENTRANCE) ?: ""

        view.findViewById<TextView>(R.id.tv_apartment).text =
            getString(R.string.home_subtitle, apartment, entrance)

        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        view.findViewById<View>(R.id.card_receipts).setOnClickListener {
            nav.selectedItemId = R.id.nav_receipts
        }
        view.findViewById<View>(R.id.card_requests).setOnClickListener {
            nav.selectedItemId = R.id.nav_requests
        }
        view.findViewById<View>(R.id.card_notifications).setOnClickListener {
            nav.selectedItemId = R.id.nav_notifications
        }
        view.findViewById<View>(R.id.card_work_log).setOnClickListener {
            nav.selectedItemId = R.id.nav_work_log
        }
        view.findViewById<View>(R.id.card_messenger).setOnClickListener {
            showMessengerDialog()
        }
    }

    private fun showMessengerDialog() {
        val ctx = requireContext()
        val options = arrayOf(getString(R.string.messenger_telegram), getString(R.string.messenger_vk))
        AlertDialog.Builder(ctx)
            .setTitle(getString(R.string.messenger_title))
            .setItems(options) { _, which ->
                val uri = if (which == 0) Uri.parse("tg://") else Uri.parse("vk://")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                if (intent.resolveActivity(ctx.packageManager) != null) {
                    startActivity(intent)
                } else {
                    Toast.makeText(ctx, R.string.messenger_not_installed, Toast.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    companion object {
        const val ARG_APARTMENT = "arg_apartment"
        const val ARG_ENTRANCE = "arg_entrance"

        fun newInstance(apartment: String, entrance: String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_APARTMENT, apartment)
                putString(ARG_ENTRANCE, entrance)
            }
        }
    }
}
