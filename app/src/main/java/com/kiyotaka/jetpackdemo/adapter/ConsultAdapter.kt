package com.kiyotaka.jetpackdemo.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.ItemRecycleConsultBinding
import com.kiyotaka.jetpackdemo.http.ConsultResponse
import com.kiyotaka.jetpackdemo.ui.fragment.main.HomeFragment

class ConsultAdapter constructor(val fragment: Fragment) :
    ListAdapter<ConsultResponse, ConsultAdapter.ViewHolder>(ConsultDiffCallBack()) {

    class ViewHolder(private val binding: ItemRecycleConsultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: ConsultResponse) {
            binding.apply {
                this.listener = listener
                this.consult = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecycleConsultBinding.inflate(
                LayoutInflater.from(parent.context)
                , parent
                , false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consult = getItem(position)
        holder.apply {
            bind(onCreateListener(consult.id), consult)
            itemView.tag = consult
        }
    }

    private fun onCreateListener(id: Int): View.OnClickListener {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", id)

            NavHostFragment.findNavController(fragment)
                .navigate(R.id.action_recordFragment_to_detailFragment, bundle)

        }
    }
}

private class ConsultDiffCallBack : DiffUtil.ItemCallback<ConsultResponse>() {
    override fun areItemsTheSame(oldItem: ConsultResponse, newItem: ConsultResponse): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ConsultResponse, newItem: ConsultResponse): Boolean {
        return oldItem.start_time == newItem.start_time && oldItem.user == newItem.user
    }

}