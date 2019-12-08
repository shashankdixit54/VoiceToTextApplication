package com.example.voiceapplication


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class AllSentanceListAdapter(
    internal var modelList: List<Sentance>,
    private val context: Context,
    private val clickListener: MyOnClickListener
) : RecyclerView.Adapter<AllSentanceListAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return modelList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.titleTv.text = modelList[position].name
        holder.mainLinLayout.setOnClickListener {
            clickListener.clickedItem(modelList[position].name)
        }
    }

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        internal var titleTv: TextView

        internal var mainLinLayout: LinearLayout

        init {
            titleTv = v.findViewById(R.id.titleTv) as TextView
            mainLinLayout = v.findViewById(R.id.mainLayout) as LinearLayout


        }
    }

    interface MyOnClickListener{
        public fun clickedItem(str: String)
    }
}
