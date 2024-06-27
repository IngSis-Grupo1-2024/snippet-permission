package com.example.snippetpermission.domain.permission.model.input

import com.example.snippetpermission.model.PermissionType

data class PermissionIsAllowedInput(
    val permissionType: PermissionType,
    val snippetId: Long,
    val userId: String,
)
