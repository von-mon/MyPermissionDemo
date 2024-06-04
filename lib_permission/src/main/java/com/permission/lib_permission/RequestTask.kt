package com.permission.lib_permission

import com.permission.lib_permission.PermissionTask

class RequestTask {
    private var headTask: PermissionTask? = null
    private var tailTask: PermissionTask? = null

    fun addTask(task: PermissionTask) {
        if (headTask == null) {
            headTask = task
        }
        tailTask?.next = task
        tailTask = task
    }

    fun runTask() {
        headTask?.request()
    }
}