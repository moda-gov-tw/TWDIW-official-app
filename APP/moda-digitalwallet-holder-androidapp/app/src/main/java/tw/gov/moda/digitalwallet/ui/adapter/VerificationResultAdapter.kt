package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.model.VerifiableCredentialDisplay
import tw.gov.moda.diw.databinding.ItemVerificationResultBinding

class VerificationResultAdapter : ListAdapter<VerifiableCredentialDisplay, VerificationResultAdapter.VerificationResultViewHolder>(
    object : DiffUtil.ItemCallback<VerifiableCredentialDisplay>() {
        override fun areItemsTheSame(
            oldItem: VerifiableCredentialDisplay,
            newItem: VerifiableCredentialDisplay
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: VerifiableCredentialDisplay,
            newItem: VerifiableCredentialDisplay
        ): Boolean {
            return oldItem == newItem
        }

    }
) {
    
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VerificationResultViewHolder {
        return VerificationResultViewHolder(ItemVerificationResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VerificationResultViewHolder, position: Int) {
        return holder.bind(getItem(position))
    }

    inner class VerificationResultViewHolder(val binding: ItemVerificationResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(display: VerifiableCredentialDisplay) {
            binding.tvTitle.text = display.title
            binding.tvValue.text = display.value
        }
    }
}