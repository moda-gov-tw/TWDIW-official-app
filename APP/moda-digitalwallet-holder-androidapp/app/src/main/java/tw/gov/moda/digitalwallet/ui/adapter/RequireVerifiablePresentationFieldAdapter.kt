package tw.gov.moda.digitalwallet.ui.adapter

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.gov.moda.digitalwallet.data.model.RequireVerifiablePresentationField
import tw.gov.moda.digitalwallet.extension.transformationMethodByChar
import tw.gov.moda.diw.databinding.ItemCustomInputBinding

class RequireVerifiablePresentationFieldAdapter(
    private val mTextChangeListener: (Int, String) -> Unit
) : ListAdapter<RequireVerifiablePresentationField, RequireVerifiablePresentationFieldAdapter.RequireVerifiablePresentationFieldViewHolder>(
    object : DiffUtil.ItemCallback<RequireVerifiablePresentationField>() {
        override fun areItemsTheSame(
            oldItem: RequireVerifiablePresentationField,
            newItem: RequireVerifiablePresentationField
        ): Boolean {
            return oldItem.ename == newItem.ename
        }

        override fun areContentsTheSame(
            oldItem: RequireVerifiablePresentationField,
            newItem: RequireVerifiablePresentationField
        ): Boolean {
            return oldItem.isHidden == newItem.isHidden && oldItem.isError == newItem.isError
        }

    }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequireVerifiablePresentationFieldViewHolder {
        return RequireVerifiablePresentationFieldViewHolder(ItemCustomInputBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: RequireVerifiablePresentationFieldViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RequireVerifiablePresentationFieldViewHolder(val binding: ItemCustomInputBinding) : RecyclerView.ViewHolder(binding.root) {

        private var mItem: RequireVerifiablePresentationField? = null

        private val textWatcher = object :TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                mItem?.also { mTextChangeListener.invoke(it.index, s.toString()) }
            }

        }

        fun bind(item: RequireVerifiablePresentationField) {
            mItem = item

            binding.editText.removeTextChangedListener(textWatcher)
            binding.tvTitle.text = item.cname
            binding.tvEditError.text = item.description
            binding.editText.setText(item.value)
            if (item.isHidden) {
                binding.editText.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
                binding.editText.transformationMethodByChar('*')
            } else {
                binding.editText.inputType = (InputType.TYPE_CLASS_TEXT)
                binding.editText.transformationMethod = null
            }
            binding.editText.setSelection(item.value.length)
            binding.editText.addTextChangedListener(textWatcher)
            if (item.isError) {
                binding.editText.isSelected = true
                binding.tvEditError.visibility = View.VISIBLE
            } else {
                binding.editText.isSelected = false
                binding.tvEditError.visibility = View.INVISIBLE
            }
        }



    }
}