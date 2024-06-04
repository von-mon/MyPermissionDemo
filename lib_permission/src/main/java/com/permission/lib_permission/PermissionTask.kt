package com.permission.lib_permission

import android.Manifest
import android.os.Build
import android.os.Environment

abstract class PermissionTask(var pb: PermissionBuilder) : BaseTask {
    // 标记下一个任务
    var next: PermissionTask? = null

    override fun finish() {
        // 如果下一个任务为空，则执行结束
        next?.request() ?: run {
            val deniedList: MutableList<String> = ArrayList()
            deniedList.addAll(pb.deniedPermissions)
            deniedList.addAll(pb.permanentDeniedPermissions)
            // 判断特殊权限
            if (pb.shouldRequestManageExternalStoragePermission()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                    Environment.isExternalStorageManager()) {
                    pb.grantedPermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                } else {
                    deniedList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }
            // deniedList.addAll(pb.permissionsWontRequest)
            if (pb.popupWindow != null) {
                pb.popupWindow!!.dismiss()
            }
            if (pb.callback != null) {
                pb.callback!!.invoke(deniedList.isEmpty(), deniedList)

            }
            pb.removeFragment()
        }
    }
}