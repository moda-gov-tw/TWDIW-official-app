package tw.gov.moda.digitalwallet.ui.record.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.db.PresentationRecord
import tw.gov.moda.diw.R
import tw.gov.moda.diw.databinding.ItemOperateRecordBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PresentationRecordAdapter(
    private val onFooterItme: (PresentationRecord) -> Unit
) : ListAdapter<PresentationRecord, PresentationRecordAdapter.ItemViewHolder>(object : DiffUtil.ItemCallback<PresentationRecord>() {
    override fun areContentsTheSame(oldItem: PresentationRecord, newItem: PresentationRecord): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: PresentationRecord, newItem: PresentationRecord): Boolean {
        return oldItem == newItem
    }
}) {

    private val mExpandSet: MutableSet<Long> = mutableSetOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresentationRecordAdapter.ItemViewHolder {
        val binding = ItemOperateRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PresentationRecordAdapter.ItemViewHolder, position: Int) {
        if (position == itemCount - 1) {
            onFooterItme.invoke(getItem(position))
        }
        holder.setItem(getItem(position))
    }


    inner class ItemViewHolder(val binding: ItemOperateRecordBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val mDateFormater = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        private var mItem: PresentationRecord? = null

        fun setItem(item: PresentationRecord) {
            this.mItem = item
            binding.tvTitle.text = item.text
            binding.tvDatetime.text = mDateFormater.format(Date(item.datetime))
            binding.tvDatetime.isVisible = true
            binding.layStatus.isVisible = true
            binding.imgToggleExpand.isVisible = true
            binding.viewLine.isVisible = false
            binding.layAuthorizedUnit.isVisible = false
            binding.layAuthorizedPurpose.isVisible = false
            binding.layAuthorizedFields.isVisible = false

            binding.tvAuthorizedCredentials.text = item.vcNames
            binding.tvAuthorizedUnit.text = item.authorizationUnit
            binding.tvAuthorizedPurpose.text = item.authorizationPurpose
            binding.tvAuthorizedFields.text = item.authorizationFields

            if (mExpandSet.contains(item.uid)) {
                binding.imgToggleExpand.isSelected = false
                binding.viewLine.isVisible = true
                binding.layAuthorizedCredentials.isVisible = true
                binding.layAuthorizedUnit.isVisible = true
                binding.layAuthorizedPurpose.isVisible = true
                binding.layAuthorizedFields.isVisible = true
            } else {
                binding.imgToggleExpand.isSelected = true
                binding.viewLine.isVisible = false
                binding.layAuthorizedCredentials.isVisible = false
                binding.layAuthorizedUnit.isVisible = false
                binding.layAuthorizedPurpose.isVisible = false
                binding.layAuthorizedFields.isVisible = false
            }


            binding.layStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.primary_01))
            binding.imgStatus.setImageResource(R.drawable.ic_record_authorization)
            binding.tvStatus.text = itemView.context.getString(R.string.authorized_card)
            binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary_06))

            binding.imgToggleExpand.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (v == binding.imgToggleExpand) {
                mItem?.also { cardRecord ->
                    if (mExpandSet.contains(cardRecord.uid)) {
                        mExpandSet.remove(cardRecord.uid)
                    } else {
                        mExpandSet.add(cardRecord.uid)
                    }
                    setItem(cardRecord)
                }
            }
        }
    }
}