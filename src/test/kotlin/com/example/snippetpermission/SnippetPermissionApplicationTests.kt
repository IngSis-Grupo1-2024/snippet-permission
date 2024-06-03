package com.example.snippetpermission

import com.example.snippetpermission.domain.permission.repository.PermissionRepository
import com.example.snippetpermission.domain.permission.service.PermissionService
import com.example.snippetpermission.model.Permission
import com.example.snippetpermission.model.PermissionType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
@AutoConfigureTestDatabase
class PermissionServiceTests {

    @MockBean
    private lateinit var permissionRepository: PermissionRepository

    private lateinit var permissionService: PermissionService

    @BeforeEach
    fun setUp() {
        permissionService = PermissionService(permissionRepository)
    }

    @Test
    fun `test addPermission`() {
        val permission = Permission(PermissionType.OWNER, 1, 1)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))
        `when`(permissionRepository.findBySnippetId(1)).thenReturn(emptyList())
        `when`(permissionRepository.save(permission)).thenReturn(permission)

        assertEquals(PermissionType.OWNER, permissionService.getPermissionType(1, 1))
    }

    @Test
    fun `test hasPermission`() {
        val permission = Permission(PermissionType.OWNER, 1, 1)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))

        assertTrue(permissionService.hasPermission(PermissionType.OWNER, 1, 1))
        assertFalse(permissionService.hasPermission(PermissionType.R, 1, 2))
    }

    @Test
    fun `test getPermissionType`() {
        val permission = Permission(PermissionType.OWNER, 1, 1)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))

        assertEquals(PermissionType.OWNER, permissionService.getPermissionType(1, 1))
    }

    @Test
    fun `test canShare`() {
        val permission = Permission(PermissionType.OWNER, 1, 1)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))

        assertTrue(permissionService.canShare(1, 1))
    }

    @Test
    fun `test reader cannot share`() {
        val permission = Permission(PermissionType.R, 1, 1)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))

        assertFalse(permissionService.canShare(1, 1))
    }

    @Test
    fun `test there cannot be two owners`() {
        val permission = Permission(PermissionType.OWNER, 1, 1)
        val permission2 = Permission(PermissionType.OWNER, 1, 2)
        `when`(permissionRepository.findByUserIdAndSnippetId(1, 1)).thenReturn(listOf(permission))
        `when`(permissionRepository.findBySnippetId(1)).thenReturn(listOf(permission2))

        assertThrows(Exception::class.java) {
            permissionService.addPermission(PermissionType.OWNER, 1, 2)
        }
    }


}
