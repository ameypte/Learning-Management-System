package com.example.staffdashboard.attendance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.R

class AttendanceAdapter(private val itemList: List<Student>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.student_item, parent, false)
        return AttendanceHolder(itemView)
    }

    override fun onBindViewHolder(holder: AttendanceHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AttendanceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.student_name)
        val attendance: CheckBox = itemView.findViewById(R.id.chAttendance)
        val percentage: TextView = itemView.findViewById(R.id.txtPercentage)

        fun bind(item: Student) {
            studentName.text = item.idCode + " - " + item.name
            attendance.isChecked = item.attendance
            percentage.text = item.percentage.toString() + "%"
        }

        init {
            attendance.setOnClickListener {
                itemList[adapterPosition].attendance = attendance.isChecked
            }
        }
    }

}