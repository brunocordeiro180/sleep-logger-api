package com.noom.interview.fullstack.sleep.controller

import com.noom.interview.fullstack.sleep.dto.ErrorResponseDTO
import com.noom.interview.fullstack.sleep.exception.SleepLogException
import com.noom.interview.fullstack.sleep.exception.UserException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class AdviceController {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException) : ResponseEntity<ErrorResponseDTO> {
        logger.error("Method argument not valid: {}", ex.bindingResult.allErrors)
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
        logger.error("User exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SleepLogException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun sleepLogException(ex: SleepLogException) : ResponseEntity<ErrorResponseDTO> {
        logger.error("Sleep log exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun generalException(ex: Exception) : ResponseEntity<ErrorResponseDTO> {
        logger.error("General exception: {}", ex.message)
        val errorResponse = ErrorResponseDTO(ex.message, null)
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}