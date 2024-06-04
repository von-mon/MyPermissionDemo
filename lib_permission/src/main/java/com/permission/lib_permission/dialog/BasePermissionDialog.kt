package com.permission.lib_permission.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View

abstract class BasePermissionDialog:Dialog {
    var permissions = ""
    constructor(context: Context, permissions: String) : super(context) {
        this.permissions = permissions
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}
    constructor(context: Context, permissions: String,themeResId: Int) : super(context, themeResId) {
        this.permissions = permissions
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    abstract fun getPositiveButton(): View
    abstract fun getNegativeButton(): View?
}