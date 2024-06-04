package com.permission.lib_permission.popupWindow

import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import com.hanvonscanner.lib_permission.databinding.ViewPurposeOfPermissionApplicationBinding


class PermissionPopup(private val activity: FragmentActivity):PopupWindow() {
    private var binding: ViewPurposeOfPermissionApplicationBinding =
        ViewPurposeOfPermissionApplicationBinding.inflate(activity.layoutInflater)

    init {
        contentView = binding.root
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.isFocusable = false
        this.setBackgroundDrawable(ColorDrawable(0x00000000))
        this.isOutsideTouchable = false
        initView()
    }

    private fun initView() {
        binding.title.text = "权限申请"
        binding.detail.text = "请允许以下权限，以正常使用相关功能"
    }

    fun setTitle(title: String) {
        binding.title.text = title
    }

    fun setDetail(detail: String) {
        binding.detail.text = detail
    }

    fun show() {
        if (activity.window == null) {
            return
        }
        // 防止activity不在running时，弹出popupWindow
        try {
            showAtLocation(activity.window.decorView, Gravity.TOP, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}