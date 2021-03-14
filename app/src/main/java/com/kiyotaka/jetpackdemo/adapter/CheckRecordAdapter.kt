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
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.ItemRecycleCheckRecordBinding
import com.kiyotaka.jetpackdemo.http.CheckRecordResponse

class CheckRecordAdapter constructor(val fragment: Fragment) :
    ListAdapter<CheckRecordResponse, CheckRecordAdapter.ViewHolder>(CheckRecordDiffCallBack()) {

    class ViewHolder(private val binding: ItemRecycleCheckRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: CheckRecordResponse) {
            binding.itemCheckName.text = when (item.check_status) {
                -1 -> "不通过"
                0 -> "审核中"
                1 -> "审核通过"
                else -> "加载中"
            }
            binding.apply {
                this.listener = listener
                this.checkrecord = item
                executePendingBindings()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consult = getItem(position)
        holder.apply {
            bind(onCreateListener(consult.id), consult)
            itemView.tag = consult
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecycleCheckRecordBinding.inflate(
                LayoutInflater.from(parent.context)
                , parent
                , false
            )
        )
    }

    private fun onCreateListener(id: Int): View.OnClickListener {
        return View.OnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", id)

            NavHostFragment.findNavController(fragment)
                .navigate(R.id.action_checkFragment_to_verifyFragment, bundle)

        }
    }
}

private class CheckRecordDiffCallBack : DiffUtil.ItemCallback<CheckRecordResponse>() {
    override fun areItemsTheSame(
        oldItem: CheckRecordResponse,
        newItem: CheckRecordResponse
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CheckRecordResponse,
        newItem: CheckRecordResponse
    ): Boolean {
        return oldItem == newItem
    }

}