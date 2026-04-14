package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.VerifiableCredential
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemGiudelineBinding
import tw.gov.moda.diw.databinding.ItemRemindCredentialBinding

class RemindCredentialAdapter : ListAdapter<VerifiableCredential, RemindCredentialAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<VerifiableCredential>() {
    override fun areContentsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
        return false
    }

    override fun areItemsTheSame(oldItem: VerifiableCredential, newItem: VerifiableCredential): Boolean {
        return false
    }
}) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRemindCredentialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemRemindCredentialBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setItem(item: VerifiableCredential) {
            binding.tvName.text = item.display
        }
    }
}