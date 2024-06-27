package com.example.snippetpermission.domain.permission.controller

import com.example.snippetpermission.domain.permission.model.dto.SnippetIds
import com.example.snippetpermission.domain.permission.model.input.PermissionIsAllowedInput
import com.example.snippetpermission.domain.permission.model.input.PermissionRequest
import com.example.snippetpermission.domain.permission.model.input.PermissionTypeInput
import com.example.snippetpermission.domain.permission.service.PermissionService
import com.example.snippetpermission.model.PermissionType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class PermissionController(private val permissionService: PermissionService) {
    @PostMapping("/map_permission")
    fun addPermission(
        @RequestBody permissionRequest: PermissionRequest,
    ): ResponseEntity<String> {
        permissionService.addPermission(
            permissionRequest.permissionType,
            permissionRequest.snippetId,
            permissionRequest.userId,
            permissionRequest.sharerId,
        )
        return ResponseEntity.ok().build<String>()
    }

    @GetMapping("/shared")
    fun getSharedSnippets(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<SnippetIds> {
        val snippetsId = permissionService.getSharedSnippets(jwt.subject)
        return ResponseEntity.ok(SnippetIds(snippetsId))
    }

    @GetMapping("/is_allowed")
    fun isAllowed(
        @RequestBody permissionRequest: PermissionIsAllowedInput,
    ): ResponseEntity<Boolean> {
        val hasPermission =
            permissionService.hasPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
            )
        return ResponseEntity.ok(hasPermission)
    }

    @GetMapping("/get_permission_type")
    fun getPermissionType(
        @RequestBody permissionRequest: PermissionTypeInput,
    ): ResponseEntity<PermissionType> {
        val permissionType =
            permissionService.getPermissionType(
                permissionRequest.snippetId,
                permissionRequest.userId,
            )
        return ResponseEntity.ok(permissionType)
    }
}
