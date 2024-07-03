package com.example.snippetpermission.domain.permission.service

import com.example.snippetpermission.domain.permission.controller.PermissionController
import com.example.snippetpermission.domain.permission.repository.PermissionRepository
import com.example.snippetpermission.model.Permission
import com.example.snippetpermission.model.PermissionType
import com.example.snippetpermission.permissionChecker.OwnerPermissionCheck
import com.example.snippetpermission.permissionChecker.ReaderPermissionCheck
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PermissionService(private val permissionRepository: PermissionRepository) {
    private val permissionChecks =
        mapOf(
            PermissionType.OWNER to OwnerPermissionCheck(),
            PermissionType.R to ReaderPermissionCheck(),
        )

    private val logger = LoggerFactory.getLogger(PermissionController::class.java)

    fun addPermission(
        permissionType: PermissionType,
        snippetId: Long,
        userId: String,
        shearerId: String = "",
    ) {
        if (!doesSnippetExist(snippetId) && permissionType == PermissionType.OWNER) {
            this.permissionRepository.save(Permission(permissionType, snippetId, userId))
            logger.info("Setting user $userId as the owner of the snippet $snippetId")
            return
        }
        // Check if the user can share the snippet, just owner can share snippets and they just can give read permissions
        if (!canShare(snippetId, shearerId)) {
            throw Exception("User $shearerId does not have permission to share the snippet")
        }

        checkIfUserHasAPreviousPermission(userId, snippetId, permissionType)
    }

    fun hasPermission(
        requestedPermission: PermissionType,
        snippetId: Long,
        userId: String,
    ): Boolean {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissionChecks[requestedPermission]?.hasPermission(permissions) ?: false
    }

    fun getPermissionType(
        snippetId: Long,
        userId: String,
    ): PermissionType {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissions.firstOrNull()?.permissionType ?: PermissionType.R
    }

    fun canShare(
        snippetId: Long,
        userId: String,
    ): Boolean {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissions.any { permission ->
            permission.permissionType == PermissionType.OWNER
        }
    }

    fun doesSnippetExist(snippetId: Long): Boolean {
        return this.permissionRepository.findBySnippetId(snippetId).isNotEmpty()
    }

    fun getSharedSnippets(userId: String): List<Long> {
        return this.permissionRepository.findByUserId(userId).filter {
                permission: Permission ->
            permission.permissionType == PermissionType.R
        }.map { permission: Permission ->
            permission.snippetId
        }
    }

    private fun checkIfUserHasAPreviousPermission(
        userId: String,
        snippetId: Long,
        permissionType: PermissionType,
    ) {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)

        onlyOneOwnerIsAllowed(permissionType, snippetId)

        // Check if the user was previously a reader
        onePermissionPerUser(permissionType, permissions)

        // If none of the above conditions are met, and if there is no
        // permission for that user in that snippet,add the new permission,
        if (permissions.isEmpty()) {
            this.permissionRepository.save(Permission(permissionType, snippetId, userId))
            logger.info("User $userId was saves as a reader of the snippet $snippetId")
        }
    }

    private fun onePermissionPerUser(
        permissionType: PermissionType,
        permissions: List<Permission>,
    ) {
        if (permissionType == PermissionType.OWNER && permissions.any { it.permissionType == PermissionType.R }) {
            throw Exception("User was previously a reader and cannot become an owner")
        }
    }

    private fun onlyOneOwnerIsAllowed(
        permissionType: PermissionType,
        snippetId: Long,
    ) {
        if (permissionType == PermissionType.OWNER) {
            val ownerPermissions = this.permissionRepository.findBySnippetId(snippetId)
            if (ownerPermissions.any { it.permissionType == PermissionType.OWNER }) {
                throw Exception("Snippet already has an owner")
            }
        }
    }
}
