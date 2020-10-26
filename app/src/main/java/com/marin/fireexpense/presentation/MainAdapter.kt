package com.marin.fireexpense.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.DiffResult.NO_POSITION
import androidx.recyclerview.widget.RecyclerView
import com.marin.fireexpense.data.model.Expense
import com.marin.fireexpense.databinding.ExpenseRowBinding
import kotlin.properties.Delegates

/**
 * Created by Enrique Marín on 06-10-2020.
 */

class MainAdapter(
    private val context: Context,
    private val itemClickListener: OnExpenseClickListener
) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

    var expenseList: List<Expense> by Delegates.observable(emptyList()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val itemBinding =
            ExpenseRowBinding.inflate(LayoutInflater.from(context), parent, false)

        val holder = MainViewHolder(itemBinding)
        itemBinding.root.setOnClickListener {
            val position =
                holder.adapterPosition.takeIf { it != NO_POSITION } ?: return@setOnClickListener
            itemClickListener.onExpenseClick(expenseList[position], position)
        }

        return holder
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(expenseList[position])
    }

    override fun getItemCount(): Int = expenseList.size

    interface OnExpenseClickListener {
        fun onExpenseClick(expense: Expense, position: Int)
    }

    inner class MainViewHolder(
        private val binding: ExpenseRowBinding
    ) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun bind(item: Expense) {
                binding.apply {
                    txtConcept.text = item.concept
                    txtAmount.text = item.amount.toString() + "€"
                    txtDate.text = item.date
                }
            }
    }
}