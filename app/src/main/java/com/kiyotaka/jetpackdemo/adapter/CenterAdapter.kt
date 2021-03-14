package com.kiyotaka.jetpackdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.kiyotaka.jetpackdemo.R
import com.kiyotaka.jetpackdemo.databinding.ItemRecycleCenterBinding

class CenterAdapter constructor(val fragment: Fragment) :
    ListAdapter<String, CenterAdapter.ViewHolder>(CenterDiffCallBack()) {


    class ViewHolder(private val binding: ItemRecycleCenterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: View.OnClickListener, item: String) {
            binding.apply {
                this.listener = listener
                this.string = item
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecycleCenterBinding.inflate(
                LayoutInflater.from(parent.context)
                , parent
                , false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val string = getItem(position)
        holder.apply {
            bind(onCreateListener(position), string)
            itemView.tag = string
        }
    }

    private fun onCreateListener(position: Int): View.OnClickListener {
        return View.OnClickListener {
            when (position) {
                0 -> {
                    fragment.findNavController()
                        .navigate(R.id.action_navigation_person_to_completeDataFragment)
                }
                /*
                1 -> {
                    fragment.findNavController().navigate(R.id.action_navigation_person_to_recordFragment)
                }
                2 -> {
                    fragment.findNavController().navigate(R.id.action_navigation_person_to_countFragment)
                }

                 */
                3 -> {
                 //   fragment.findNavController()
                      //  .navigate(R.id.action_navigation_person_to_verifyFragment)
                }
                4 -> {
                   // fragment.findNavController()
                     //   .navigate(R.id.action_navigation_person_to_changeKeyFragment)
                }
                5 -> {
                    fragment.activity?.finish()
                }
            }
        }
    }


}

private class CenterDiffCallBack : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}