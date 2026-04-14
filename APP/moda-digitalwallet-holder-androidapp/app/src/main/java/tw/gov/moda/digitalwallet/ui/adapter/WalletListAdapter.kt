package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.Wallet
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemWalletListBinding

class WalletListAdapter(private val mLisener: (Wallet) -> Unit) : ListAdapter<Wallet, WalletListAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<Wallet>() {
    override fun areContentsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Wallet, newItem: Wallet): Boolean {
        return oldItem == newItem
    }
}) {
    private var mSelectedName = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletListAdapter.ItemViewHolder {
        val binding = ItemWalletListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WalletListAdapter.ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }

    fun setSelectedNickname(name: String) {
        mSelectedName = name
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(val binding: ItemWalletListBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: Wallet? = null

        init {
            binding.root.setOnClickListener(this)
        }

        fun setItem(item: Wallet) {
            this.mItem = item
            binding.tvName.text = item.nickname
            if (mSelectedName == item.nickname) {
                binding.root.setBackgroundResource(R.drawable.shape_menu_background)
            } else {
                binding.root.setBackgroundResource(0)
            }
        }


        override fun onClick(v: View?) {
            mItem?.also { mLisener.invoke(it) }
        }
    }
}