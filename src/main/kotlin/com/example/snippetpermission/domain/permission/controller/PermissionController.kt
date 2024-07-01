package com.example.snippetpermission.domain.permission.controller

import com.example.snippetpermission.domain.permission.model.dto.SnippetIds
import com.example.snippetpermission.domain.permission.model.input.PermissionIsAllowedInput
import com.example.snippetpermission.domain.permission.model.input.PermissionRequest
import com.example.snippetpermission.domain.permission.service.PermissionService
import com.example.snippetpermission.model.PermissionType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*

@RestController
class PermissionController(private val permissionService: PermissionService) {

    private val logger = LoggerFactory.getLogger(PermissionController::class.java)
    @PostMapping("/map_permission")
    fun addPermission(
        @RequestBody permissionRequest: PermissionRequest,
    ): ResponseEntity<String> {
        try {
            logger.info("Adding permission of snippet ${permissionRequest.snippetId} to user ${permissionRequest.userId}")
            permissionService.addPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
                permissionRequest.sharerId,
            )
            return ResponseEntity.ok().build()
        } catch (e: Exception){
            logger.warn(e.message)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
        }
    }

    @GetMapping("/shared")
    fun getSharedSnippets(
        @AuthenticationPrincipal jwt: Jwt
    ): ResponseEntity<SnippetIds> {
        logger.info("Getting shared snippets id for user ${jwt.subject}")
        val snippetsId = permissionService.getSharedSnippets(jwt.subject)
        return ResponseEntity.ok(SnippetIds(snippetsId))
    }

    @GetMapping("/is_allowed")
    fun isAllowed(
        @RequestBody permissionRequest: PermissionIsAllowedInput,
    ): ResponseEntity<Boolean> {
        logger.info("Checking if user ${permissionRequest.userId} is allowed " +
                "for snippet ${permissionRequest.snippetId} for the permission ${permissionRequest.permissionType}")
        val hasPermission =
            permissionService.hasPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
            )
        logger.info("Permission ${permissionRequest.permissionType} is $hasPermission " +
                "for snippet ${permissionRequest.snippetId} to user ${permissionRequest.userId}")
        return ResponseEntity.ok(hasPermission)
    }

    @GetMapping("/get_permission_type/{snippetId}/{userId}")
    fun getPermissionType(
        @PathVariable snippetId: String,
        @PathVariable userId: String,
    ): ResponseEntity<PermissionType> {
        logger.info("Getting permission of user $userId for snippet $snippetId")
        val permissionType =
            permissionService.getPermissionType(
                snippetId.toLong(),
                userId,
            )
        return ResponseEntity.ok(permissionType)
    }
}
