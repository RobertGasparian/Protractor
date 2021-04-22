package com.ekik.protractor.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {

    @get:LayoutRes
    abstract val layoutId: Int

    protected lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher =
            requireActivity().registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    permissionGranted()
                } else {
                    permissionDenied()
                }
            }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
        setupClicks()
        subscribeToViewModel()
    }

    protected open fun subscribeToViewModel() {

    }

    protected open fun setupViews(view: View) {

    }

    protected open fun setupClicks() {

    }

    protected fun registerPermissionCallback() {

    }

    protected fun checkPermission(requestedPermission: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
            requireContext(),
            requestedPermission
        )
    }

    protected open fun permissionGranted() {

    }

    protected open fun permissionDenied() {

    }
}