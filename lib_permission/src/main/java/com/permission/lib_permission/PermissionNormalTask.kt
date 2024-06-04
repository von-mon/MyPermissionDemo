package com.permission.lib_permission


class PermissionNormalTask(permissionBuilder: PermissionBuilder): PermissionTask(permissionBuilder) {

    override fun request() {
        val requestList = mutableListOf<String>()
        for (permission in pb.normalPermissions) {
            if (MyPermissions.isGranted(pb.activity, permission)) {
                pb.grantedPermissions.add(permission)
            } else {
                requestList.add(permission)
            }
        }
        // 如果请求权限列表为空，则结束当前任务
        if (requestList.isEmpty()) {
            finish()
            return
        }
        // 否则发起请求 考虑到会手动关闭权限 因此需要全部请求
        pb.requestNow(pb.normalPermissions,this)

    }
}