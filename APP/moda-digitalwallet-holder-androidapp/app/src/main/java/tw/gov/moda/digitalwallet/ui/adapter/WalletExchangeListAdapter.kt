package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemWalletExchangeBinding
import tw.gov.moda.diw.databinding.ItemWalletListBinding

class WalletExchangeListAdapter(private val mLisener: (Wallet) -> Unit) : ListAdapter<Wallet, WalletExchangeListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<Wallet>() {
    override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
        return oldItem == newItem
    }
}) {
    private var mSelectedName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletExchangeListAdapter.ItemViewHolder {
        val binding = ItemWalletExchangeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletExchangeListAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position), position == (itemCount - 1))
    }

    fun setSelectedNickname(name: String) {
        mSelectedName = name
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemWalletExchangeBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: Wallet? = null

        init {
            binding.root.setOnClickListener(this)
            binding.rbChecked.setOnClickListener(this)
        }

        fun setItem(item: Wallet, isLastItem: Boolean) {
            this.mItem = item
            binding.tvName.text = item.nickname
            if (mSelectedName == item.nickname) {
                binding.rbChecked.isChecked = true
            } else {
                binding.rbChecked.isChecked = false
            }
            binding.viewLine.isVisible = !isLastItem
        }


        override fun onClick(v: View?) {
            mItem?.also { mLisener.invoke(it) }
        }
    }
}