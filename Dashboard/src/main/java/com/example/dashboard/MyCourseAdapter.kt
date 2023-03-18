package com.example.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyCourseAdapter(private val itemList: List<ModelCourse>) :
    RecyclerView.Adapter<MyCourseAdapter.CourseHolder>() {
    private lateinit var listener: OnCourseListener
    interface OnCourseListener{
        fun onCourseRemoveClick(position: Int)
    }
    fun setOnCourseClickListener(listener: OnCourseListener){
        this.listener = listener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyCourseAdapter.CourseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return CourseHolder(view,listener)
    }

    override fun onBindViewHolder(holder: MyCourseAdapter.CourseHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    inner class CourseHolder(itemView: View,listener: OnCourseListener) : RecyclerView.ViewHolder(itemView) {
        private val courseCode = itemView.findViewById<TextView>(R.id.course_code)
        private val courseTitle = itemView.findViewById<TextView>(R.id.course_title)

        fun bind(item: ModelCourse) {
            courseCode.text = item.courseCode
            courseTitle.text = item.courseTitle
        }
        init {
            itemView.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
                listener.onCourseRemoveClick(adapterPosition)
            }
        }
    }

}