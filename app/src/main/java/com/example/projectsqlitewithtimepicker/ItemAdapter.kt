package com.example.projectsqlitewithtimepicker


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter(val context: Context, val items: ArrayList<MyActivityModel>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val llMain = view.llMain
        val tvTime = view.tvTime
        val tvDescription = view.tvDescription
        val ivEdit = view.ivEdit
        val ivDelete = view.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_row,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemAdapter.ViewHolder, position: Int) {
        val item = items.get(position)

        holder.tvTime.text = item.time
        holder.tvDescription.text = item.description

        if (position % 2 == 0) {
            holder.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.llMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
//
        holder.ivDelete.setOnClickListener { view ->
            if (context is MainActivity) {
                context.deleteRecordAlertDialog(item)
            }
        }
        holder.ivEdit.setOnClickListener { view ->
            if (context is MainActivity) {
                context.updateRecordDialog(item)
            }
        }

    }
}


