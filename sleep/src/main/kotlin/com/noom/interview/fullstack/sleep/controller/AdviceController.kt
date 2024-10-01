package com.noom.interview.fullstack.sleep.controller

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.noom.interview.fullstack.sleep.dto.response.ErrorResponseDTO
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.exception.UserException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class AdviceController {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<ErrorResponseDTO> {
        logger.error("Method argument not valid: {}", ex.bindingResult.allErrors)
        val listOfErrors = ArrayList<String>();
        ex.bindingResult.allErrors.mapNotNull { error ->
            listOfErrors.add(error.defaultMessage ?: "Invalid value")
        }
        val errorResponse = ErrorResponseDTO("Validation Error", listOfErrors)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponseDTO> {
        logger.error("HttpMessageNotReadableException: {}", ex.message)
        val cause = ex.cause
        if (cause is MissingKotlinParameterException) {
            return handleMissingKotlinParameterException(cause)
        }
        val errorResponse = ErrorResponseDTO("Malformed JSON request.", null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponseDTO> {
        val errorMessage = "Invalid value for '${ex.name}'. Expected type: '${ex.requiredType?.simpleName}', but got: '${ex.value}'"
        val errorResponse = ErrorResponseDTO(errorMessage, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UserException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleUserException(ex: UserException) : ResponseEntity<ErrorResponseDTO> {
        logger.error("User exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SleepLogException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSleepLogException(ex: SleepLogException) : ResponseEntity<ErrorResponseDTO> {
        logger.error("Sleep log exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGeneralException(ex: Exception) : ResponseEntity<ErrorResponseDTO> {
        logger.error("General exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun handleMissingKotlinParameterException(cause: MissingKotlinParameterException): ResponseEntity<ErrorResponseDTO> {
        val missingField = cause.parameter.name ?: "unknown"
        val errorMessage = "Required field '$missingField' is missing or null."
        val errorResponse = ErrorResponseDTO(errorMessage, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}