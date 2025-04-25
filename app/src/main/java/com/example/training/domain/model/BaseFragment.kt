package com.example.training.domain.model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.example.training.R
import kotlinx.coroutines.launch

abstract class BaseFragment <VB: ViewBinding> : Fragment() {

    private var _binding: VB? = null

    val binding get() = _binding!!

    protected abstract fun getViewBinding(): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = getViewBinding()
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun loadingOn()

    protected abstract fun loadingOf()

    protected fun prepareAlertDialog(
        title: String,
        description: String,
        onClick: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(description)
            .setPositiveButton(getString(R.string.string_ok)) { _, _ -> }
            .setOnDismissListener {
                onClick()
            }
            .show()
    }

    //Наблюдает за состоянием класса и обрабатывает его
    protected abstract fun observingState()

    //Отслеживает взаимодествие пользователя с ui
    protected abstract fun eventListener()
}

fun Fragment.observingStateWhenStart(body:suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            body()
        }
    }
}
