package com.example.impnewgpuplus.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.impnewgpuplus.databinding.ActivityMainBinding
import com.example.impnewgpuplus.utils.Util
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    lateinit var binding: ActivityMainBinding
    private val REQUEST_CAMERA_IMAGE = 10101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        if (EasyPermissions.hasPermissions(this@MainActivity, *Util.cameraPermissionPass)) {
            Log.d("myPermission", "hasPermissions allow")
            openCameraScreen()
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Please allow permissions to proceed further",
                REQUEST_CAMERA_IMAGE,
                *Util.cameraPermissionPass
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

        when (requestCode) {

            REQUEST_CAMERA_IMAGE -> {
                if (perms.size == Util.cameraPermissionPass.size) {
                    Log.d("myPermissionsGranted", "all Permission allow")
                    openCameraScreen()
                } else {
                    Log.d("myPermissionsGranted", "not all Permission allow")
                }
            }
            else -> {
                Log.d("myPermissionsGranted", "no any  Permission allow")
            }
        }

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("myPermission", "not allow")
        if (EasyPermissions.somePermissionPermanentlyDenied(this@MainActivity, perms)) {
            AppSettingsDialog.Builder(this@MainActivity).build().show()
        }
    }

    private fun openCameraScreen() {
        startActivity(Intent(this, CameraScreen::class.java))
    }
}