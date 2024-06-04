package com.permission.lib_permission

import android.os.Build
import android.os.Environment

class PermissionManageExternalStorageTask(permissionBuilder: PermissionBuilder): PermissionTask(permissionBuilder) {
    override fun request() {
        // Android 11 才会有这个权限
        if (pb.shouldRequestManageExternalStoragePermission() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 授权即返回
            if (Environment.isExternalStorageManager()) {
                finish()
                return
            }
            pb.requestManageExternalStoragePermissionNow(this)
        } else {
            // 如果不符合要求结束掉整体流程
            finish()
        }

    }
}