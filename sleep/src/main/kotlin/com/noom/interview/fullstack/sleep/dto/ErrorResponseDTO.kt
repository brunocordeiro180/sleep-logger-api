package com.noom.interview.fullstack.sleep.dto

data class ErrorResponseDTO(
    val error: String?,
    val messages: List<String>?
)