package com.example.staffdashboard

import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import android.widget.Toast
import com.example.staffdashboard.databinding.FragmentUploadTimeTableBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UploadTimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var uploadTimeTableBinding: FragmentUploadTimeTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        uploadTimeTableBinding = FragmentUploadTimeTableBinding.inflate(inflater, container, false)

        val spinner = uploadTimeTableBinding.spDay

        val items = arrayOf(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Friday",
            "Thursday"
        )
        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner_dropdown_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        val year = uploadTimeTableBinding.spYear
        val item = arrayOf(
            "First Year",
            "Second Year",
            "Third Year"
        )
        val adapter1 = ArrayAdapter(requireContext(), R.layout.custom_spinner_dropdown_item, item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        year.adapter = adapter1

        year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        uploadTimeTableBinding.typePra.setOnClickListener {
            uploadTimeTableBinding.rgBatch.visibility = View.VISIBLE
        }
        uploadTimeTableBinding.typeLec.setOnClickListener {
            uploadTimeTableBinding.rgBatch.visibility = View.GONE
        }

        uploadTimeTableBinding.btnStart.setOnClickListener {
            timePicker()
        }
        uploadTimeTableBinding.btnEnd.setOnClickListener {
            val endTime = timePicker()
            Toast.makeText(requireContext(), endTime,Toast.LENGTH_SHORT).show()
        }
        return uploadTimeTableBinding.root
    }

    private fun timePicker():String {
        val currentTime = Calendar.getInstance()
        var hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            {_,hourOfDay, minute ->
                hour = hourOfDay
//                Toast.makeText(requireContext(), "Selected Time: $hourOfDay:$minute", Toast.LENGTH_SHORT).show()
            },
            hour, minute, false
        )
        timePicker.show()
        return "$hour:$minute"
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadTimeTable().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}