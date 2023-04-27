package com.example.staffdashboard.timetable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.staffdashboard.ModelTimeTable
import com.example.staffdashboard.R

class TimeTableAdapter(private val itemList: List<ModelTimeTable>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_LECTURE = 0
    private val VIEW_TYPE_PRACTICAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LECTURE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.lecture_item, parent, false)
                LectureViewHolder(view)
            }
            VIEW_TYPE_PRACTICAL -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.practical_item, parent, false)
                PracticalViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = itemList[position]
        when (holder.itemViewType) {
            VIEW_TYPE_LECTURE -> {
                val lectureHolder = holder as LectureViewHolder
                lectureHolder.bind(item)
            }
            VIEW_TYPE_PRACTICAL -> {
                val practicalHolder = holder as PracticalViewHolder
                practicalHolder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemList[position].courseTitle1 == null) {
            VIEW_TYPE_LECTURE
        } else {
            VIEW_TYPE_PRACTICAL
        }
    }

    inner class LectureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeView: TextView = itemView.findViewById(R.id.start_time)
        private val endTimeView: TextView = itemView.findViewById(R.id.end_time)
        private val courseCodeView: TextView = itemView.findViewById(R.id.course_code)
        private val courseTeacherView: TextView = itemView.findViewById(R.id.course_teacher)
        private val courseTitleView: TextView = itemView.findViewById(R.id.course_title)

        fun bind(item: ModelTimeTable) {
            startTimeView.text = item.startTime
            endTimeView.text = item.endTime
            courseCodeView.text = item.courseCode
            courseTeacherView.text = item.courseTeacher
            courseTitleView.text = item.courseTitle
        }
    }

    inner class PracticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val startTimeView: TextView = itemView.findViewById(R.id.start_time)
        private val endTimeView: TextView = itemView.findViewById(R.id.end_time)
        private val courseCode1View: TextView = itemView.findViewById(R.id.txtIF1Code)
        private val courseTeacher1View: TextView = itemView.findViewById(R.id.txtIF1Teacher)
        private val courseTitle1View: TextView = itemView.findViewById(R.id.txtIF1Subject)
        private val courseCode2View: TextView = itemView.findViewById(R.id.txtIF2Code)
        private val courseTeacher2View: TextView = itemView.findViewById(R.id.txtIF2Teacher)
        private val courseTitle2View: TextView = itemView.findViewById(R.id.txtIF2Subject)
        private val courseCode3View: TextView = itemView.findViewById(R.id.txtIF3Code)
        private val courseTeacher3View: TextView = itemView.findViewById(R.id.txtIF3Teacher)
        private val courseTitle3View: TextView = itemView.findViewById(R.id.txtIF3Subject)

        fun bind(item: ModelTimeTable) {
            startTimeView.text = item.startTime
            endTimeView.text = item.endTime
            courseCode1View.text = item.courseCode1
            courseTeacher1View.text = item.courseTeacher1
            courseTitle1View.text = item.courseTitle1
            courseCode2View.text = item.courseCode2
            courseTeacher2View.text = item.courseTeacher2
            courseTitle2View.text = item.courseTitle2
            courseCode3View.text = item.courseCode3
            courseTeacher3View.text = item.courseTeacher3
            courseTitle3View.text = item.courseTitle3
        }
    }
}