package com.ekik.protractor.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.view.doOnLayout
import com.ekik.protractor.*
import com.ekik.protractor.viewmodels.HomeViewModel
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : BaseFragment(), View.OnTouchListener {

    private lateinit var touchLine: View
    private lateinit var touchLineAngleDisplayTv: TextView
    private lateinit var plumbLine: View
    private lateinit var plumbDisplayAngleTv: TextView

    private lateinit var previewView: PreviewView
    private lateinit var camController: LifecycleCameraController

    private lateinit var holdingSign: TextView
    private var holdingAnim: Animation? = null

    private lateinit var resetBtn: Button
    private lateinit var calibrationBtn: Button

    private lateinit var cameraBtn: Button
    private lateinit var plumbBtn: Button
    private lateinit var touchBtn: Button

    private lateinit var protractorIV: ImageView

    private val homeViewModel: HomeViewModel by viewModel()

    override val layoutId: Int
        get() = R.layout.fragment_home

    companion object {
        fun newInstance(): HomeFragment {
            val args = Bundle()

            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.setOnTouchListener(this)
        return view
    }

    override fun setupViews(view: View) {
        touchLine = view.findViewById(R.id.touchLine)
        touchLineAngleDisplayTv = touchLine.findViewById(R.id.touchDisplayAngleTv)
        plumbLine = view.findViewById(R.id.plumbLine)
        plumbDisplayAngleTv = view.findViewById(R.id.displayAngle)
        cameraBtn = view.findViewById(R.id.cameraBtn)
        touchBtn = view.findViewById(R.id.touchBtn)
        plumbBtn = view.findViewById(R.id.plumbBtn)
        resetBtn = view.findViewById(R.id.resetBtn)
        calibrationBtn = view.findViewById(R.id.calibrationBtn)
        holdingSign = view.findViewById(R.id.holdingSignTv)
        protractorIV = view.findViewById(R.id.protractorIV)
        protractorIV.doOnLayout {
            homeViewModel.setAnchorPoint(PointF(it.x + it.width.toFloat() / 2, it.y))
        }
        createCameraView(view)
    }

    private fun createCameraView(view: View) {
        previewView = view.findViewById(R.id.previewView)
        previewView.setOnTouchListener(this)
        camController = LifecycleCameraController(requireContext())
        previewView.controller = camController
    }

    override fun setupClicks() {
        cameraBtn.setOnClickListener {
            homeViewModel.setMode(HomeViewModel.Mode.Camera)
        }
        plumbBtn.setOnClickListener {
            homeViewModel.setMode(HomeViewModel.Mode.Plumb)
        }
        touchBtn.setOnClickListener {
            homeViewModel.setMode(HomeViewModel.Mode.Touch)
        }
        calibrationBtn.setOnClickListener {
            homeViewModel.setCalibration()
        }
        resetBtn.setOnClickListener {
            homeViewModel.resetCalibration()
        }
    }

    override fun subscribeToViewModel() {
        homeViewModel.touchRealAngle.observe(viewLifecycleOwner) { angle ->
            touchLine.rotation = -angle.toFloat()
        }
        homeViewModel.plumbRealAngle.observe(viewLifecycleOwner) { angle ->
            plumbLine.rotation = -angle.toFloat()
        }
        homeViewModel.touchDisplayAngle.observe(viewLifecycleOwner) { angle ->
            touchLineAngleDisplayTv.text = angle
        }
        homeViewModel.plumbDisplayAngle.observe(viewLifecycleOwner) { angle ->
            plumbDisplayAngleTv.text = angle
        }
        homeViewModel.mode.observe(viewLifecycleOwner) { mode ->
            when(mode) {
                is HomeViewModel.Mode.Camera -> {
                    cameraModeButtonsHandling()
                    cameraModeLinesHandling()
                    protractorIV.setImageResource(R.drawable.protractor_plumb_bg)
                    startCamera()
                }
                is HomeViewModel.Mode.Plumb -> {
                    plumbModeButtonsHandling()
                    plumbModeLinesHandling()
                    protractorIV.setImageResource(R.drawable.protractor_plumb_bg)
                    stopCamera()
                }
                is HomeViewModel.Mode.Touch -> {
                    touchModeButtonsHandling()
                    touchModeLinesHandling()
                    protractorIV.setImageResource(R.drawable.protractor_touch_bg)
                    stopCamera()
                }
            }
        }
        homeViewModel.cameraHoldingMode.observe(viewLifecycleOwner) {
            when(it) {
                HomeViewModel.HoldingMode.HOLDING -> showHolding()
                HomeViewModel.HoldingMode.FREE -> hideHolding()
                null -> { DoNothing }
            }
        }
        homeViewModel.calibrationAction.observe(viewLifecycleOwner) {
            //TODO ask about text
            Toast.makeText(requireContext(), "Calibrated with ${it.round(1)}", Toast.LENGTH_SHORT).show()
        }
        homeViewModel.darkMode.observe(viewLifecycleOwner) { dark ->
            if (dark) {
                //TODO: handle dark mode
                Toast.makeText(requireContext(), "Make dark mode", Toast.LENGTH_SHORT).show()
            } else {
                //TODO: handle light mode
                Toast.makeText(requireContext(), "Make light mode", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideCalibrationButtons() {
        resetBtn.gone()
        calibrationBtn.gone()
    }

    private fun showCalibrationButtons() {
        resetBtn.show()
        calibrationBtn.show()
    }

    private fun startCamera() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            previewView.show()
            camController.bindToLifecycle(viewLifecycleOwner)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun stopCamera() {
        previewView.gone()
        camController.unbind()
    }

    private fun touchModeButtonsHandling() {
        cameraBtn.disable()
        touchBtn.enable()
        plumbBtn.enable()
        hideCalibrationButtons()
    }

    private fun plumbModeButtonsHandling() {
        cameraBtn.enable()
        touchBtn.enable()
        plumbBtn.disable()
        showCalibrationButtons()
    }

    private fun cameraModeButtonsHandling() {
        cameraBtn.disable()
        touchBtn.enable()
        plumbBtn.enable()
        showCalibrationButtons()
    }

    private fun touchModeLinesHandling() {
        plumbLine.gone()
        plumbDisplayAngleTv.gone()
        touchLine.show()
    }

    private fun plumbModeLinesHandling() {
        plumbLine.show()
        plumbDisplayAngleTv.show()
        touchLine.gone()
    }

    private fun cameraModeLinesHandling() {
        plumbLine.show()
        plumbDisplayAngleTv.show()
        touchLine.show()
    }

    private fun showHolding() {
        holdingSign.show()
        holdingAnim = AnimationUtils.loadAnimation(context, R.anim.alpha_anim)
        holdingSign.startAnimation(holdingAnim)
    }

    private fun hideHolding() {
        holdingSign.gone()
        holdingSign.clearAnimation()
        holdingAnim?.cancel()
        holdingAnim?.reset()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> homeViewModel.hold()
            MotionEvent.ACTION_UP -> homeViewModel.release()
        }
        homeViewModel.setTouchPoint(PointF(event.x, event.y))
        return true
    }

    override fun permissionGranted() {
        startCamera()
    }

    override fun permissionDenied() {
        //TODO need to ask about behaviour
        homeViewModel.setMode(HomeViewModel.Mode.Plumb)
        Toast.makeText(requireContext(), "Denied!", Toast.LENGTH_SHORT).show()
    }
}