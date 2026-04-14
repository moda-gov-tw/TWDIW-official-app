package tw.gov.moda.digitalwallet.ui.record.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.OperationRecord
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.digitalwallet.data.element.OperationEnum
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemOperateRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class OperateRecordAdapter(
    private val onFooterItme: (OperationRecord) -> Unit
) : ListAdapter<OperationRecord, OperateRecordAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<OperationRecord>() {
    override fun areContentsTheSame(oldItem: OperationRecord, newItem: OperationRecord): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: OperationRecord, newItem: OperationRecord): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperateRecordAdapter.ItemViewHolder {
        val binding = ItemOperateRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OperateRecordAdapter.ItemViewHolder, position: Int) {
        if (position == itemCount - 1) {
            onFooterItme.invoke(getItem(position))
        }
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemOperateRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        private val mDateFormater = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        private var mItem: OperationRecord? = null

        fun setItem(item: OperationRecord) {
            this.mItem = item
            binding.tvTitle.text = item.text
            binding.tvDatetime.text = mDateFormater.format(Date(item.datetime))

            when (item.status) {
                OperationEnum.ADD_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedCredentials.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedFields.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.orange_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_add_card)
                    binding.tvStatus.text = itemView.context.getString(R.string.add_card)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.orange_06))
                }

                OperationEnum.INVAILD_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedCredentials.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedFields.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.secondary_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_invaild)
                    binding.tvStatus.text = itemView.context.getString(R.string.invalid_card)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary_06))
                }

                OperationEnum.DELETE_CARD -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedCredentials.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedFields.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.secondary_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_record_delete)
                    binding.tvStatus.text = itemView.context.getString(R.string.remove_credential)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary_06))
                }

                OperationEnum.EDIT_WALLET_PINCODE -> {
                    binding.tvDatetime.isVisible = true
                    binding.layStatus.isVisible = true
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedCredentials.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedFields.isVisible = false

                    binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.secondary_01))
                    binding.imgStatus.setImageResource(R.drawable.ic_edit_wallet)
                    binding.tvStatus.text = itemView.context.getString(R.string.edit_wallet_password)
                    binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary_06))
                }

                else -> {
                    binding.layStatus.isVisible = false
                    binding.imgToggleExpand.isVisible = false
                    binding.viewLine.isVisible = false
                    binding.layAuthorizedCredentials.isVisible = false
                    binding.layAuthorizedUnit.isVisible = false
                    binding.layAuthorizedPurpose.isVisible = false
                    binding.layAuthorizedFields.isVisible = false
                }
            }
        }
    }
}