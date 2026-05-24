package com.example.communication.presentation.regular.fragments

import android.app.DatePickerDialog
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
import com.example.communication.data.models.Service
import com.example.communication.data.models.ServiceStatus
import com.example.communication.presentation.regular.AdminViewModel
import com.example.communication.presentation.regular.AdminViewModelFactory
import com.example.communication.presentation.regular.adapters.ServiceAdapter
import com.example.communication.presentation.utils.animateItems
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import java.util.Calendar

class ServicesFragment : Fragment() {

    private val viewModel: AdminViewModel by activityViewModels { AdminViewModelFactory() }
    private val adapter = ServiceAdapter()
    private var firstLoad = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_services, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rv       = view.findViewById<RecyclerView>(R.id.rv_services)
        val tvEmpty  = view.findViewById<TextView>(R.id.tv_empty)
        val progress = view.findViewById<ProgressBar>(R.id.progress_bar)
        val fab      = view.findViewById<ExtendedFloatingActionButton>(R.id.fab_add_service)
        val swipe    = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh)

        rv.adapter = adapter
        swipe.setColorSchemeResources(R.color.color_primary)
        swipe.setOnRefreshListener { viewModel.loadServices() }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.services.collect { list ->
                        adapter.submitList(list)
                        tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        rv.visibility      = if (list.isEmpty()) View.GONE  else View.VISIBLE
                        if (list.isNotEmpty() && firstLoad) {
                            rv.animateItems()
                            firstLoad = false
                        }
                    }
                }
                launch {
                    viewModel.isLoading.collect { loading ->
                        if (!loading) swipe.isRefreshing = false
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

        viewModel.loadServices()
        viewModel.loadApartments()

        fab.setOnClickListener { showCreateServiceSheet() }
    }

    private fun showCreateServiceSheet() {
        val sheet     = BottomSheetDialog(requireContext())
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_assign_service, null)
        sheet.setContentView(sheetView)

        // 1. Service type dropdown
        val serviceTypes = resources.getStringArray(R.array.service_types)
        val acvType = sheetView.findViewById<AutoCompleteTextView>(R.id.acv_service_type)
        acvType.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, serviceTypes)
        )
        acvType.setText(serviceTypes[0], false)
        var selectedType = serviceTypes[0]
        acvType.setOnItemClickListener { _, _, pos, _ -> selectedType = serviceTypes[pos] }

        // 2. Apartment dropdown
        val apartments  = viewModel.apartments.value
        val aptLabels   = apartments.map { "Кв. ${it.first}" }.toTypedArray()
        val acvApt      = sheetView.findViewById<AutoCompleteTextView>(R.id.acv_apartment)
        acvApt.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, aptLabels)
        )
        var selectedResidentId  = ""
        var selectedApartment   = ""
        acvApt.setOnItemClickListener { _, _, pos, _ ->
            selectedResidentId = apartments[pos].second
            selectedApartment  = apartments[pos].first
        }

        // 3. Date picker
        val etDate         = sheetView.findViewById<TextInputEditText>(R.id.et_scheduled_date)
        var selectedDateIso = ""
        etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    selectedDateIso = "%04d-%02d-%02dT09:00:00".format(year, month + 1, day)
                    etDate.setText("%02d.%02d.%04d".format(day, month + 1, year))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // 4. Description (optional)
        val etDesc = sheetView.findViewById<TextInputEditText>(R.id.et_service_description)

        sheetView.findViewById<MaterialButton>(R.id.btn_assign).setOnClickListener {
            val aptText = acvApt.text?.toString()?.trim() ?: ""
            when {
                aptText.isBlank() || selectedResidentId.isBlank() -> {
                    Toast.makeText(requireContext(), R.string.error_select_apartment, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                selectedDateIso.isBlank() -> {
                    Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else -> {
                    val service = Service(
                        id              = System.currentTimeMillis().toString(),
                        serviceType     = selectedType,
                        description     = etDesc.text?.toString()?.trim() ?: "",
                        scheduledAt     = selectedDateIso,
                        residentId      = selectedResidentId,
                        apartmentNumber = selectedApartment,
                        status          = ServiceStatus.SCHEDULED
                    )
                    viewModel.assignService(service)
                    sheet.dismiss()
                }
            }
        }

        sheetView.findViewById<MaterialButton>(R.id.btn_cancel).setOnClickListener { sheet.dismiss() }
        sheet.show()
    }

    companion object {
        fun newInstance() = ServicesFragment()
    }
}
