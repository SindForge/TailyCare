package com.example.feedm.managementClasses.resultAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feedm.R
import com.example.feedm.data.RetrofitServiceGoogleFactory
import com.example.feedm.data.model.ExaRemoteResult
import com.example.feedm.data.model.GoogleRemoteResult
import com.example.feedm.data.model.Item
import com.example.feedm.data.model.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MySearchAdapter(private var items: GoogleRemoteResult, private val onItemClick:(Item)->Unit): RecyclerView.Adapter<ResultViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.sa_item_recyclerview, parent, false)

        return ResultViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {

        val item = items.items[position]
        try{
            val imageUrl = item.pagemap.cse_image[0].src
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .preload()
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imageView)

            holder.title.text = item.title
            holder.itemView.setOnClickListener{onItemClick(item)}
        }catch(npe: NullPointerException){

        }

    }


    override fun getItemCount(): Int {
       return items.items.size
    }



}