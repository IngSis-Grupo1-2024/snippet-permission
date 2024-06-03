package com.example.snippetpermission.permissionChecker

import com.example.snippetpermission.model.Permission

interface PermissionCheck {
    fun hasPermission(permissions: List<Permission>): Boolean
}
