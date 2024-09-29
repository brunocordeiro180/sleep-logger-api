package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.dto.ErrorResponseDTO
import com.noom.interview.fullstack.sleep.exception.UserException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class AdviceController {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<ErrorResponseDTO> {
        val listOfErrors = ArrayList<String>();
        ex.bindingResult.allErrors.mapNotNull { error ->
            listOfErrors.add(error.defaultMessage ?: "Invalid value")
        }
        val errorResponse = ErrorResponseDTO("Validation Error", listOfErrors)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun userException(ex: UserException) : ResponseEntity<ErrorResponseDTO> {
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}