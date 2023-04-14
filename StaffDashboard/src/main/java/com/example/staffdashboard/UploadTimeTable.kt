package com.example.staffdashboard

import android.app.TimePickerDialog
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.staffdashboard.databinding.FragmentUploadTimeTableBinding
import com.google.firebase.database.DatabaseReference

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UploadTimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var batch: String? = null
    private lateinit var uploadTimeTableBinding: FragmentUploadTimeTableBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var selectedType: RadioButton
    private lateinit var selectedBatch: RadioButton
    private lateinit var startTime: String
    private lateinit var endTime: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,container: ViewGroup?,
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
            uploadTimeTableBinding.tvType2.setText("Practical")
            uploadTimeTableBinding.rgBatch.visibility = View.VISIBLE
            uploadTimeTableBinding.tvBatchName.visibility = View.VISIBLE
        }
        uploadTimeTableBinding.typeLec.setOnClickListener {
            uploadTimeTableBinding.tvType2.setText("Lecture")
            uploadTimeTableBinding.rgBatch.visibility = View.GONE
            uploadTimeTableBinding.tvBatchName.visibility = View.GONE
        }

        uploadTimeTableBinding.batch1.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.setText("IF1")
        }
        uploadTimeTableBinding.batch2.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.setText("IF2")
        }
        uploadTimeTableBinding.batch2.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.setText("IF3")
        }

        uploadTimeTableBinding.btnStart.setOnClickListener {
            timePicker{selectedTime ->
                startTime = selectedTime
            }
        }
        uploadTimeTableBinding.btnEnd.setOnClickListener {
            timePicker { selectedTime ->
                endTime = selectedTime
            }
        }

        uploadTimeTableBinding.btnAdd.setOnClickListener {
            val selectedTypeId: Int = uploadTimeTableBinding.rgType.checkedRadioButtonId
            selectedType = requireView().findViewById(selectedTypeId)

            val day = uploadTimeTableBinding.spDay.selectedItem.toString()
            val year = uploadTimeTableBinding.spYear.selectedItem.toString()
            val type: String = selectedType.text.toString()

            if (type == "Practical") {
                val selectedBatchId: Int = uploadTimeTableBinding.rgBatch.checkedRadioButtonId
                selectedBatch = requireView().findViewById(selectedBatchId)
                batch = selectedBatch.text.toString()
            } else{
                batch =""
            }

//            val name = sharedPreferences.getString("loggedStaffDepartment", "Name").toString()

            Toast.makeText(requireContext(),"$type $day $year\n $batch $startTime $endTime",Toast.LENGTH_SHORT).show()
        }
        return uploadTimeTableBinding.root
    }

    private fun timePicker(callback: (String) -> Unit) {
        val currentTime = Calendar.getInstance()
        var hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(),
            {_,hourOfDay, minute ->
                hour = hourOfDay
                val selectedTime = "$hourOfDay:$minute"
                callback(selectedTime)
            },
            hour, minute, false
        )
        timePicker.show()
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