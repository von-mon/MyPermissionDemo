package com.permission.lib_permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

object MyPermissions {

    fun init(activity: FragmentActivity): PermissionBuilder {
        return PermissionBuilder(activity)
    }

    fun init(fragment: Fragment): PermissionBuilder {
        return PermissionBuilder(fragment)
    }

    /**
     * 检查权限是否被允许
     */
    fun isGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
