package com.example.snippetpermission.permissionChecker

import com.example.snippetpermission.model.Permission
import com.example.snippetpermission.model.PermissionType

class OwnerPermissionCheck : PermissionCheck {
    override fun hasPermission(permissions: List<Permission>): Boolean {
        return permissions.any { it.permissionType == PermissionType.OWNER }
    }
}
