package com.example.staffdashboard.timetable

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.ModelTimeTable
import com.example.staffdashboard.R
import com.example.staffdashboard.databinding.FragmentTimeTableBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class TimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loggedUserDept: String
    private lateinit var selectedYear: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    private lateinit var timeTableRecyclerView: RecyclerView
    private lateinit var itemList: ArrayList<ModelTimeTable>
    private lateinit var selectedDay: String
    private lateinit var timeTableBinding: FragmentTimeTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        timeTableBinding = FragmentTimeTableBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedUserDept = sharedPreferences.getString("loggedStaffDepartment", null).toString()

        val spinner = timeTableBinding.spDay

        val items = arrayOf(
            "Select Day",
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
                selectedDay = p0?.getItemAtPosition(p2).toString()
                timeTableBinding.timeTableList.visibility = View.GONE
                getData()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        val year = timeTableBinding.spYear
        val yearItems = arrayOf(
            "First Year",
            "Second Year",
            "Third Year",
        )
        val yearAdapter =
            ArrayAdapter(requireContext(), R.layout.custom_spinner_dropdown_item, yearItems)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        year.adapter = yearAdapter

        year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedYear = year.selectedItem.toString()
                timeTableBinding.spDay.setSelection(0)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        timeTableBinding.btnUpload.setOnClickListener {
            replaceFragment(UploadTimeTable())
        }

        return timeTableBinding.root
    }

    private fun getData() {
        timeTableBinding.progressBar.visibility = View.VISIBLE
        timeTableRecyclerView = timeTableBinding.timeTableList
        timeTableRecyclerView.layoutManager = LinearLayoutManager(context)
        timeTableRecyclerView.setHasFixedSize(true)
        itemList = arrayListOf()
        if (selectedDay == "Select Day") {
            Toast.makeText(context, "Please select a day", Toast.LENGTH_SHORT).show()
            timeTableBinding.progressBar.visibility = View.GONE
            return
        }
        database = FirebaseDatabase.getInstance().getReference("Departments").child(loggedUserDept)
            .child(selectedYear).child("Time Table").child(selectedDay)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(context, "No data found", Toast.LENGTH_SHORT).show()
                    timeTableBinding.timeTableList.visibility = View.GONE
                    timeTableBinding.progressBar.visibility = View.GONE
                }
                for (sessionSnapshot in snapshot.children) {
                    val startTime = sessionSnapshot.child("startTime").value.toString()
                    val endTime = sessionSnapshot.child("endTime").value.toString()
                    val str = sessionSnapshot.key
                    if (str != null) {
                        if (str.contains("Pra")) {
                            var courseCode1 = ""
                            var courseTeacher1 = ""
                            var courseTitle1 = ""
                            var courseCode2 = ""
                            var courseTeacher2 = ""
                            var courseTitle2 = ""
                            var courseCode3 = ""
                            var courseTeacher3 = ""
                            var courseTitle3 = ""

                            for (batch in sessionSnapshot.children) {
                                if (batch.key.toString().contains("1")) {
                                    courseCode1 = batch.child("courseCode1").value.toString()
                                    courseTeacher1 = batch.child("courseTeacher1").value.toString()
                                    courseTitle1 = batch.child("courseTitle1").value.toString()
                                } else if (batch.key.toString().contains("2")) {
                                    courseCode2 = batch.child("courseCode2").value.toString()
                                    courseTeacher2 = batch.child("courseTeacher2").value.toString()
                                    courseTitle2 = batch.child("courseTitle2").value.toString()
                                } else if (batch.key.toString().contains("3")) {
                                    courseCode3 = batch.child("courseCode3").value.toString()
                                    courseTeacher3 = batch.child("courseTeacher3").value.toString()
                                    courseTitle3 = batch.child("courseTitle3").value.toString()
                                }
                            }
                            val obj = ModelTimeTable(
                                startTime = startTime,
                                endTime = endTime,
                                courseCode1 = courseCode1,
                                courseTeacher1 = courseTeacher1,
                                courseTitle1 = courseTitle1,
                                courseCode2 = courseCode2,
                                courseTeacher2 = courseTeacher2,
                                courseTitle2 = courseTitle2,
                                courseCode3 = courseCode3,
                                courseTeacher3 = courseTeacher3,
                                courseTitle3 = courseTitle3,
                            )
                            itemList.add(obj)
                        } else {
                            var courseCode = sessionSnapshot.child("courseCode").value.toString()
                            var courseTeacher =
                                sessionSnapshot.child("courseTeacher").value.toString()
                            var courseTitle = sessionSnapshot.child("courseTitle").value.toString()

                            val obj = ModelTimeTable(
                                startTime = startTime,
                                endTime = endTime,
                                courseCode = courseCode,
                                courseTeacher = courseTeacher,
                                courseTitle = courseTitle,
                            )
                            itemList.add(obj)
                        }

                    }

                    // sort the itemsList according to the startTime format of startTime is: 12:00 AM, 1:50 PM, 2:00 PM, etc
                    itemList.sortBy { model ->
                        val time = model.startTime ?: "12:00 AM" // use "12:00 AM" if startTime is null
                        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                        val date = try {
                            sdf.parse(time)
                        } catch (e: ParseException) {
                            null
                        }
                        date?.time ?: Long.MAX_VALUE // return a large number to sort invalid dates to the end of the list
                    }
                    timeTableBinding.timeTableList.visibility = View.VISIBLE
                    timeTableRecyclerView.adapter = TimeTableAdapter(itemList)

                }
                timeTableBinding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getTimeInMinutes(timeString: String?): Int {
        if (timeString == null) return 0
        val timeRegex = "(\\d+):(\\d+) (AM|PM)".toRegex()
        val (hours, minutes, ampm) = timeRegex.find(timeString)?.destructured ?: return 0
        val hoursInt = hours.toInt() % 12 + if (ampm == "PM") 12 else 0
        return hoursInt * 60 + minutes.toInt()
    }


    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.dashFrameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimeTable().apply {
                arguments = Bundle().apply {

                }
            }
    }
}