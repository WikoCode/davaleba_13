package com.example.myapplication

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.myapplication.MainActivity
import com.example.myapplication.databinding.FieldsItemBinding

class FieldsAdapter(private val mainActivity: MainActivity, private val formValues: MutableMap<Int, String>) : RecyclerView.Adapter<FieldsAdapter.FieldsViewHolder>() {

    inner class FieldsViewHolder(val binding: FieldsItemBinding) : ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<Fields>() {
        override fun areItemsTheSame(oldItem: Fields, newItem: Fields): Boolean {
            return oldItem.fieldId == newItem.fieldId
        }

        override fun areContentsTheSame(oldItem: Fields, newItem: Fields): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var fields: List<Fields>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = fields.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldsViewHolder {

        return FieldsViewHolder(
            FieldsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FieldsViewHolder, position: Int) {
        holder.binding.apply {
            val field = fields[position]
            etField.hint = field.hint

            when (field.keyboard) {
                "text" -> etField.inputType = InputType.TYPE_CLASS_TEXT
                "number" -> etField.inputType = InputType.TYPE_CLASS_NUMBER
            }

            Glide.with(root.context)
                .load(field.icon)
                .into(ivIcon)

            if (field.required) {
                etField.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_required_indicator, 0
                )
            } else {
                etField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }

            etField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateFormState(field.fieldId, s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

    }
    private fun updateFormState(fieldId: Int, value: String) {
        formValues[fieldId] = value
    }

}

