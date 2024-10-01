package com.noom.interview.fullstack.sleep.dto.response

data class ErrorResponseDTO(
    val error: String?,
    val messages: List<String>?
)