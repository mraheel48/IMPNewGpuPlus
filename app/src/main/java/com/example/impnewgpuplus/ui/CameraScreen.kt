package com.example.impnewgpuplus.ui

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.impnewgpuplus.GPUImageFilterTools
import com.example.impnewgpuplus.databinding.ActivityCameraScreenBinding
import com.example.impnewgpuplus.utilsCamera.Camera2Loader
import com.example.impnewgpuplus.utilsCamera.CameraLoader
import com.example.impnewgpuplus.utilsCamera.doOnLayout
import jp.co.cyberagent.android.gpuimage.GPUImageView
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.util.Rotation

class CameraScreen : AppCompatActivity() {

    private lateinit var binding: ActivityCameraScreenBinding

    private val cameraLoader: CameraLoader by lazy {
        Camera2Loader(this@CameraScreen)
    }

    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                filterAdjuster?.adjust(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        binding.buttonChooseFilter.setOnClickListener {
            GPUImageFilterTools.showDialog(this) { filter -> switchFilterTo(filter) }
        }

        binding.buttonCapture.setOnClickListener {
            saveSnapshot()
        }

        binding.imgSwitchCamera.run {
            if (!cameraLoader.hasMultipleCamera()) {
                visibility = View.GONE
            }
            setOnClickListener {
                cameraLoader.switchCamera()
                binding.surfaceView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
            }
        }

        cameraLoader.setOnPreviewFrameListener { data, width, height ->
            binding.surfaceView.updatePreviewFrame(data, width, height)
        }

        binding.surfaceView.setRotation(getRotation(cameraLoader.getCameraOrientation()))
        binding.surfaceView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY)

    }

    override fun onResume() {
        super.onResume()
        binding.surfaceView.doOnLayout {
            cameraLoader.onResume(it.width, it.height)
        }
    }

    override fun onPause() {
        cameraLoader.onPause()
        super.onPause()
    }

    private fun saveSnapshot() {
        val folderName = "GPUImage"
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        binding.surfaceView.saveToPictures(folderName, fileName) {
            Toast.makeText(this, "$folderName/$fileName saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRotation(orientation: Int): Rotation {
        return when (orientation) {
            90 -> Rotation.ROTATION_90
            180 -> Rotation.ROTATION_180
            270 -> Rotation.ROTATION_270
            else -> Rotation.NORMAL
        }
    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        if (binding.surfaceView.filter == null || binding.surfaceView.filter!!.javaClass != filter.javaClass) {
            binding.surfaceView.filter = filter
            filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
            filterAdjuster?.adjust(binding.seekBar.progress)
        }
    }

}