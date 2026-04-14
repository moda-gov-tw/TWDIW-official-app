package tw.gov.moda.digitalwallet.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseDialog<B : ViewBinding> : DialogFragment() {
    abstract fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?): B

    protected abstract fun getViewModel(): BaseViewModel?

    protected val binding get() = _binding!!

    private var _binding: B? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = initViewBinding(inflater,container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initView() {
    }

    protected open fun subscribeObservers() {
    }

}