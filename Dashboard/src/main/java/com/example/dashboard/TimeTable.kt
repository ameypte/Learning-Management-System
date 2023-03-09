package com.example.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var loggedUserDept: String
    private lateinit var loggedUserYear: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: DatabaseReference

    private lateinit var timeTableRecyclerView: RecyclerView
    private lateinit var itemList: ArrayList<ModelTimeTable>
    private lateinit var selectedDay: String

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_time_table, container, false)
        sharedPreferences = requireActivity().getSharedPreferences(
            getString(R.string.login_preference_file_name),
            Context.MODE_PRIVATE
        )
        loggedUserDept = sharedPreferences.getString("loggedUserDept", null).toString()
        loggedUserYear = sharedPreferences.getString("loggedUserYear", null).toString()

        val spinner = view.findViewById<Spinner>(R.id.spDay)

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
                selectedDay = p0?.getItemAtPosition(p2).toString()
                //code for recycler view
                timeTableRecyclerView = view.findViewById(R.id.timeTableList)
                timeTableRecyclerView.layoutManager = LinearLayoutManager(context)
                timeTableRecyclerView.setHasFixedSize(true)
                itemList = arrayListOf()
                getData()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        // Inflate the layout for this fragment
        return view
    }

    private fun getData() {
        database = FirebaseDatabase.getInstance().getReference("Departments").child(loggedUserDept)
            .child(loggedUserYear).child("Time Table").child(selectedDay)

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
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
                    timeTableRecyclerView.adapter = TimeTableAdapter(itemList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            TimeTable().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}