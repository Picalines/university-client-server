package com.kostenko.model

import jakarta.persistence.*

@Entity
data class FileEntity(
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) val id: Long,
    val name: String,
    val size: Long,
    @Lob val data: Array<Byte>
)