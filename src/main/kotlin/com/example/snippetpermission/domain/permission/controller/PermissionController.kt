package com.example.snippetpermission.domain.permission.controller

import com.example.snippetpermission.domain.permission.service.PermissionService
import com.example.snippetpermission.model.PermissionType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PermissionController(private val permissionService: PermissionService) {
    @PostMapping("/map_permission")
    fun addPermission(
        @RequestBody permissionRequest: PermissionRequest,
    ): ResponseEntity<String> {
        permissionService.addPermission(
            permissionRequest.permissionType!!,
            permissionRequest.snippetId,
            permissionRequest.userId,
        )
        return ResponseEntity.ok().build<String>()
    }

    @GetMapping("/is_allowed")
    fun isAllowed(
        @RequestBody permissionRequest: PermissionRequest,
    ): ResponseEntity<Boolean> {
        val hasPermission =
            permissionService.hasPermission(
                permissionRequest.permissionType!!,
                permissionRequest.snippetId,
                permissionRequest.userId,
            )
        return ResponseEntity.ok(hasPermission)
    }

    @GetMapping("/get_permission_type")
    fun getPermissionType(
        @RequestBody permissionRequest: PermissionRequest,
    ): ResponseEntity<PermissionType> {
        val permissionType =
            permissionService.getPermissionType(
                permissionRequest.snippetId,
                permissionRequest.userId,
            )
        return ResponseEntity.ok(permissionType)
    }
}

data class PermissionRequest(
    val permissionType: PermissionType? = null,
    val snippetId: Int,
    val userId: Int,
)
