package com.example.snippetpermission.domain.permission.repository

import com.example.snippetpermission.model.Permission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {

    fun findByUserIdAndSnippetId(userId: Int, snippetId: Int): List<Permission>
    fun findBySnippetId(snippetId: Int): List<Permission>
}
