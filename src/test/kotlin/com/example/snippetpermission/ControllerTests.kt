package com.example.snippetpermission

import com.example.snippetpermission.domain.permission.controller.PermissionController
import com.example.snippetpermission.domain.permission.model.dto.SnippetIds
import com.example.snippetpermission.domain.permission.model.input.PermissionIsAllowedInput
import com.example.snippetpermission.domain.permission.model.input.PermissionRequest
import com.example.snippetpermission.domain.permission.service.PermissionService
import com.example.snippetpermission.model.PermissionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt

class PermissionControllerTests {
    private lateinit var permissionController: PermissionController
    private val permissionService: PermissionService = Mockito.mock(PermissionService::class.java)

    @BeforeEach
    fun setup() {
        permissionController = PermissionController(permissionService)
    }

    @Test
    fun `add permission returns ok when successful`() {
        val permissionRequest = PermissionRequest(PermissionType.OWNER, 1L, "1", "2")
        Mockito.doNothing().`when`(permissionService).addPermission(
            permissionRequest.permissionType,
            permissionRequest.snippetId,
            permissionRequest.userId,
            permissionRequest.sharerId,
        )

        val response = permissionController.addPermission(permissionRequest)

        assertEquals(ResponseEntity.ok().build<String>(), response)
    }

    @Test
    fun `add permission returns not found when exception occurs`() {
        val permissionRequest = PermissionRequest(PermissionType.OWNER, 1L, "1", "2")
        `when`(
            permissionService.addPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
                permissionRequest.sharerId,
            ),
        ).thenThrow(RuntimeException("Error"))

        val response = permissionController.addPermission(permissionRequest)

        assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error"), response)
    }

    @Test
    fun `get shared snippets returns snippet ids`() {
        val jwt = Mockito.mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("1")
        `when`(permissionService.getSharedSnippets("1")).thenReturn(listOf(1L, 2L, 3L))

        val response = permissionController.getSharedSnippets(jwt)

        assertEquals(ResponseEntity.ok(SnippetIds(listOf(1L, 2L, 3L))), response)
    }

    @Test
    fun `is allowed returns true when permission exists`() {
        val permissionRequest = PermissionIsAllowedInput(PermissionType.OWNER, 1L, "1")
        `when`(
            permissionService.hasPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
            ),
        ).thenReturn(true)

        val response = permissionController.isAllowed(permissionRequest)

        assertEquals(ResponseEntity.ok(true), response)
    }

    @Test
    fun `is allowed returns false when permission does not exist`() {
        val permissionRequest = PermissionIsAllowedInput(PermissionType.OWNER, 1L, "1")
        `when`(
            permissionService.hasPermission(
                permissionRequest.permissionType,
                permissionRequest.snippetId,
                permissionRequest.userId,
            ),
        ).thenReturn(false)

        val response = permissionController.isAllowed(permissionRequest)

        assertEquals(ResponseEntity.ok(false), response)
    }

    @Test
    fun `get permission type returns permission type`() {
        `when`(permissionService.getPermissionType(1L, "1")).thenReturn(PermissionType.OWNER)

        val response = permissionController.getPermissionType("1", "1")

        assertEquals(ResponseEntity.ok(PermissionType.OWNER), response)
    }
}
