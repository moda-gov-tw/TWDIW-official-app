package tw.gov.moda.digitalwallet.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.model.RemindAlert
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.DialogRemindBinding

class RemindAlertAdapter(
    private val mOnConfirmListener: (RemindAlert) -> Unit
) : ListAdapter<RemindAlert, RemindAlertAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<RemindAlert>() {
    override fun areContentsTheSame(oldItem: RemindAlert, newItem: RemindAlert): Boolean {
        return false
    }

    override fun areItemsTheSame(oldItem: RemindAlert, newItem: RemindAlert): Boolean {
        return false
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = DialogRemindBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: DialogRemindBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var mItem: RemindAlert? = null

        init {
            binding.btnConfirm.setOnClickListener(this)
        }

        fun setItem(item: RemindAlert) {
            mItem = item
            binding.tvTitle.text = itemView.context.getString(item.title)
            binding.tvMessage.text = itemView.context.getString(item.message)
            if (item.credentialList.isNotEmpty()) {
                binding.tvMessage.isVisible = true
                binding.recyclerView.isVisible = true
                binding.recyclerView.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
                val adapter = RemindCredentialAdapter()
                binding.recyclerView.adapter = adapter
                adapter.submitList(item.credentialList.filter { it.display.isNotEmpty() })
            } else {
                binding.tvMessage.isVisible = false
                binding.recyclerView.isVisible = false
            }
            if (item.subRemind != null) {
                binding.tvTitle.text = itemView.context.getString(item.subRemind.title)
            }

            // 判斷是更新失敗，則要顯示訊息
            if (item.message == R.string.credential_cannot_update_please_check_connection) {
                binding.tvMessage.isVisible = true
            }


            // 顯示第二區塊清單
            binding.laySubGroup.isVisible = false
            if (item.subRemind != null && item.subRemind.credentialList.isNotEmpty()) {
                binding.laySubGroup.isVisible = true
                binding.tvSubMessage.text = itemView.context.getString(item.subRemind.message)
                binding.recyclerViewSub.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.VERTICAL, false)
                val adapter = RemindCredentialAdapter()
                binding.recyclerViewSub.adapter = adapter
                adapter.submitList(item.subRemind.credentialList)
            } else {
                binding.laySubGroup.isVisible = false
            }

            // 判斷是否滾到底部，是才可以點選確認
            binding.svMessage.post {
                binding.btnConfirm.isEnabled = !binding.svMessage.canScrollVertically(1)
                binding.svMessage.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    // 判斷是否滾動到最底部
                    if (!binding.btnConfirm.isEnabled) {
                        binding.btnConfirm.isEnabled = !binding.svMessage.canScrollVertically(1)
                    }
                }
            }
        }

        override fun onClick(v: View?) {
            if (v == binding.btnConfirm) {
                mItem?.also { mOnConfirmListener.invoke(it) }
            }
        }
    }
}