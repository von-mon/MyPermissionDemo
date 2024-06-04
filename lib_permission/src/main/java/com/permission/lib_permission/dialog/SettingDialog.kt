package com.permission.lib_permission.dialog

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import com.hanvonscanner.lib_permission.databinding.DialogSettingBinding
import com.hanvonscanner.lib_permission.databinding.PermissionLayoutBinding

class SettingDialog(context: Context,val permissions:Set<String>): Dialog(context) {
    lateinit var binding: DialogSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCanceledOnTouchOutside(false)
        initPermissionLayout()
        setupWindow()
    }

    fun setTitle(title:String) {
//        binding.tvTitle.text = title
    }

    fun setDetail(detail:String){
//        binding.tvDetail.text = detail
    }

    private fun initPermissionLayout() {
        val item = PermissionLayoutBinding.inflate(layoutInflater,binding.llPermissionList,false)
        for (permission in permissions) {
            when(permission) {
                Manifest.permission.CAMERA -> {
                    item.tvPermission.text = "相机"
                }
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    item.tvPermission.text = "存储"
                }
            }
        }
        binding.llPermissionList.addView(item.root)
    }

    private fun setupWindow() {
        val width = context.resources.displayMetrics.widthPixels
        val height = context.resources.displayMetrics.heightPixels
        if (width < height) {
            // now we are in portrait
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.86).toInt()
                it.attributes = param
            }
        } else {
            // now we are in landscape
            window?.let {
                val param = it.attributes
                it.setGravity(Gravity.CENTER)
                param.width = (width * 0.6).toInt()
                it.attributes = param
            }
        }
    }

}