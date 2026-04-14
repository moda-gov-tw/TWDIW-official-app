package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.common.AppConstants
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialField
import tw.gov.moda.diw.databinding.ItemChangeCredentialFieldBinding

class ChangeCredentialFieldAdapter : ListAdapter<VerifiableCredentialField, ChangeCredentialFieldAdapter.ChangeCredentialViewHolder>(object : DiffUtil.ItemCallback<VerifiableCredentialField>() {
    override fun areItemsTheSame(oldItem: VerifiableCredentialField, newItem: VerifiableCredentialField): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: VerifiableCredentialField, newItem: VerifiableCredentialField): Boolean {
        return oldItem == newItem
    }

}) {
    private var mIsHidden = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeCredentialViewHolder {
        return ChangeCredentialViewHolder(ItemChangeCredentialFieldBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChangeCredentialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setVisible(isHidden: Boolean) {
        mIsHidden = isHidden
    }

    inner class ChangeCredentialViewHolder(val binding: ItemChangeCredentialFieldBinding) : RecyclerView.ViewHolder(binding.root){

        private var mItem: VerifiableCredentialField? = null

        fun bind(item: VerifiableCredentialField) {
            mItem = item
            if (mIsHidden) {
                binding.tvCardField.text = item.title
                binding.tvValue.text = AppConstants.Common.MARK_TEXT
            } else {
                binding.tvCardField.text = item.title
                binding.tvValue.text = item.value
            }
        }
    }
}