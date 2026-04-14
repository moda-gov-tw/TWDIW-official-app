package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialCheck
import tw.gov.moda.diw.databinding.ItemVerifiableCredentialCheckBinding

class VerifiableCredentialCheckAdapter(
    private val mCheckListener: ((VerifiableCredentialCheck) -> Unit)
) : ListAdapter<VerifiableCredentialCheck, VerifiableCredentialCheckAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<VerifiableCredentialCheck>() {
    override fun areContentsTheSame(oldItem: VerifiableCredentialCheck, newItem: VerifiableCredentialCheck): Boolean {
        return oldItem.isChecked == newItem.isChecked
    }

    override fun areItemsTheSame(oldItem: VerifiableCredentialCheck, newItem: VerifiableCredentialCheck): Boolean {
        return oldItem == newItem
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerifiableCredentialCheckAdapter.ItemViewHolder {
        val binding = ItemVerifiableCredentialCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VerifiableCredentialCheckAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemVerifiableCredentialCheckBinding) : RecyclerView.ViewHolder(binding.root), CompoundButton.OnCheckedChangeListener {
        private var mItem: VerifiableCredentialCheck? = null

        init {
            binding.checkBox.setOnCheckedChangeListener(this)
        }

        fun setItem(item: VerifiableCredentialCheck) {
            mItem = item
            binding.checkBox.text = item.title
            binding.checkBox.isChecked = item.isChecked
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            mItem?.also {
                it.isChecked = isChecked
                mCheckListener.invoke(it)
            }
        }
    }
}