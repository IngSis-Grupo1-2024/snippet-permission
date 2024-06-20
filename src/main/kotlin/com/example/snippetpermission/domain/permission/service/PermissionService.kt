package com.example.snippetpermission.domain.permission.service

import com.example.snippetpermission.domain.permission.repository.PermissionRepository
import com.example.snippetpermission.model.Permission
import com.example.snippetpermission.model.PermissionType
import com.example.snippetpermission.permissionChecker.OwnerPermissionCheck
import com.example.snippetpermission.permissionChecker.ReaderPermissionCheck
import org.springframework.stereotype.Service

@Service
class PermissionService(private val permissionRepository: PermissionRepository) {
    private val permissionChecks =
        mapOf(
            PermissionType.OWNER to OwnerPermissionCheck(),
            PermissionType.R to ReaderPermissionCheck(),
        )

    fun addPermission(
        permissionType: PermissionType,
        snippetId: Int,
        userId: Int,
        shearerId: Int,
    ) {
        if (!doesSnippetExist(snippetId) && permissionType == PermissionType.OWNER) {
            this.permissionRepository.save(Permission(permissionType, snippetId, userId))
            return
        }
        // Check if the user can share the snippet, just owner can share snippets and they just can give read permissions
        if (!canShare(snippetId, shearerId)) {
            throw Exception("User does not have permission to share the snippet")
        }

        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)

        // Check if there is already an owner for the snippet
        if (permissionType == PermissionType.OWNER) {
            val ownerPermissions = this.permissionRepository.findBySnippetId(snippetId)
            if (ownerPermissions.any { it.permissionType == PermissionType.OWNER }) {
                throw Exception("Snippet already has an owner")
            }
        }

        // Check if the user was previously a reader
        if (permissionType == PermissionType.OWNER && permissions.any { it.permissionType == PermissionType.R }) {
            throw Exception("User was previously a reader and cannot become an owner")
        }

        // If none of the above conditions are met, add the new permission
        this.permissionRepository.save(Permission(permissionType, snippetId, userId))
    }

    fun hasPermission(
        requestedPermission: PermissionType,
        snippetId: Int,
        userId: Int,
    ): Boolean {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissionChecks[requestedPermission]?.hasPermission(permissions) ?: false
    }

    fun getPermissionType(
        snippetId: Int,
        userId: Int,
    ): PermissionType {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissions.firstOrNull()?.permissionType ?: PermissionType.R
    }

    fun canShare(
        snippetId: Int,
        userId: Int,
    ): Boolean {
        val permissions = this.permissionRepository.findByUserIdAndSnippetId(userId, snippetId)
        return permissions.any { permission ->
            permission.permissionType == PermissionType.OWNER
        }
    }

    fun doesSnippetExist(snippetId: Int): Boolean {
        return this.permissionRepository.findBySnippetId(snippetId).isNotEmpty()
    }
}
