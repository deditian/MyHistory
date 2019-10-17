package com.dedi.myhistory.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dedi.myhistory.R
import com.dedi.myhistory.data.HistoryModel
import com.dedi.myhistory.detail.DetailActivity
import com.dedi.myhistory.model.ParceModel
import kotlinx.android.synthetic.main.item_content.view.*


class HomeActivityAdapter(activity: FragmentActivity) :
    PagedListAdapter<HistoryModel, HomeActivityAdapter.HomeViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryModel>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldFav: HistoryModel, newFav: HistoryModel): Boolean {
                return oldFav.date == newFav.date
            }

            override fun areContentsTheSame(oldFav: HistoryModel, newFav: HistoryModel): Boolean {
                return oldFav == newFav
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_content, parent, false))
    }


    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }

    }


    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(results: HistoryModel) = itemView.run {
            txt_title.text = results.title
            txt_tempat.text = results.location
            txtdate.text = results.date

            cv_item_course.setOnClickListener {
                val mIntent = Intent(context, DetailActivity::class.java).apply {
                    putExtra("list_data", ParceModel(results.id,results.title,results.date,results.location,results.description))
                }
                context.startActivity(mIntent)
            }

        }
    }

}