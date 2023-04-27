package com.example.staffdashboard

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
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


class UploadTimeTable : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var batch: String? = null
    private var calendarStartTime = Calendar.getInstance()
    private var calendarEndTime = Calendar.getInstance()
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var uploadTimeTableBinding: FragmentUploadTimeTableBinding
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var selectedType: RadioButton
    private lateinit var selectedBatch: RadioButton
    private lateinit var startTime: String
    private lateinit var endTime: String
    private lateinit var type:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        uploadTimeTableBinding = FragmentUploadTimeTableBinding.inflate(inflater, container, false)

        createNotificationChannel()

        sharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.login_preference_file_name), Context.MODE_PRIVATE
        )
        val spinner = uploadTimeTableBinding.spDay

        val items = arrayOf(
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
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
//                startTime format is 12:00 PM, 1:50 PM set the calenderStatTime according to that the startTime is in 12 hour format
                val time = startTime.split(" ")
                val time1 = time[0].split(":")
                val hour = time1[0].toInt()
                val minute = time1[1].toInt()
                val amPm = time[1]
                if (amPm == "PM") {
                    calendarStartTime.set(Calendar.HOUR_OF_DAY, hour + 12)
                } else {
                    calendarStartTime.set(Calendar.HOUR_OF_DAY, hour)
                }
                calendarStartTime.set(Calendar.MINUTE, minute)
                calendarStartTime.set(Calendar.SECOND, 0)
                calendarStartTime.set(Calendar.MILLISECOND, 0)
                uploadTimeTableBinding.btnStart.text = startTime
            }
        }
        uploadTimeTableBinding.btnEnd.setOnClickListener {
            timePicker { selectedTime ->
                endTime = selectedTime
//                endTime format is 12:00 PM, 1:50 PM set the calenderEndTime according to that the endTime is in 12 hour format
                val time = endTime.split(" ")
                val time1 = time[0].split(":")
                val hour = time1[0].toInt()
                val minute = time1[1].toInt()
                val amPm = time[1]
                if (amPm == "PM") {
                    calendarEndTime.set(Calendar.HOUR_OF_DAY, hour + 12)
                } else {
                    calendarEndTime.set(Calendar.HOUR_OF_DAY, hour)
                }
                calendarEndTime.set(Calendar.MINUTE, minute)
                calendarEndTime.set(Calendar.SECOND, 0)
                calendarEndTime.set(Calendar.MILLISECOND, 0)

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
            type=  selectedType.text.toString()
            val selectedCourseCode = uploadTimeTableBinding.spCourse.selectedItem.toString()
            var selectedCourseTitle = ""
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
                                            sendNotification()
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
                                    database.child(type).child("courseTeacher").setValue(staffName)
                                    database.child(type).child("courseTitle")
                                        .setValue(selectedCourseTitle).addOnSuccessListener {
                                            sendNotification()
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

    private fun sendNotification() {
        val intent = Intent(context,TTNotificationReceiver::class.java)
        val title = type
        val message = "Notification Message"
        intent.putExtra(titleExtra,title)
        intent.putExtra(messageExtra,message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val time = calendarStartTime.timeInMillis
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Time Table"
            val descriptionText = "Time Table Notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = descriptionText

            val notificationManager = requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun timePicker(callback: (String) -> Unit) {
        val currentTime = Calendar.getInstance()
        var hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePicker = TimePickerDialog(
            requireContext(), { _, hourOfDay, minute ->
                hour = hourOfDay
                val amPm = if (hour < 12) "AM" else "PM"
                hour %= 12
                if (hour == 0) hour = 12
                val selectedTime = String.format("%d:%02d %s", hour, minute, amPm)

                callback(selectedTime)
            }, hour, minute, false
        )

        timePicker.show()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = UploadTimeTable().apply {
            arguments = Bundle().apply {}
        }
    }
}