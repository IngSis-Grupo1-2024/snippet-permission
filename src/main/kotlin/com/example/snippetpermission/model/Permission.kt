package com.example.snippetpermission.model

import jakarta.persistence.*

@Entity
@Table(name = "Permissions")
data class Permission(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var permissionType: PermissionType,
    @Column(nullable = false)
    var snippetId: Int,
    @Column(nullable = false)
    var userId: Int,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    var id: Long = 0

    constructor() : this(PermissionType.R, 1, 1)
}

enum class PermissionType {
    OWNER,
    R,
}
