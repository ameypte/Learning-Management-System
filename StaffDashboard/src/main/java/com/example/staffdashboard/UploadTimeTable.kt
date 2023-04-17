package com.example.staffdashboard

import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.staffdashboard.databinding.FragmentUploadTimeTableBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        uploadTimeTableBinding = FragmentUploadTimeTableBinding.inflate(inflater, container, false)

        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name), Context.MODE_PRIVATE
        )
        val spinner = uploadTimeTableBinding.spDay

        val items = arrayOf(
            "Monday", "Tuesday", "Wednesday", "Friday", "Thursday"
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
            "First Year", "Second Year", "Third Year"
        )
        val adapter1 = ArrayAdapter(requireContext(), R.layout.custom_spinner_dropdown_item, item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        year.adapter = adapter1

        year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        val courseSp = uploadTimeTableBinding.spCourse

        val staffDepartment = sharedPreferences.getString("loggedStaffDepartment", "").toString()
        val staffPhone = sharedPreferences.getString("loggedStaffPhone", "").toString()
        val staffName = sharedPreferences.getString("loggedStaffName", "").toString()
        database = FirebaseDatabase.getInstance().getReference("Departments").child(staffDepartment)
            .child("Staff")
        val coursesTeach = database.child(staffPhone).child("coursesTeach")
        coursesTeach.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val courseList = ArrayList<String>()
                    for (course in snapshot.children) {
                        courseList.add(course.key.toString())
                    }
                    val adapter2 = ArrayAdapter(
                        requireContext(), R.layout.custom_spinner_dropdown_item, courseList
                    )
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    courseSp.adapter = adapter2
                    uploadTimeTableBinding.btnAdd.visibility = View.VISIBLE
                    uploadTimeTableBinding.progressBar.visibility = View.GONE
                } else {
                    Toast.makeText(requireContext(), "No courses found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }
        })

        uploadTimeTableBinding.typePra.setOnClickListener {
            uploadTimeTableBinding.tvType2.text = "Practical"
            uploadTimeTableBinding.rgBatch.visibility = View.VISIBLE
            uploadTimeTableBinding.tvBatchName.visibility = View.VISIBLE
        }
        uploadTimeTableBinding.typeLec.setOnClickListener {
            uploadTimeTableBinding.tvType2.text = "Lecture"
            uploadTimeTableBinding.rgBatch.visibility = View.GONE
            uploadTimeTableBinding.tvBatchName.visibility = View.GONE
        }

        uploadTimeTableBinding.batch1.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.text = "IF1"
        }
        uploadTimeTableBinding.batch2.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.text = "IF2"
        }
        uploadTimeTableBinding.batch3.setOnClickListener {
            uploadTimeTableBinding.tvBatchName.text = "IF3"
        }

        uploadTimeTableBinding.btnStart.setOnClickListener {
            timePicker { selectedTime ->
                startTime = selectedTime
                uploadTimeTableBinding.btnStart.text = startTime
            }
        }
        uploadTimeTableBinding.btnEnd.setOnClickListener {
            timePicker { selectedTime ->
                endTime = selectedTime
                uploadTimeTableBinding.btnEnd.text = endTime
            }
        }

        uploadTimeTableBinding.btnAdd.setOnClickListener {
            val selectedTypeId: Int = uploadTimeTableBinding.rgType.checkedRadioButtonId
            if (selectedTypeId == -1 || uploadTimeTableBinding.btnStart.text == "Set" || uploadTimeTableBinding.btnEnd.text == "Set") {
                Toast.makeText(requireContext(), "Please Fill all the details", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            selectedType = requireView().findViewById(selectedTypeId)

            val day = uploadTimeTableBinding.spDay.selectedItem.toString()
            val selectedYear = uploadTimeTableBinding.spYear.selectedItem.toString()
            var type: String = selectedType.text.toString()
            val selectedCourseCode = uploadTimeTableBinding.spCourse.selectedItem.toString()
            var selectedCourseTitle: String = ""
            database =
                FirebaseDatabase.getInstance().getReference("Courses").child(selectedCourseCode)
            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        selectedCourseTitle = snapshot.child("courseTitle").value.toString()
                        database = FirebaseDatabase.getInstance().getReference("Departments")
                            .child(staffDepartment).child(selectedYear).child("Time Table")
                            .child(day)

                        if (type == "Practical") {
                            val selectedBatchId: Int =
                                uploadTimeTableBinding.rgBatch.checkedRadioButtonId
                            if (selectedBatchId == -1) {
                                Toast.makeText(
                                    requireContext(), "Please select th batch", Toast.LENGTH_SHORT
                                ).show()
                                return
                            }
                            selectedBatch = requireView().findViewById(selectedBatchId)
                            batch = selectedBatch.text.toString()
                            type = "Pra"

                            val courseCode = "courseCode" + batch!!.last()
                            val courseTeacher = "courseTeacher" + batch!!.last()
                            val courseTitle = "courseTitle" + batch!!.last()

                            database.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var count = 1
                                    if (snapshot.exists()) {
                                        for (period in snapshot.children) {
                                            if (period.child("startTime").value.toString() == startTime) {
                                                type = period.key.toString()
                                                break
                                            }
                                            count++
                                        }
                                    }
                                    if (type == "Pra") {
                                        type = "$count$type"
                                    }

                                    database.child(type).child("startTime").setValue(startTime)
                                    database.child(type).child("endTime").setValue(endTime)

                                    database.child(type).child(batch!!).child(courseCode)
                                        .setValue(selectedCourseCode)
                                    database.child(type).child(batch!!).child(courseTeacher)
                                        .setValue(staffName)
                                    database.child(type).child(batch!!).child(courseTitle)
                                        .setValue(selectedCourseTitle).addOnSuccessListener {
                                            activity?.supportFragmentManager?.beginTransaction()
                                                ?.replace(
                                                    R.id.dashFrameLayout,
                                                    TimeTable.newInstance("", "")
                                                )?.commit()
                                            Toast.makeText(
                                                requireContext(),
                                                "Practical Added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(), error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            batch = ""
                            type = "Lec"

                            database.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var count = 1
                                    if (snapshot.exists()) {
                                        for (period in snapshot.children) {
                                            if (period.child("startTime").value.toString() == startTime) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Time slot already occupied",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                return
                                            }
                                            count++
                                        }
                                    }
                                    type = "$count$type"

                                    database.child(type).child("startTime").setValue(startTime)
                                    database.child(type).child("endTime").setValue(endTime)
                                    database.child(type).child("courseCode")
                                        .setValue(selectedCourseCode)
                                    database.child(type).child("courseTitle")
                                        .setValue(selectedCourseTitle).addOnSuccessListener {
                                            activity?.supportFragmentManager?.beginTransaction()
                                                ?.replace(
                                                    R.id.dashFrameLayout,
                                                    TimeTable.newInstance("", "")
                                                )?.commit()
                                            Toast.makeText(
                                                requireContext(),
                                                "Lecture Added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(
                                        requireContext(), error.message, Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                }
            })

        }
        return uploadTimeTableBinding.root
    }

    private fun timePicker(callback: (String) -> Unit) {
        val currentTime = Calendar.getInstance()
        var hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(), { _, hourOfDay, minute ->
                hour = hourOfDay
                //the selected time must be between 8:00 AM to 6:00 PM
                if (hour < 8 || hour > 18) {
                    Toast.makeText(
                        requireContext(),
                        "Please select time between 8:00 AM to 6:00 PM",
                        Toast.LENGTH_SHORT
                    ).show()
                    uploadTimeTableBinding.btnStart.text = "Set"
                    uploadTimeTableBinding.btnEnd.text = "Set"
                    return@TimePickerDialog
                }
                val selectedTime = "$hourOfDay:$minute"
                callback(selectedTime)
            }, hour, minute, true
        )
        timePicker.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = UploadTimeTable().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}