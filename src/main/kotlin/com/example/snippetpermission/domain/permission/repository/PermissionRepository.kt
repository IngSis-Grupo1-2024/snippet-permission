package com.example.snippetpermission.domain.permission.repository

import com.example.snippetpermission.model.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    fun findByUserIdAndSnippetId(
        userId: String,
        snippetId: Long,
    ): List<Permission>

    fun findBySnippetId(snippetId: Long): List<Permission>

    fun findByUserId(userId: String): List<Permission>
}
