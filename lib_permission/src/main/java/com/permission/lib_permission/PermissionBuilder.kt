package com.permission.lib_permission

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.permission.lib_permission.dialog.BasePermissionDialog
import com.permission.lib_permission.dialog.SettingDialog
import com.permission.lib_permission.popupWindow.PermissionPopup

class PermissionBuilder private constructor() {
    lateinit var activity: FragmentActivity
    var fragment: Fragment? = null
    var title = ""
    var detail = ""
    var dialogTitle = ""
    var dialogDetail = ""

    val normalPermissions: MutableSet<String> = LinkedHashSet()
    val deniedPermissions: MutableSet<String> = LinkedHashSet()
    val specialPermissions: MutableSet<String> = LinkedHashSet()
    val grantedPermissions: MutableSet<String> = LinkedHashSet()

    // 永久拒绝权限列表
    var permanentDeniedPermissions: MutableSet<String> = LinkedHashSet()
    var permissions = listOf<String>()

    // 权限申请回调 权限申请成功 true 权限申请失败 false 权限被拒绝的列表
    var callback: ((Boolean, List<String>) -> Unit)? = null

    // 特殊权限 特殊权限弹窗
    var specialDialog = listOf<BasePermissionDialog>()

    // 特殊权限
    val allSpecialPermissions = setOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.WRITE_SETTINGS,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.BODY_SENSORS_BACKGROUND,
    )
    var popupWindow: PermissionPopup? = null
    private val fragmentManager: FragmentManager
        get() {
            return fragment?.childFragmentManager ?: activity.supportFragmentManager
        }

    private val permissionFragment: PermissionFragment
        get() {
            val existedFragment =
                fragmentManager.findFragmentByTag(PermissionFragment::class.java.simpleName)
            return if (existedFragment != null) {
                existedFragment as PermissionFragment
            } else {
                val create = PermissionFragment()
                fragmentManager
                    .beginTransaction()
                    .add(create, PermissionFragment::class.java.simpleName)
                    .commitNowAllowingStateLoss()
                create
            }
        }

    constructor(activity: FragmentActivity) : this() {
        this.activity = activity
    }

    constructor(fragment: Fragment) : this() {
        this.fragment = fragment
    }

    init {
        if (fragment != null) {
            activity = fragment!!.requireActivity()
        }
    }


    fun permissions(vararg permissions: String): PermissionBuilder {
        return permissions(permissions.toMutableList())
    }

    fun permissions(permissions: MutableList<String>): PermissionBuilder {
        for (permission in permissions) {
            if (permission in allSpecialPermissions) {
                specialPermissions.add(permission)
            } else {
                normalPermissions.add(permission)
            }
        }
        this.permissions = permissions
        return this
    }

    fun titleToTip(title: String): PermissionBuilder {
        this.title = title
        return this
    }

    fun detailToTip(detail: String): PermissionBuilder {
        this.detail = detail
        return this
    }

    fun titleToPermission(title: String): PermissionBuilder {
        this.dialogTitle = title
        return this
    }

    fun detailToPermission(detail: String): PermissionBuilder {
        this.dialogDetail = detail
        return this
    }

    fun specialDialog(specialDialog: List<BasePermissionDialog>): PermissionBuilder {
        this.specialDialog = specialDialog
        return this
    }

    fun shouldRequestManageExternalStoragePermission(): Boolean {
        return specialPermissions.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
    }

    fun requestNow(permissions: Set<String>, task: BaseTask) {
        permissionFragment.requestNow(this, permissions, task)
    }

    fun requestManageExternalStoragePermissionNow(task: BaseTask) {
        // TODO(后续不再强制设置弹窗)
        if (specialDialog.isEmpty()) {
            throw IllegalArgumentException("Please configure the special permission description dialog")
        } else {
            val dialog =
                specialDialog.find { it.permissions == Manifest.permission.MANAGE_EXTERNAL_STORAGE }
            if (dialog == null) {
                throw IllegalArgumentException("Please configure the special permission description dialog with [Manifest.permission.MANAGE_EXTERNAL_STORAGE]")
            } else {
                dialog.show()
                dialog.getPositiveButton().setOnClickListener {
                    dialog.dismiss()
                    permissionFragment.requestManageExternalStoragePermissionNow(this, task)
                }
                dialog.getNegativeButton()?.setOnClickListener {
                    dialog.dismiss()
                    task.finish()
                }
            }
        }

    }

    fun request(callback: (Boolean, List<String>) -> Unit) {
        this.callback = callback
        realRequest()
    }

    private fun realRequest() {
        val taskList = RequestTask()
        taskList.addTask(PermissionNormalTask(this))
        taskList.addTask(PermissionManageExternalStorageTask(this))
        taskList.runTask()
    }

    fun removeFragment() {
        val existedFragment =
            fragmentManager.findFragmentByTag(PermissionFragment::class.java.simpleName)
        if (existedFragment != null) {
            fragmentManager.beginTransaction().remove(existedFragment).commitNowAllowingStateLoss()
        }
    }

    fun showSettingDialog(task: BaseTask) {
        val dialog = SettingDialog(activity, permanentDeniedPermissions)
        dialog.setTitle(dialogTitle)
        dialog.setDetail(dialogDetail)
        showSettingDialog(task, dialog)
    }

    private fun showSettingDialog(task: BaseTask, dialog: SettingDialog) {
        dialog.show()
        dialog.binding.btnSettingComplete.setOnClickListener {
            dialog.dismiss()
            permissionFragment.toSettings()
        }
        dialog.binding.btnCancel.setOnClickListener {
            dialog.dismiss()
            task.finish()
        }
    }
}