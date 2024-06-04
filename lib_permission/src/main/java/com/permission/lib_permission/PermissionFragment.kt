package com.permission.lib_permission

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permission.lib_permission.popupWindow.PermissionPopup

class PermissionFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var pb: PermissionBuilder
    private lateinit var task: BaseTask
    private val requestNormalPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            postForResult {
                onRequestNormalPermissionsResult(grantResults)
            }
        }

    private val requestManageExternalStorageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            postForResult {
                onRequestManageExternalStoragePermissionResult()
            }
        }

    private val toSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            postForResult {
                onRequestSettingResult()
            }
        }

    /**
     * 普通权限请求
     */
    fun requestNow(
        permissionBuilder: PermissionBuilder,
        permissions: Set<String>,
        currentTask: BaseTask
    ) {
        pb = permissionBuilder
        task = currentTask
        // 显示顶层提示窗
        showPopup(pb.activity, pb.title, pb.detail)
        requestNormalPermissionLauncher.launch(permissions.toTypedArray())
    }

    fun requestManageExternalStoragePermissionNow(
        permissionBuilder: PermissionBuilder,
        currentTask: BaseTask
    ) {
        pb = permissionBuilder
        task = currentTask
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            var intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            if (intent.resolveActivity(requireActivity().packageManager) == null) {
                intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            }
            requestManageExternalStorageLauncher.launch(intent)
        } else {
            onRequestManageExternalStoragePermissionResult()
        }
    }

    private fun onRequestNormalPermissionsResult(grantResults: Map<String, Boolean>) {
        pb.grantedPermissions.clear()
        pb.popupWindow!!.dismiss()
        for ((permission, granted) in grantResults) {
            if (granted) {
                pb.grantedPermissions.add(permission)
                pb.deniedPermissions.remove(permission)
                pb.permanentDeniedPermissions.remove(permission)
            } else {
                val permissionRationale =
                    shouldShowRequestPermissionRationale(permission)
                if (permissionRationale) {
                    pb.deniedPermissions.add(permission)
                } else {
                    pb.permanentDeniedPermissions.add(permission)
                    pb.deniedPermissions.remove(permission)
                }
            }
        }
        val deniedPermissions = mutableListOf<String>()
        deniedPermissions.addAll(pb.deniedPermissions)
        deniedPermissions.addAll(pb.permanentDeniedPermissions)
        // 防止用户在设置打开了权限 这里再次判断一下
        for (permission in deniedPermissions) {
            if (MyPermissions.isGranted(requireContext(), permission)) {
                pb.deniedPermissions.remove(permission)
                pb.grantedPermissions.add(permission)
            }
        }
        val allGranted = pb.grantedPermissions.size == pb.normalPermissions.size
        if (allGranted) {
            // 全部授予权限，结束当前任务
            task.finish()
        } else {
            // 这里处理永久拒绝权限
            if (pb.permanentDeniedPermissions.isNotEmpty()) {
                pb.showSettingDialog(task)
            }
        }
    }

    private fun onRequestManageExternalStoragePermissionResult() {
        task.finish()
    }

    private fun onRequestSettingResult() {
        // 再次处理永久拒绝权限是否被授予
        val list = mutableListOf<String>()
        list.addAll(pb.permanentDeniedPermissions)
        for (permission in list) {
            if (MyPermissions.isGranted(requireContext(), permission)) {
                pb.grantedPermissions.add(permission)
                pb.permanentDeniedPermissions.remove(permission)
            }
        }
        task.finish()
    }

    fun toSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        toSettingsLauncher.launch(intent)
    }

    private fun showPopup(
        activity: FragmentActivity,
        title: String,
        detail: String,
    ) {
        if (pb.popupWindow == null) {
            pb.popupWindow = PermissionPopup(activity)
        }
        pb.popupWindow!!.setTitle(title)
        pb.popupWindow!!.setDetail(detail)
        pb.popupWindow!!.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        pb.popupWindow?.dismiss()
        pb.popupWindow = null
    }

    private fun postForResult(callback: () -> Unit) {
        handler.post {
            callback()
        }
    }
}