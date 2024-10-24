package com.kostenko.server

import jakarta.persistence.*

@Entity
@Table(name="todos")
data class Todo (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String,
    val done: Boolean
)
