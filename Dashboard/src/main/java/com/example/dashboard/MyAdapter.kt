package com.example.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val studentList: ArrayList<Student>):RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.student_item,parent,false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = studentList[position]
        holder.name.text = currentItem.name
        holder.idCode.text = currentItem.idCode
    }

    class MyViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.valName)
        val idCode:TextView = itemView.findViewById(R.id.valId)
    }
}