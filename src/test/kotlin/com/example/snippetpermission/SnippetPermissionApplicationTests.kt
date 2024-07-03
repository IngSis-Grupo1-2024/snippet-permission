package com.example.snippetpermission

import com.example.snippetpermission.domain.permission.model.dto.SnippetIds
import com.example.snippetpermission.domain.permission.model.input.PermissionIsAllowedInput
import com.example.snippetpermission.domain.permission.model.input.PermissionRequest
import com.example.snippetpermission.domain.permission.model.input.PermissionTypeInput
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
class SnippetPermissionApplicationTests {
    @MockBean
    private lateinit var permissionRepository: PermissionRepository

    private lateinit var permissionService: PermissionService

    @BeforeEach
    fun setUp() {
        permissionService = PermissionService(permissionRepository)
    }

    @Test
    fun `test addPermission`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))
        `when`(permissionRepository.findBySnippetId(1)).thenReturn(emptyList())
        `when`(permissionRepository.save(permission)).thenReturn(permission)

        assertEquals(PermissionType.OWNER, permissionService.getPermissionType(1, "1"))
    }

    @Test
    fun `test hasPermission`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))

        assertTrue(permissionService.hasPermission(PermissionType.OWNER, 1, "1"))
        assertFalse(permissionService.hasPermission(PermissionType.R, 1, "2"))
    }

    @Test
    fun `test isAllowed`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        `when`(permissionRepository.findBySnippetId(1)).thenReturn(listOf(permission))
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))

        permissionService.addPermission(PermissionType.R, 1, "2", "1")
    }

    @Test
    fun `test getPermissionType`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))

        assertEquals(PermissionType.OWNER, permissionService.getPermissionType(1, "1"))
    }

    @Test
    fun `test canShare`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))

        assertTrue(permissionService.canShare(1, "1"))
    }

    @Test
    fun `test reader cannot share`() {
        val permission = Permission(PermissionType.R, 1, "1")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))

        assertFalse(permissionService.canShare(1, "1"))
    }

    @Test
    fun `test there cannot be two owners`() {
        val permission = Permission(PermissionType.OWNER, 1, "1")
        val permission2 = Permission(PermissionType.OWNER, 1, "2")
        `when`(permissionRepository.findByUserIdAndSnippetId("1", 1)).thenReturn(listOf(permission))
        `when`(permissionRepository.findBySnippetId(1)).thenReturn(listOf(permission2))

        assertThrows(Exception::class.java) {
            permissionService.addPermission(PermissionType.OWNER, 1, "2")
        }
    }

    @Test
    fun `set and get permission type`() {
        val permission = Permission()
        permission.permissionType = PermissionType.OWNER
        assertEquals(PermissionType.OWNER, permission.permissionType)
    }

    @Test
    fun `set and get snippet id`() {
        val permission = Permission()
        permission.snippetId = 1
        assertEquals(1, permission.snippetId)
    }

    @Test
    fun `set and get user id`() {
        val permission = Permission()
        permission.userId = "1"
        assertEquals("1", permission.userId)
    }

    @Test
    fun `set and get snippets`() {
        val snippetIds = SnippetIds(listOf(1L, 2L, 3L))
        assertEquals(listOf(1L, 2L, 3L), snippetIds.snippets)
    }

    @Test
    fun `set and get properties`() {
        val input = PermissionIsAllowedInput(PermissionType.OWNER, 1L, "1")
        assertEquals(PermissionType.OWNER, input.permissionType)
        assertEquals(1L, input.snippetId)
        assertEquals("1", input.userId)
    }

    @Test
    fun `set, get properties and share`() {
        val request = PermissionRequest(PermissionType.OWNER, 1L, "1", "2")
        assertEquals(PermissionType.OWNER, request.permissionType)
        assertEquals(1L, request.snippetId)
        assertEquals("1", request.userId)
        assertEquals("2", request.sharerId)
    }

    @Test
    fun `set and get property`() {
        val input = PermissionTypeInput(1L, "1")
        assertEquals(1L, input.snippetId)
        assertEquals("1", input.userId)
    }

    @Test
    fun `id field is initially zero`() {
        val permission = Permission(PermissionType.OWNER, 1L, "1")

        assertEquals(0L, permission.id)
    }
}
