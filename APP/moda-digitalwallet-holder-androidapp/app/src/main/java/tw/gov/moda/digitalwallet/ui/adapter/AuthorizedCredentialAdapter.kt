package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.model.AuthorizedCredential
import tw.gov.moda.diw.databinding.ItemAuthorizedCredentialBinding

class AuthorizedCredentialAdapter : ListAdapter<AuthorizedCredential, AuthorizedCredentialAdapter.AuthorizedCredentialViewHolder>(
    object : DiffUtil.ItemCallback<AuthorizedCredential>() {
        override fun areItemsTheSame(
            oldItem: AuthorizedCredential,
            newItem: AuthorizedCredential
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: AuthorizedCredential,
            newItem: AuthorizedCredential
        ): Boolean {
            return oldItem == newItem
        }

    }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AuthorizedCredentialViewHolder {
        return AuthorizedCredentialViewHolder(ItemAuthorizedCredentialBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AuthorizedCredentialViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AuthorizedCredentialViewHolder(val binding: ItemAuthorizedCredentialBinding) : RecyclerView.ViewHolder(binding.root) {

        private var mItem: AuthorizedCredential? = null

        fun bind(item: AuthorizedCredential) {
            mItem = item
            binding.tvVcName.text = item.vcName
            binding.tvOrgName.text = item.orgName
        }
    }
}