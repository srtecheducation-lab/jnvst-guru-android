package com.jnvst.guru.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class StateDto(
    val id: Long,
    val code: String,
    val name: String
)
